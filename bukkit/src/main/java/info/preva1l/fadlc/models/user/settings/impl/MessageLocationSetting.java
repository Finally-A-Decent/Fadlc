package info.preva1l.fadlc.models.user.settings.impl;

import info.preva1l.fadlc.config.menus.SettingsConfig;
import info.preva1l.fadlc.models.user.settings.DefaultEnumSetting;
import info.preva1l.fadlc.models.user.settings.values.MessageLocation;

public class MessageLocationSetting extends DefaultEnumSetting<MessageLocation> {
    public MessageLocationSetting() {
        super(MessageLocation.CHAT);
    }

    @Override
    public SettingsConfig.Lang.Setting getLang() {
        return SettingsConfig.i().getLang().getSettings().getMessageLocation();
    }
}
