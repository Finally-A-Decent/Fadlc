package info.preva1l.fadlc.menus;

import info.preva1l.fadlc.config.menus.SettingsConfig;
import info.preva1l.fadlc.menus.lib.PaginatedFastInv;

public class SettingsMenu extends PaginatedFastInv<SettingsConfig> {
    public SettingsMenu() {
        super(SettingsConfig.i());
    }
}
