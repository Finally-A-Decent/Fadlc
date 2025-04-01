package info.preva1l.fadlc.models.user.settings.values;

import java.util.Optional;

public enum MessageLocation implements EnumSettingValue<MessageLocation> {
    CHAT,
    HOTBAR,
    TITLE
    ;

    @Override
    public Optional<MessageLocation> next() {
        int currentOrd = this.ordinal();
        if (currentOrd + 1 >= values().length) return Optional.empty();
        return Optional.ofNullable(values()[currentOrd + 1]);
    }

    @Override
    public Optional<MessageLocation> previous() {
        int currentOrd = this.ordinal();
        if (currentOrd - 1 < 0) return Optional.empty();
        return Optional.ofNullable(values()[currentOrd - 1]);
    }
}
