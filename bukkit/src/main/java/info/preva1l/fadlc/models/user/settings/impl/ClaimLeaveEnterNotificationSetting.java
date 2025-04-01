package info.preva1l.fadlc.models.user.settings.impl;

import info.preva1l.fadlc.config.menus.SettingsConfig;
import info.preva1l.fadlc.models.user.settings.DefaultBooleanSetting;

public class ClaimLeaveEnterNotificationSetting extends DefaultBooleanSetting {
    public ClaimLeaveEnterNotificationSetting() {
        super(true);
    }

    @Override
    public SettingsConfig.Lang.Setting getLang() {
        return SettingsConfig.i().getLang().getSettings().getClaimLeaveEnterNotification();
    }
}
