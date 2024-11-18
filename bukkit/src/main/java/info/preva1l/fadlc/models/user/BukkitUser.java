package info.preva1l.fadlc.models.user;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.models.MessageLocation;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.utils.Text;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class BukkitUser implements OnlineUser, CommandUser {
    private final String name;
    private final UUID uniqueId;
    private Player player = null;
    private int availableChunks;
    private boolean viewBorders;
    private boolean showEnterMessages;
    private boolean showLeaveMessages;
    private MessageLocation messageLocation;
    private int claimWithProfileId;

    public BukkitUser(String name, UUID uniqueId, int availableChunks, boolean viewBorders, boolean showEnterMessages,
                      boolean showLeaveMessages, MessageLocation messageLocation, int claimWithProfileId) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.availableChunks = availableChunks;
        this.viewBorders = viewBorders;
        this.showEnterMessages = showEnterMessages;
        this.showLeaveMessages = showLeaveMessages;
        this.messageLocation = messageLocation;
        this.claimWithProfileId = claimWithProfileId;
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
    public void setViewBorders(boolean viewBorders) {
        this.viewBorders = viewBorders;
        UserManager.getInstance().cacheUser(this);
    }

    @Override
    public void setShowEnterMessage(boolean showEnterMessage) {
        this.showEnterMessages = showEnterMessage;
        UserManager.getInstance().cacheUser(this);
    }

    @Override
    public void setShowLeaveMessage(boolean showLeaveMessage) {
        this.showLeaveMessages = showLeaveMessage;
        UserManager.getInstance().cacheUser(this);
    }

    @Override
    public void setMessageLocation(MessageLocation newMessageLocation) {
        this.messageLocation = newMessageLocation;
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
    public void sendMessage(String message, boolean prefixed) {
        switch (messageLocation) {
            case CHAT -> getAudience().sendMessage(Text.modernMessage(Lang.i().getPrefix() + message));
            case HOTBAR -> getAudience().sendActionBar(Text.modernMessage(Lang.i().getPrefix() + message));
            case TITLE -> getAudience().sendTitlePart(TitlePart.SUBTITLE, Text.modernMessage(Lang.i().getPrefix() + message));
        }
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
