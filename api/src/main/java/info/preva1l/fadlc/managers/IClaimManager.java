package info.preva1l.fadlc.managers;

import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.ILoc;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.user.OnlineUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IClaimManager {
    Optional<IClaim> getClaimAt(IClaimChunk claimChunk);
    Optional<IClaim> getClaimAt(ILoc loc);
    IClaim getClaimByOwner(OnlineUser user);

    List<IClaim> getAllClaims();

    IClaimChunk getChunk(UUID uniqueId);
    IClaimChunk getChunkAt(ILoc loc);
}
