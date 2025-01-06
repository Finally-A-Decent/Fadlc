package info.preva1l.fadlc.models.user.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SettingHolder<T, C extends Setting<T>> {
    private final C value;

    public Class<C> getValueClass() {
        return (Class<C>) value.getClass();
    }

    public T getState() {
        return value.getState();
    }

    public void setState(T state) {
        value.setState(state);
    }
}
