package info.preva1l.fadlc.models.user.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SettingHolder<C> {
    private C value;
    private Class<C> valueClass;
}
