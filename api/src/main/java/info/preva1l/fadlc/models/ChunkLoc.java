package info.preva1l.fadlc.models;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class ChunkLoc {
    @Expose private final int x;
    @Expose private final int z;
    @Expose private final String world;
    @Expose private final String server;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ChunkLoc loc = (ChunkLoc) object;
        return x == loc.x && z == loc.z && Objects.equals(world, loc.world) && Objects.equals(server, loc.server);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z, world, server);
    }
}
