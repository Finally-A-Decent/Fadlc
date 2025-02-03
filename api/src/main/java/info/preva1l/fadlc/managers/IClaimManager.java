package info.preva1l.fadlc.managers;

import info.preva1l.fadlc.models.ChunkLoc;
import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.user.OnlineUser;

import java.util.List;
import java.util.Optional;

public interface IClaimManager {
    Optional<IClaim> getClaimAt(IClaimChunk claimChunk);
    Optional<IClaim> getClaimAt(IPosition loc);
    IClaim getClaimByOwner(OnlineUser user);

    List<IClaim> getAllClaims();

    IClaimChunk getChunk(ChunkLoc chunkLoc);
    IClaimChunk getChunkAt(IPosition loc);
    IClaimChunk getChunkAt(int x, int z, String world);
}
