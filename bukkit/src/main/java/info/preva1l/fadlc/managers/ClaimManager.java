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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClaimManager implements IClaimManager {
    @Getter private static final ClaimManager instance = new ClaimManager();

    private final Map<UUID, IClaim> claimCache = new ConcurrentHashMap<>();
    private final Map<ChunkLoc, IClaimChunk> chunkCache = new ConcurrentHashMap<>();

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
