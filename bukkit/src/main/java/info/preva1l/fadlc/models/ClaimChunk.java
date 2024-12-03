package info.preva1l.fadlc.models;

import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.models.claim.IClaim;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Optional;

@Getter
@Setter
public class ClaimChunk implements IClaimChunk {
    private final ChunkLoc loc;
    private long claimedSince; // -1 if not claimed
    private int profileId; // -1 if not claimed

    public ClaimChunk(ChunkLoc chunkLoc, long claimedSince, int profileId) {
        this.loc = chunkLoc;
        this.claimedSince = claimedSince;
        this.profileId = profileId;
    }

    @Override
    public int getChunkX() {
        return loc.getX();
    }

    @Override
    public int getChunkZ() {
        return loc.getZ();
    }

    @Override
    public String getWorldName() {
        return loc.getWorld();
    }

    @Override
    public String getServer() {
        return loc.getServer();
    }

    @Override
    public World getWorld() {
        return Bukkit.getWorld(loc.getWorld());
    }

    @Override
    public ChunkStatus getStatus() {
        Optional<IClaim> claim = ClaimManager.getInstance().getClaimAt(this);
        if (claim.isPresent()) {
            return ChunkStatus.CLAIMED;
        }

        // todo: check if world is restricted
        if (false) {
            return ChunkStatus.WORLD_DISABLED;
        }

        // todo: worldguard hook
        if (false) {
            return ChunkStatus.BLOCKED_WORLD_GUARD;
        }

        // todo: advanced server zones hook
        if (false) {
            return ChunkStatus.BLOCKED_ZONE_BORDER;
        }

        return ChunkStatus.CLAIMABLE;
    }
}
