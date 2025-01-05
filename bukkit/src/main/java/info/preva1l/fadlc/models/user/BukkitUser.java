package info.preva1l.fadlc.models.user;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.models.MessageLocation;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.user.settings.SettingHolder;
import info.preva1l.fadlc.utils.Text;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
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
    private final List<SettingHolder<?>> settings;

    public BukkitUser(String name, UUID uniqueId, int availableChunks,
                      int claimWithProfileId, List<SettingHolder<?>> settings) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.availableChunks = availableChunks;
        this.claimWithProfileId = claimWithProfileId;
        this.settings = settings;
    }

    @Override
    public @NotNull Audience getAudience() {
        return Fadlc.i().getAudiences().player(asPlayer());
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return asPlayer().hasPermission(permission);
    }

    @Override
    public Player asPlayer() {
        if (player == null) {
            player = Bukkit.getPlayer(uniqueId);
        }
        return player;
    }

    @Override
    public void setAvailableChunks(int newAmount) {
        this.availableChunks = newAmount;
        UserManager.getInstance().cacheUser(this);
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
    public void sendMessage(@NotNull String message) {
        sendMessage(message, true);
    }

    @Override
    public void setClaimWithProfile(IClaimProfile profile) {
        this.claimWithProfileId = profile.getId();
        UserManager.getInstance().cacheUser(this);
    }

    @Override
    public void sendMessage(@NotNull String message, boolean prefixed) {
        if (message.isEmpty()) return;
        switch (getSetting(MessageLocation.class)) {
            case CHAT -> getAudience().sendMessage(Text.modernMessage(Lang.i().getPrefix() + message));
            case HOTBAR -> getAudience().sendActionBar(Text.modernMessage(Lang.i().getPrefix() + message));
            case TITLE -> {
                getAudience().sendTitlePart(TitlePart.TITLE, Text.modernMessage(Lang.i().getPrefix()));
                getAudience().sendTitlePart(TitlePart.SUBTITLE, Text.modernMessage(message));
            }
        }
    }

    @Override
    public <T> T getSetting(Class<T> clazz) {
        return getSettingHolder(clazz).getValue();
    }

    @Override
    public <T> SettingHolder<T> getSettingHolder(Class<T> clazz) {
        return settings.stream()
                .filter(c -> c.getValueClass().equals(clazz))
                .findFirst()
                .map(holder -> (SettingHolder<T>) holder)
                .orElseThrow();
    }

    @Override
    public <T> void updateSetting(T object, Class<T> clazz) {
        getSettingHolder(clazz).setValue(object);
        UserManager.getInstance().cacheUser(this);
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
