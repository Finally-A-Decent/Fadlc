package info.preva1l.fadfc.models.claim;

import info.preva1l.fadfc.models.IClaimChunk;
import info.preva1l.fadfc.models.user.User;
import info.preva1l.fadfc.persistence.DatabaseObject;

import java.util.Map;
import java.util.Optional;

public interface IClaim extends DatabaseObject {
    User getOwner();

    Map<Integer, IClaimProfile> getProfiles();

    Optional<IClaimProfile> getProfile(IClaimChunk chunk);

    Map<IClaimChunk, Integer> getClaimedChunks();

    void claimChunk(IClaimChunk chunk);

    void setProfile(IClaimChunk chunk, int profile);
}
