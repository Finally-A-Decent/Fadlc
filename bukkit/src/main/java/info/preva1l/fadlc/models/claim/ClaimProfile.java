package info.preva1l.fadlc.models.claim;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.models.ChunkLoc;
import info.preva1l.fadlc.models.claim.settings.ProfileFlag;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.models.user.User;
import info.preva1l.fadlc.registry.ProfileFlagsRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@AllArgsConstructor
public class ClaimProfile implements IClaimProfile {
    private final UUID parentUUID;
    private final UUID uniqueId;
    private String name;
    private final int id;
    private Material icon;
    private final Map<Integer, IProfileGroup> groups;
    private final Map<ProfileFlag, Boolean> flags;
    private String border;

    private final Map<User, IProfileGroup> groupCache = new ConcurrentHashMap<>();

    public static ClaimProfile baseProfile(OnlineUser user, int id) {
        Map<ProfileFlag, Boolean> flags = new HashMap<>();
        for (ProfileFlag flag : ProfileFlagsRegistry.getAll()) {
            flags.put(flag, flag.isEnabledByDefault());
        }
        Map<Integer, IProfileGroup> groups = Map.of(
                1, ProfileGroup.rankOne(),
                2, ProfileGroup.rankTwo(),
                3, ProfileGroup.rankThree(),
                4, ProfileGroup.rankFour(),
                5, ProfileGroup.rankFive()
        );
        return new ClaimProfile(user.getUniqueId(), UUID.randomUUID(), "&7%s's Claim".formatted(user.getName()), id, getRandomMaterial(), groups, flags, "default"); // todo: config
    }

    private static Material getRandomMaterial() {
        for (int i = 0; i < 2; i++) {
            Material[] materials = Material.values();
            Material material = materials[Fadlc.i().getRandom().nextInt(materials.length)];
            if (material.isAir() || !material.isItem() || material.isLegacy()) {
                --i;
                continue;
            }
            return material;
        }
        return Material.BLACK_WOOL;
    }

    @Override
    public IClaim getParent() {
        return ClaimManager.getInstance().getByUUID(parentUUID);
    }

    @Override
    public void setName(String name) {
        this.name = name;
        getParent().updateProfile(this);
    }

    @Override
    public void setIcon(Material icon) {
        this.icon = icon;
        getParent().updateProfile(this);
    }

    @Override
    public void setBorder(String border) {
        this.border = border;
        getParent().updateProfile(this);
    }

    /**
     * Uses a dirty cache technique, its kinda goofy, but it works ong.
     * <p>
     *     If the user somehow ends up in more than 1 group it takes them out of the lowest priority.
     * </p>
     *
     * @param user the user to get
     * @return the group they are in
     */
    @Override
    public IProfileGroup getPlayerGroup(User user) {
        IProfileGroup cachedGroup = groupCache.get(user);
        if (cachedGroup != null) {
            return cachedGroup;
        }
        List<IProfileGroup> usersGroups = new ArrayList<>(groups.values().stream()
                .filter(g -> g.getUsers().contains(user))
                .sorted(Comparator.comparing(IProfileGroup::getId)).toList());

        IProfileGroup group = usersGroups.isEmpty() ? null : usersGroups.getLast();

        if (usersGroups.size() > 1) {
            while (usersGroups.size() > 1) {
                IProfileGroup g = usersGroups.removeFirst();
                groups.get(g.getId()).getUsers().remove(user);
            }
        }

        if (group == null) {
            return groups.get(1);
        }
        groupCache.put(user, group);
        return group;
    }

    @Override
    public List<ChunkLoc> getClaimedChunks() {
        List<ChunkLoc> chunks = new ArrayList<>();
        for (ChunkLoc chunk : getParent().getClaimedChunks().keySet()) {
            if (getParent().getClaimedChunks().get(chunk) != getId()) continue;
            chunks.add(chunk);
        }
        return chunks;
    }

    @Override
    public List<User> getMembers() {
        List<User> users = new ArrayList<>();
        for (IProfileGroup group : groups.values()) {
            users.addAll(group.getUsers());
        }
        return users;
    }
}
