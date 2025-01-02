package info.preva1l.fadlc.models.user;

import info.preva1l.fadlc.models.MessageLocation;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import org.bukkit.entity.Player;

public interface OnlineUser extends User {
    Player asPlayer();

    int getAvailableChunks();

    void setAvailableChunks(int newAmount);

    boolean isViewBorders();

    void setViewBorders(boolean viewBorders);

    boolean isShowEnterMessages();

    void setShowEnterMessage(boolean showEnterMessage);

    boolean isShowLeaveMessages();

    void setShowLeaveMessage(boolean showLeaveMessage);

    MessageLocation getMessageLocation();

    void setMessageLocation(MessageLocation newMessageLocation);

    IClaimProfile getClaimWithProfile();

    void setClaimWithProfile(IClaimProfile newProfile);

    IClaim getClaim();

    void sendMessage(String message);

    void sendMessage(String message, boolean prefixed);
}
