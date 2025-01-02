package info.preva1l.fadlc.models.claim;

import info.preva1l.fadlc.api.events.ChunkClaimEvent;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.models.ChunkLoc;
import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class Claim implements IClaim {
    private final User owner;
    private final Map<ChunkLoc, Integer> claimedChunks;
    private final Map<Integer, IClaimProfile> profiles;

    @Override
    public Optional<IClaimProfile> getProfile(IClaimChunk chunk) {
        return Optional.ofNullable(profiles.get(claimedChunks.get(chunk.getLoc())));
    }

    @Override
    public void claimChunk(@NotNull IClaimChunk claimChunk) {
        claimChunk.setClaimedSince(System.currentTimeMillis());
        claimChunk.setProfileId(owner.getOnlineUser().getClaimWithProfile().getId());
        claimedChunks.put(claimChunk.getLoc(), owner.getOnlineUser().getClaimWithProfile().getId());
        ClaimManager.getInstance().cacheChunk(claimChunk);
        ClaimManager.getInstance().updateClaim(this);
        new ChunkClaimEvent(owner.getOnlineUser().asPlayer(), this, claimChunk).callEvent();
    }

    @Override
    public void updateProfile(IClaimProfile profile) {
        profiles.put(profile.getId(), profile);
        ClaimManager.getInstance().updateClaim(this);
    }
}
