package info.preva1l.fadlc.user.settings.impl;

import info.preva1l.fadlc.config.menus.SettingsConfig;
import info.preva1l.fadlc.user.settings.DefaultBooleanSetting;

public class ViewBordersSetting extends DefaultBooleanSetting {
    public ViewBordersSetting() {
        super(true);
    }

    @Override
    public SettingsConfig.Lang.Setting getLang() {
        return SettingsConfig.i().getLang().getSettings().getViewBorders();
    }
}
