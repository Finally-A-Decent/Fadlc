package info.preva1l.fadlc.models;

import info.preva1l.fadlc.persistence.DatabaseObject;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface IClaimChunk extends DatabaseObject {
    ChunkLoc getLoc();

    int getChunkX();

    int getChunkZ();

    String getWorld();

    String getServer();

    ChunkStatus getStatus();

    int getProfileId();

    void setProfileId(int profileId);

    long getClaimedSince();

    void setClaimedSince(long claimedSince);
}
