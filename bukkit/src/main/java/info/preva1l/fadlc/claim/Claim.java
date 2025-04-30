package info.preva1l.fadlc.claim;

import info.preva1l.fadlc.api.events.ChunkClaimEvent;
import info.preva1l.fadlc.models.ChunkLoc;
import info.preva1l.fadlc.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
        var onlineUser = owner.getOnlineUser();
        if (onlineUser == null) throw new IllegalStateException("Cannot claim a chunk when the claim owner is not online!");

        var result = new ChunkClaimEvent(onlineUser.asPlayer(), this, claimChunk).callEvent();
        if (!result) return;

        onlineUser.setAvailableChunks(onlineUser.getAvailableChunks() - 1);

        claimChunk.setClaimedSince(System.currentTimeMillis());
        claimChunk.setProfileId(onlineUser.getClaimWithProfile().getId());
        claimedChunks.put(claimChunk.getLoc(), onlineUser.getClaimWithProfile().getId());
    }

    @Override
    public UUID getUniqueId() {
        return owner.getUniqueId();
    }
}
