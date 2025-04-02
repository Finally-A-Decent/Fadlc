package info.preva1l.fadlc.models;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;

@Getter
@Setter
@ApiStatus.NonExtendable
public class Location {
    private int x;
    private int y;
    private int z;

    public Location(int x, int y, int z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
