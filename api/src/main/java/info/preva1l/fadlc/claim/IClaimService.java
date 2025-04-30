package info.preva1l.fadlc.claim;

import info.preva1l.fadlc.models.ChunkLoc;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.user.OnlineUser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApiStatus.NonExtendable
public interface IClaimService {
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
