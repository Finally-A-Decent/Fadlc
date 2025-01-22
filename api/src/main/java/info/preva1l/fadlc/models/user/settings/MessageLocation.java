package info.preva1l.fadlc.models.user.settings;

public enum MessageLocation {
    CHAT,
    HOTBAR,
    TITLE
    ;

    public String formattedName() {
        return name().toLowerCase().substring(0, 1).toUpperCase() + name().toLowerCase().substring(1);
    }

    public MessageLocation next() {
        int currentOrd = this.ordinal();
        if (currentOrd + 1 >= values().length) return null;
        return values()[currentOrd + 1];
    }

    public MessageLocation previous() {
        int currentOrd = this.ordinal();
        if (currentOrd - 1 < 0) return null;
        return values()[currentOrd - 1];
    }
}
