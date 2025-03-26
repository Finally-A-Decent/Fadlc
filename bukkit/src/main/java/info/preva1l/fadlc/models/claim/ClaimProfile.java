package info.preva1l.fadlc.models.claim;

import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.models.ChunkLoc;
import info.preva1l.fadlc.models.claim.settings.ProfileFlag;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.models.user.User;
import info.preva1l.fadlc.registry.ProfileFlagsRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

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
        return new ClaimProfile(
                user.getUniqueId(),
                UUID.randomUUID(),
                Config.i().getProfileDefaults().getName().replace("%username%", user.getName()),
                id,
                getRandomMaterial(),
                groups,
                flags,
                Config.i().getProfileDefaults().getBorder()
        );
    }

    private static Material getRandomMaterial() {
        Material material;
        do {
            Material[] materials = Material.values();
            material = materials[ThreadLocalRandom.current().nextInt(materials.length)];
        } while (material == null
                || material.isAir()
                || !material.isItem()
                || material.isEmpty());
        return material;
    }

    @Override
    public IClaim getParent() {
        return ClaimManager.getInstance().getClaimByUUID(parentUUID);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setIcon(Material icon) {
        this.icon = icon;
    }

    @Override
    public void setBorder(String border) {
        this.border = border;
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
    public @NotNull IProfileGroup getPlayerGroup(User user) {
        return Objects.requireNonNull(getPlayerGroup(user, true));
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
    public @Nullable IProfileGroup getPlayerGroup(User user, boolean useDefault) {
        IProfileGroup cachedGroup = groupCache.get(user);
        if (cachedGroup != null) return cachedGroup;

        List<IProfileGroup> usersGroups = new ArrayList<>(
                groups.values().stream()
                        .filter(g -> g.getUsers().contains(user))
                        .sorted(Comparator.comparing(IProfileGroup::getId)).toList()
        );

        IProfileGroup group = usersGroups.isEmpty() ? null : usersGroups.getLast();

        while (usersGroups.size() > 1) {
            IProfileGroup g = usersGroups.removeFirst();
            groups.get(g.getId()).getUsers().remove(user);
        }

        if (group == null) {
            if (!useDefault) return null;
            return groups.get(1);
        }

        groupCache.put(user, group);
        return group;
    }

    @Override
    public void setPlayerGroup(User user, int groupId) {
        IProfileGroup oldGroup = getPlayerGroup(user, false);
        if (oldGroup != null) oldGroup.getUsers().remove(user);

        IProfileGroup group = groups.get(groupId);
        group.getUsers().add(user);
        groupCache.put(user, group);
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
