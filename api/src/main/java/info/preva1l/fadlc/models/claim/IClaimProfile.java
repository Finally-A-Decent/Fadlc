package info.preva1l.fadlc.models.claim;

import info.preva1l.fadlc.models.ChunkLoc;
import info.preva1l.fadlc.models.claim.settings.ProfileFlag;
import info.preva1l.fadlc.models.user.User;
import info.preva1l.fadlc.persistence.DatabaseObject;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IClaimProfile extends DatabaseObject {
    IClaim getParent();

    UUID getUniqueId();

    int getId();

    String getName();

    void setName(String name);

    Material getIcon();

    void setIcon(Material icon);

    Map<Integer, IProfileGroup> getGroups();

    Map<ProfileFlag, Boolean> getFlags();

    String getBorder();

    void setBorder(String border);

    IProfileGroup getPlayerGroup(User user);

    List<ChunkLoc> getClaimedChunks();

    List<User> getMembers();
}
