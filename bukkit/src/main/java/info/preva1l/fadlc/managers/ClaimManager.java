package info.preva1l.fadlc.managers;

import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.config.ServerSettings;
import info.preva1l.fadlc.config.misc.PerformanceMode;
import info.preva1l.fadlc.models.*;
import info.preva1l.fadlc.models.claim.Claim;
import info.preva1l.fadlc.models.claim.ClaimProfile;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.user.OnlineUser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClaimManager implements IClaimManager {
    private static ClaimManager instance;
    private final Map<UUID, IClaim> claimCache = new ConcurrentHashMap<>();
    private final Map<ChunkLoc, IClaimChunk> chunkCache = new ConcurrentHashMap<>();

    public static ClaimManager getInstance() {
        if (instance == null) {
            instance = new ClaimManager();
        }
        return instance;
    }

    public void updateClaim(IClaim claim) {
        claimCache.put(claim.getOwner().getUniqueId(), claim);
    }

    @Override
    public Optional<IClaim> getClaimAt(IClaimChunk claimChunk) {
        return claimCache.values().stream().filter(claim -> claim.getClaimedChunks().containsKey(claimChunk.getLoc())).findFirst();
    }

    @Override
    public Optional<IClaim> getClaimAt(ILoc loc) {
        return claimCache.values().stream().filter(claim -> claim.getClaimedChunks().containsKey(loc.getChunk().getLoc())).findFirst();
    }

    @Override
    public IClaim getClaimByOwner(OnlineUser user) {
        IClaim claim = claimCache.get(user.getUniqueId());
        if (claim == null) {
            Map<Integer, IClaimProfile> baseProfiles = new HashMap<>();
            baseProfiles.put(1, ClaimProfile.baseProfile(user, 1));
            claim = new Claim(user, new HashMap<>(), baseProfiles);
            updateClaim(claim);
        }
        return claim;
    }

    public IClaim getByUUID(UUID uuid) {
        return claimCache.get(uuid);
    }

    @Override
    public List<IClaim> getAllClaims() {
        return new ArrayList<>(claimCache.values());
    }

    public List<IClaimChunk> getClaimedChunks() {
        return new ArrayList<>(chunkCache.values().stream().filter(c -> c.getStatus() == ChunkStatus.CLAIMED).toList());
    }

    public void cacheChunk(IClaimChunk claimChunk) {
        if (!claimChunk.getServer().equals(ServerSettings.getInstance().getName())) return;
        chunkCache.put(claimChunk.getLoc(), claimChunk);
    }

    @Override
    public IClaimChunk getChunk(ChunkLoc loc) {
        return chunkCache.get(loc);
    }

    @Override
    public IClaimChunk getChunkAt(ILoc loc) {
        Optional<IClaimChunk> optChunk = chunkCache.values().stream().filter(chunk -> chunk.getChunkX() == loc.getX() >> 4
                && chunk.getChunkZ() == loc.getZ() >> 4 && loc.getWorld().equals(chunk.getWorldName())).findFirst();

        if (optChunk.isPresent()) {
            return optChunk.get();
        }

        IClaimChunk newChunk = new ClaimChunk(new ChunkLoc(loc.getX() >> 4, loc.getZ() >> 4, loc.getWorld(), ServerSettings.getInstance().getName()), -1, -1);
        if (Config.i().getPerformanceMode() != PerformanceMode.MEMORY) {
            cacheChunk(newChunk);
        }
        return newChunk;
    }

    public IClaimChunk getChunkAt(int x, int z, String world) {
        Optional<IClaimChunk> optChunk = chunkCache.values().stream().filter(chunk -> chunk.getChunkX() == x
                && chunk.getChunkZ() == z && world.equals(chunk.getWorldName())).findFirst();

        if (optChunk.isPresent()) {
            return optChunk.get();
        }

        IClaimChunk newChunk = new ClaimChunk(new ChunkLoc(x, z, world, ServerSettings.getInstance().getName()), -1, -1);
        if (Config.i().getPerformanceMode() != PerformanceMode.MEMORY) {
            cacheChunk(newChunk);
        }
        return newChunk;
    }
}
