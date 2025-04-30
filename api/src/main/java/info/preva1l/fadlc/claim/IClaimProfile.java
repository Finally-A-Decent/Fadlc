package info.preva1l.fadlc.claim;

import info.preva1l.fadlc.claim.settings.ProfileFlag;
import info.preva1l.fadlc.models.ChunkLoc;
import info.preva1l.fadlc.persistence.DatabaseObject;
import info.preva1l.fadlc.user.User;
import org.bukkit.Material;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@ApiStatus.NonExtendable
public interface IClaimProfile extends DatabaseObject {
    IClaim getParent();

    int getId();

    String getName();

    void setName(String name);

    Material getIcon();

    void setIcon(Material icon);

    Map<Integer, IProfileGroup> getGroups();

    Map<ProfileFlag, Boolean> getFlags();

    boolean getFlag(ProfileFlag flag);

    default boolean getFlag(Supplier<ProfileFlag> flag) {
        return getFlag(flag.get());
    }

    boolean setFlag(ProfileFlag flag, boolean value);

    default boolean setFlag(Supplier<ProfileFlag> flag, boolean value) {
        return setFlag(flag.get(), value);
    }

    String getBorder();

    void setBorder(String border);

    @NotNull IProfileGroup getPlayerGroup(User user);

    @Nullable IProfileGroup getPlayerGroup(User user, boolean useDefault);

    void setPlayerGroup(User user, int groupId);

    List<ChunkLoc> getClaimedChunks();

    List<User> getMembers();
}
