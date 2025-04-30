package info.preva1l.fadlc.user;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.claim.IClaim;
import info.preva1l.fadlc.claim.IClaimProfile;
import info.preva1l.fadlc.claim.IProfileGroup;
import info.preva1l.fadlc.claim.services.ClaimService;
import info.preva1l.fadlc.claim.settings.GroupSetting;
import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.config.ServerSettings;
import info.preva1l.fadlc.config.menus.SettingsConfig;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.models.Position;
import info.preva1l.fadlc.models.Replacer;
import info.preva1l.fadlc.user.registry.UserSettingsRegistry;
import info.preva1l.fadlc.user.settings.InputSetting;
import info.preva1l.fadlc.user.settings.Setting;
import info.preva1l.fadlc.user.settings.values.MessageLocation;
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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
        return ClaimService.getInstance().getClaimByOwner(this);
    }

    @Override
    public void setClaimWithProfile(IClaimProfile profile) {
        this.claimWithProfileId = profile.getId();
    }

    @Override
    public void sendMessage(String message, Replacer... replacements) {
        sendMessage(message, true, replacements);
    }

    @Override
    public void sendMessage(String message, boolean prefixed, Replacer... replacements) {
        sendMessage(Text.text(message, replacements), prefixed);
    }

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
        Setting<T> access = getSettingAccess(clazz, def);
        T t = access.getState();
        if (t == null) access.setState(def);
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
    public <T> void updateSetting(T object, Class<? extends Setting<T>> clazz) {
        getSettingAccess(clazz, object).setState(object);
    }

    @Override
    public boolean canPerformAction(IPosition location, Supplier<GroupSetting> setting) {
        return canPerformAction(location, setting.get());
    }

    @Override
    public boolean canPerformAction(IPosition location, GroupSetting setting) {
        Optional<IClaim> claimAtLocation = ClaimService.getInstance().getClaimAt(location);

        if (claimAtLocation.isEmpty()) return true;
        if (claimAtLocation.get().getOwner().equals(this)) return true;

        IProfileGroup group = claimAtLocation.get().getProfile(location.getChunk()).orElseThrow().getPlayerGroup(this);

        return group.getSettings().get(setting);
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
    public void requestInput(String prompt, @Nullable String placeholder, Consumer<@Nullable String> callback) {
        requestInput(prompt, placeholder, String.class, callback);
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
    public CompletableFuture<Boolean> teleport(IPosition position) {
        if (position.getServer().equals(ServerSettings.getInstance().getName())) {
            asPlayer().teleportAsync(
                    new Location(
                            Bukkit.getWorld(position.getWorld()),
                            position.getX(),
                            position.getY(),
                            position.getZ()),
                    PlayerTeleportEvent.TeleportCause.PLUGIN
            );
        }
        // todo: implement server transfer, probs have it in a pattern that allows people to override it, ex: huskhomes hook
        return null;
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
