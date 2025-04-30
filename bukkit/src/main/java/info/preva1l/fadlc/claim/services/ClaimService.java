package info.preva1l.fadlc.claim.services;

import info.preva1l.fadlc.claim.*;
import info.preva1l.fadlc.claim.registry.GroupSettingsRegistry;
import info.preva1l.fadlc.claim.registry.ProfileFlagsRegistry;
import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.config.ServerSettings;
import info.preva1l.fadlc.config.misc.PerformanceMode;
import info.preva1l.fadlc.models.ChunkLoc;
import info.preva1l.fadlc.models.ChunkStatus;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.persistence.DataService;
import info.preva1l.fadlc.user.OnlineUser;
import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;
import info.preva1l.trashcan.flavor.annotations.inject.Inject;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Service
public final class ClaimService implements IClaimService {
    @Getter public static final ClaimService instance = new ClaimService();

    private final Map<UUID, IClaim> claimCache = new ConcurrentHashMap<>();
    private final Map<ChunkLoc, IClaimChunk> chunkCache = new ConcurrentHashMap<>();

    @Inject private DataService dataService;

    @Configure
    public void configure() {
        dataService.getAll(IClaimChunk.class).join().forEach(this::cacheChunk);
        dataService.getAll(IClaim.class).join().forEach(this::updateClaim);

        loadGroupSettingsRegistry();
        loadProfileFlagsRegistry();
    }

    private void loadProfileFlagsRegistry() {
        Lang.ProfileFlags conf = Lang.i().getProfileFlags();
        Stream.of(
                conf.getExplosionDamage().create("explosion_damage"),
                conf.getEntityGriefing().create("entity_griefing"),
                conf.getPvp().create("pvp"),
                conf.getPassiveMobSpawn().create("passive_mob_spawn"),
                conf.getHostileMobSpawn().create("hostile_mob_spawn")
        ).forEach(ProfileFlagsRegistry::register);
    }

    private void loadGroupSettingsRegistry() {
        Lang.GroupSettings conf = Lang.i().getGroupSettings();
        Stream.of(
                conf.getBreakBlocks().create("break_blocks"),
                conf.getPlaceBlocks().create("place_blocks"),
                conf.getUseDoors().create("use_doors"),
                conf.getUseButtons().create("use_buttons"),
                conf.getEnter().create("enter")
        ).forEach(GroupSettingsRegistry::register);
    }

    // Claims

    public void updateClaim(IClaim claim) {
        claimCache.put(claim.getOwner().getUniqueId(), claim);
    }

    public IClaim getClaimByUUID(UUID uuid) {
        return claimCache.get(uuid);
    }

    @Override
    public Optional<IClaim> getClaimAt(IClaimChunk claimChunk) {
        return claimCache.values().stream().filter(claim -> claim.getClaimedChunks().containsKey(claimChunk.getLoc())).findFirst();
    }

    @Override
    public Optional<IClaim> getClaimAt(IPosition loc) {
        return claimCache.values().stream().filter(claim -> claim.getClaimedChunks().containsKey(loc.toChunkLoc())).findFirst();
    }

    @Override
    public @NotNull IClaim getClaimByOwner(OnlineUser user) {
        IClaim claim = getClaimByOwner(user.getUniqueId());
        if (claim == null) {
            Map<Integer, IClaimProfile> baseProfiles = new HashMap<>();
            baseProfiles.put(1, ClaimProfile.baseProfile(user, 1));
            claim = new Claim(user, new HashMap<>(), baseProfiles);
            updateClaim(claim);
        }
        return claim;
    }

    @Override
    public @Nullable IClaim getClaimByOwner(UUID user) {
        return claimCache.get(user);
    }

    @Override
    public List<IClaim> getAllClaims() {
        return new ArrayList<>(claimCache.values());
    }

    // Chunks

    public List<IClaimChunk> getClaimedChunks() {
        return new ArrayList<>(
                chunkCache.values()
                        .stream()
                        .filter(c -> c.getStatus() == ChunkStatus.CLAIMED
                                && c.getLoc().getServer().equals(ServerSettings.getInstance().getName())
                        ).toList()
        );
    }

    public void cacheChunk(IClaimChunk claimChunk) {
        if (!claimChunk.getServer().equals(ServerSettings.getInstance().getName())) return;
        chunkCache.put(claimChunk.getLoc(), claimChunk);
    }

    @Override
    public IClaimChunk getChunk(ChunkLoc loc) {
        IClaimChunk chunk = chunkCache.get(loc);

        if (chunk != null) return chunk;

        IClaimChunk newChunk = ClaimChunk.unclaimed(loc);
        if (Config.i().getOptimization().getPerformanceMode() != PerformanceMode.MEMORY) cacheChunk(newChunk);
        return newChunk;
    }

    @Override
    public IClaimChunk getChunkAt(IPosition loc) {
        return getChunk(loc.toChunkLoc());
    }

    @Override
    public IClaimChunk getChunkAt(int x, int z, String world) {
        return getChunkAt(x, z, world, ServerSettings.getInstance().getName());
    }

    @Override
    public IClaimChunk getChunkAt(int x, int z, String world, String server) {
        ChunkLoc chunkLoc = new ChunkLoc(x, z, world, server);
        return getChunk(chunkLoc);
    }
}
