package info.preva1l.fadlc.models.user.settings;

import info.preva1l.fadlc.config.menus.SettingsConfig;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.menus.lib.PaginatedMenu;
import info.preva1l.fadlc.models.Tuple;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.utils.Text;
import org.bukkit.inventory.ItemStack;

/**
 * Created on 1/04/2025
 *
 * @author Preva1l
 */
public abstract class DefaultBooleanSetting extends BooleanSetting implements DefaultSetting<Boolean> {
    protected DefaultBooleanSetting(boolean defaultValue) {
        super(defaultValue);
    }

    @Override
    public ItemStack getItem() {
        SettingsConfig.Lang.SettingToggle config = SettingsConfig.i().getLang().getSettingToggle();

        return new ItemBuilder(getLang().icon())
                .name(Text.text(config.name(), Tuple.of("%setting%", getName())))
                .lore(Text.list(config.description(),
                        Tuple.of("%status%", getState()
                                ? SettingsConfig.i().getLang().getSettingToggle().enabled()
                                : SettingsConfig.i().getLang().getSettingToggle().disabled()),
                        Tuple.of("%description%", getDescription())
                ))
                .build();
    }

    @Override
    public void postChange(OnlineUser user, PaginatedMenu menu) {
        SettingsConfig.i().getLang().getSettingToggle().getSound().play(user);
    }
}
