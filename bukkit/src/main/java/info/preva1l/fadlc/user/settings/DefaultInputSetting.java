package info.preva1l.fadlc.user.settings;

import info.preva1l.fadlc.config.menus.SettingsConfig;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.menus.lib.PaginatedMenu;
import info.preva1l.fadlc.models.Tuple;
import info.preva1l.fadlc.user.OnlineUser;
import info.preva1l.fadlc.utils.Text;
import org.bukkit.inventory.ItemStack;

/**
 * Created on 1/04/2025
 *
 * @author Preva1l
 */
public abstract class DefaultInputSetting<T> extends InputSetting<T> implements DefaultSetting<T> {
    protected DefaultInputSetting(T defaultValue) {
        super(defaultValue);
    }

    @Override
    public ItemStack getItem() {
        SettingsConfig.Lang.SettingInput config = SettingsConfig.i().getLang().getSettingInput();

        return new ItemBuilder(getLang().icon())
                .name(Text.text(config.name(), Tuple.of("%setting%", getName())))
                .lore(Text.list(config.description(),
                        Tuple.of("%current%", getStateAsString()),
                        Tuple.of("%description%", getDescription())
                ))
                .build();
    }

    @Override
    public void postChange(OnlineUser user, PaginatedMenu menu) {
        SettingsConfig.i().getLang().getSettingCycle().getSound().play(user);
    }
}
