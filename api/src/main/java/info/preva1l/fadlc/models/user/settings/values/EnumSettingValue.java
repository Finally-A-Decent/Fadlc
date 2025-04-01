package info.preva1l.fadlc.models.user.settings.values;

import java.util.Optional;

/**
 * Created on 1/04/2025
 *
 * @author Preva1l
 */
public interface EnumSettingValue<T extends EnumSettingValue<T>> {
    String name();

    default String formattedName() {
        return name().toLowerCase().substring(0, 1).toUpperCase() + name().toLowerCase().substring(1);
    }

    Optional<T> next();

    Optional<T> previous();
}
