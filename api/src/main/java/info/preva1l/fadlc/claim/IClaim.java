package info.preva1l.fadlc.claim;

import info.preva1l.fadlc.models.ChunkLoc;
import info.preva1l.fadlc.persistence.DatabaseObject;
import info.preva1l.fadlc.user.User;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;

@ApiStatus.NonExtendable
public interface IClaim extends DatabaseObject {
    User getOwner();

    Map<Integer, IClaimProfile> getProfiles();

    Optional<IClaimProfile> getProfile(IClaimChunk chunk);

    Map<ChunkLoc, Integer> getClaimedChunks();

    void claimChunk(IClaimChunk chunk);
}
