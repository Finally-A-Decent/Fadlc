package info.preva1l.fadlc.models;

import info.preva1l.fadlc.claim.IClaimChunk;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;

@Getter
@Setter
@ApiStatus.NonExtendable
public abstract class IPosition extends Location {
    private String server;
    private String world;

    protected IPosition(String server, String world, int x, int y, int z) {
        super(x, y, z);
        this.server = server;
        this.world = world;
    }

    public abstract IClaimChunk getChunk();

    public ChunkLoc toChunkLoc() {
        return new ChunkLoc(getX() >> 4, getZ() >> 4, getWorld(), getServer());
    }
}
