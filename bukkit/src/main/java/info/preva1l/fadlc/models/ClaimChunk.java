package info.preva1l.fadlc.models;

import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.models.claim.IClaim;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ClaimChunk implements IClaimChunk {
    private final UUID uniqueId;
    private final ChunkLoc loc;
    private long claimedSince; // -1 if not claimed
    private int profileId; // -1 if not claimed

    public static ClaimChunk unclaimed(ChunkLoc loc) {
        return new ClaimChunk(
                UUID.nameUUIDFromBytes(loc.toString().getBytes()),
                loc,
                -1,
                -1
        );
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

        if (Config.i().getGeneral().getDisabledWorlds().contains(loc.getWorld())) {
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
