package info.preva1l.fadlc.models;

public interface ScrollableEnum {
    ScrollableEnum next();

    ScrollableEnum previous();

    String name();

    default String formattedName() {
        return name().toLowerCase().substring(0, 1).toUpperCase() + name().toLowerCase().substring(1);
    }
}
