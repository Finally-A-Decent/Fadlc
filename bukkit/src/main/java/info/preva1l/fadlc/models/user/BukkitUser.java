package info.preva1l.fadlc.models.user;

import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.user.settings.MessageLocation;
import info.preva1l.fadlc.models.user.settings.Setting;
import info.preva1l.fadlc.registry.UserSettingsRegistry;
import info.preva1l.fadlc.utils.Text;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public class BukkitUser implements OnlineUser, CommandUser {
    private final String name;
    private final UUID uniqueId;
    private Player player = null;
    private int availableChunks;
    private int claimWithProfileId;
    private final List<Setting<?>> settings;

    public BukkitUser(String name, UUID uniqueId, int availableChunks,
                      int claimWithProfileId, List<Setting<?>> settings) {
        this.name = name;
        this.uniqueId = uniqueId;
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
            player = Bukkit.getPlayer(uniqueId);

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
        sendMessage(Text.modernMessage(message), prefixed);
    }

    @Override
    public void sendMessage(@NotNull Component component) {
        sendMessage(component, true);
    }

    @Override
    public void sendMessage(@NotNull Component component, boolean prefixed) {
        if (!(component instanceof TextComponent message)) return;
        if (message.content().isEmpty()) return;
        switch (getSetting(UserSettingsRegistry.MESSAGE_LOCATION.get(), MessageLocation.CHAT)) {
            case CHAT -> asPlayer().sendMessage(prefixed ? Text.modernMessage(Lang.i().getPrefix()).append(message) : message);
            case HOTBAR -> asPlayer().sendActionBar(prefixed ? Text.modernMessage(Lang.i().getPrefix()).append(message) : message);
            case TITLE -> {
                if (prefixed) asPlayer().sendTitlePart(TitlePart.TITLE, Text.modernMessage(Lang.i().getPrefix()));
                asPlayer().sendTitlePart(TitlePart.SUBTITLE, message);
            }
        }
    }

    @Override
    public <T> T getSetting(Class<? extends Setting<T>> clazz, T def) {
        T t = getSettingAccess(clazz).getState();
        if (t == null) t = updateSetting(def, clazz);
        return t;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Setting<T> getSettingAccess(Class<? extends Setting<T>> clazz) {
        return (Setting<T>) settings.stream()
                .filter(c -> c.getClass().equals(clazz))
                .findFirst().orElse(null);
    }

    @SneakyThrows
    @Override
    public <T> T updateSetting(T object, Class<? extends Setting<T>> clazz) {
        Setting<T> access = getSettingAccess(clazz);
        if (access == null) {
            access = clazz.getDeclaredConstructor(object.getClass()).newInstance(object);
            settings.add(access);
        }
        access.setState(object);
        return object;
    }

    @SneakyThrows
    @Override
    public void putSettingIfEmpty(Object object, Class<? extends Setting<?>> clazz) {
       Setting<?> access = settings.stream()
                .filter(c -> c.getClass().equals(clazz)).findFirst().orElse(null);
        if (access != null) return;
        if (object == null) {
            access = clazz.getDeclaredConstructor().newInstance();
        } else {
            access = clazz.getDeclaredConstructor(object.getClass()).newInstance(object);
        }
        settings.add(access);
    }

    @Override
    public int hashCode() {
        return uniqueId.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OfflineUser || o instanceof BukkitUser)) return false;
        User other = (User) o;
        return uniqueId.equals(other.getUniqueId());
    }

    @Override
    public OnlineUser getOnlineUser() {
        return this;
    }
}
