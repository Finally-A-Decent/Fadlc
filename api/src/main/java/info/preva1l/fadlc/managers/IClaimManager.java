package info.preva1l.fadlc.managers;

import info.preva1l.fadlc.models.ChunkLoc;
import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.user.OnlineUser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApiStatus.NonExtendable
public interface IClaimManager {
    Optional<IClaim> getClaimAt(IClaimChunk claimChunk);
    Optional<IClaim> getClaimAt(IPosition loc);
    @NotNull IClaim getClaimByOwner(OnlineUser user);
    @Nullable IClaim getClaimByOwner(UUID user);
    List<IClaim> getAllClaims();

    IClaimChunk getChunk(ChunkLoc chunkLoc);
    IClaimChunk getChunkAt(IPosition loc);
    IClaimChunk getChunkAt(int x, int z, String world);
    IClaimChunk getChunkAt(int x, int z, String world, String server);
}
