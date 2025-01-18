package info.preva1l.fadlc.models.user.settings;

import info.preva1l.fadlc.models.ScrollableEnum;

public enum MessageLocation implements ScrollableEnum {
    HOTBAR,
    TITLE,
    CHAT;

    @Override
    public ScrollableEnum next() {
        int currentOrd = this.ordinal();
        if (currentOrd + 1 >= values().length) return null;
        return values()[currentOrd + 1];
    }

    @Override
    public ScrollableEnum previous() {
        int currentOrd = this.ordinal();
        if (currentOrd - 1 < 0) return null;
        return values()[currentOrd - 1];
    }
}
