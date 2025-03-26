package info.preva1l.fadlc.models;

import info.preva1l.fadlc.models.user.User;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public abstract class IPosition extends Location {
    protected IPosition(int x, int y, int z) {
        super(x, y, z);
    }

    public abstract String getServer();

    public abstract String getWorld();

    public abstract IClaimChunk getChunk();

    public ChunkLoc toChunkLoc() {
        return new ChunkLoc(getX() >> 4, getZ() >> 4, getWorld(), getServer());
    }

    public abstract void teleport(User user);
}
