package info.preva1l.fadlc.models.user.settings;

import info.preva1l.fadlc.config.menus.SettingsConfig;

import java.util.List;

/**
 * Created on 1/04/2025
 *
 * @author Preva1l
 */
public interface DefaultSetting<T> extends Setting<T> {
    SettingsConfig.Lang.Setting getLang();

    @Override
    default String getName() {
        return getLang().name();
    }

    @Override
    default List<String> getDescription() {
        return getLang().description();
    }
}
