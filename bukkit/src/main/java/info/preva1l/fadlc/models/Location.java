package info.preva1l.fadlc.models;

import lombok.Getter;

@Getter
public class Location implements ILocation {
    private final int x;
    private final int y;
    private final int z;

    public Location(int x, int y, int z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
