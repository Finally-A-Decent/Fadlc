package info.preva1l.fadlc.models.user;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.config.menus.SettingsConfig;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.models.Position;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.user.settings.InputSetting;
import info.preva1l.fadlc.models.user.settings.Setting;
import info.preva1l.fadlc.models.user.settings.values.MessageLocation;
import info.preva1l.fadlc.registry.UserSettingsRegistry;
import info.preva1l.fadlc.utils.Logger;
import info.preva1l.fadlc.utils.Text;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.title.TitlePart;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Getter
public final class BukkitUser extends OfflineUser implements OnlineUser, CommandUser {
    private Player player = null;
    private int availableChunks;
    private int claimWithProfileId;
    private final List<Setting<?>> settings;

    public BukkitUser(String name, UUID uniqueId, int availableChunks,
                      int claimWithProfileId, List<Setting<?>> settings) {
        super(uniqueId, name);
        this.availableChunks = availableChunks;
        this.claimWithProfileId = claimWithProfileId;
        this.settings = settings;
    }

    @Override
    public @NotNull Audience getAudience() {
        return asPlayer();
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return asPlayer().hasPermission(permission);
    }

    @Override
    public Player asPlayer() {
        if (player == null) {
            player = Bukkit.getPlayer(getUniqueId());

            if (player == null) throw new IllegalStateException("You cannot access a stale OnlineUser instance!");
        }
        return player;
    }

    @Override
    public void setAvailableChunks(int newAmount) {
        this.availableChunks = newAmount;
    }

    @Override
    public IClaimProfile getClaimWithProfile() {
        return getClaim().getProfiles().get(getClaimWithProfileId());
    }

    @Override
    public IClaim getClaim() {
        return ClaimManager.getInstance().getClaimByOwner(this);
    }

    @Override
    public void setClaimWithProfile(IClaimProfile profile) {
        this.claimWithProfileId = profile.getId();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        sendMessage(message, true);
    }

    @Override
    public void sendMessage(@NotNull String message, boolean prefixed) {
        sendMessage(Text.text(message), prefixed);
    }

    @Override
    public void sendMessage(@NotNull Component component) {
        sendMessage(component, true);
    }

    @Override
    public void sendMessage(@NotNull Component component, boolean prefixed) {
        if (!(component instanceof TextComponent message)) return;
        if (message.content().isEmpty()) return;
        switch (getSetting(UserSettingsRegistry.MESSAGE_LOCATION, MessageLocation.CHAT)) {
            case CHAT -> asPlayer().sendMessage(prefixed ? Text.text(Lang.i().getPrefix()).append(message) : message);
            case HOTBAR -> asPlayer().sendActionBar(prefixed ? Text.text(Lang.i().getPrefix()).append(message) : message);
            case TITLE -> {
                if (prefixed) asPlayer().sendTitlePart(TitlePart.TITLE, Text.text(Lang.i().getPrefix()));
                asPlayer().sendTitlePart(TitlePart.SUBTITLE, message);
            }
        }
    }

    @Override
    public <T> T getSetting(Class<? extends Setting<T>> clazz, T def) {
        T t = getSettingAccess(clazz, def).getState();
        if (t == null) t = updateSetting(def, clazz);
        return t;
    }

    @Override
    public <T> Optional<Setting<T>> getSettingAccess(Class<? extends Setting<T>> clazz) {
        return settings.stream()
                .filter(clazz::isInstance)
                .findFirst()
                .map(clazz::cast);
    }

    public <T> Setting<T> getSettingAccess(Class<? extends Setting<T>> clazz, T def) {
        return getSettingAccess(clazz).orElseGet(() -> {
            var constructor = tryGetConstructor(def, clazz);
            if (constructor == null) {
                RuntimeException exp = new RuntimeException("Could not find constructor for " + clazz.getName());
                Logger.severe("Could not find constructor for " + clazz.getName(), exp);
                throw exp;
            }
            return tryInstantiate(def, constructor);
        });
    }

    @Override
    public <T> T updateSetting(T object, Class<? extends Setting<T>> clazz) {
        getSettingAccess(clazz, object).setState(object);
        return object;
    }

    @Override
    public <T> void requestInput(InputSetting<T> setting, Consumer<T> callback) {
        requestInput(SettingsConfig.i().getLang().getSettingInput().prompt(),
                null, setting::parse, setting.getStateClass(),
                input -> {
                    if (input == null) {
                        sendMessage(SettingsConfig.i().getLang().getSettingInput().invalid());
                        return;
                    }
                    callback.accept(input);
                }
        );
    }

    @Override
    public <T> void requestInput(String prompt, @Nullable String placeholder, Class<T> type, Consumer<@Nullable T> callback) {
        requestInput(prompt, placeholder, this::parseInput, type, callback);
    }

    private <T> void requestInput(String prompt, @Nullable String placeholder, BiFunction<Class<T>, String, T> parser, Class<T> type, Consumer<@Nullable T> callback) {
        new AnvilGUI.Builder().plugin(Fadlc.i())
                .jsonTitle(JSONComponentSerializer.json().serialize(Text.text(prompt)))
                .text(placeholder == null ? "" : placeholder)
                .onClick((slot, state) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) return Collections.emptyList();
                    return Collections.singletonList(AnvilGUI.ResponseAction.run(() -> callback.accept(parser.apply(type, state.getText()))));
                }).onClose((state) -> callback.accept(parser.apply(type, state.getText()))).open(player);
    }

    private <T> T parseInput(Class<T> type, String input) {
        if (input.isEmpty()) return null;

        if (type == String.class) return type.cast(input);

        try {
            Number value = Double.valueOf(input);
            if (type == Double.class) {
                return type.cast(value.doubleValue());
            } else if (type == Float.class) {
                return type.cast(value.floatValue());
            } else if (type == Integer.class) {
                return type.cast(value.intValue());
            }
        } catch (NumberFormatException ignored) {
            return null;
        }

        throw new IllegalArgumentException("Unsupported return type: " + type.getSimpleName());
    }

    @Override
    public IPosition getPosition() {
        return Position.fromBukkit(getPlayer().getLocation());
    }

    @Override
    public OnlineUser getOnlineUser() {
        return this;
    }

    private <T> Setting<T> tryInstantiate(T object, Constructor<? extends Setting<T>> constructor) {
        try {
            Setting<T> instance = constructor.newInstance(object);
            settings.add(instance);
            return instance;
        } catch (IllegalArgumentException ignored) {
            try {
                Setting<T> instance = constructor.newInstance();
                settings.add(instance);
                return instance;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Constructor<? extends Setting<T>> tryGetConstructor(T object, Class<? extends Setting<T>> clazz) {
        try {
            return clazz.getDeclaredConstructor(object.getClass());
        } catch (NoSuchMethodException ignored) {
            try {
                return clazz.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
    }
}
