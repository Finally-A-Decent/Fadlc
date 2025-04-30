package info.preva1l.fadlc.user.settings;

import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.config.menus.SettingsConfig;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.menus.lib.PaginatedMenu;
import info.preva1l.fadlc.models.Tuple;
import info.preva1l.fadlc.user.OnlineUser;
import info.preva1l.fadlc.user.settings.values.EnumSettingValue;
import info.preva1l.fadlc.utils.Text;
import org.bukkit.inventory.ItemStack;

/**
 * Created on 1/04/2025
 *
 * @author Preva1l
 */
public abstract class DefaultEnumSetting<T extends EnumSettingValue<T>> extends EnumSetting<T> implements DefaultSetting<T> {
    protected DefaultEnumSetting(T defaultValue) {
        super(defaultValue);
    }

    @Override
    public ItemStack getItem() {
        SettingsConfig.Lang.SettingCycle config = SettingsConfig.i().getLang().getSettingCycle();

        String previous = getState().previous()
                .map(EnumSettingValue::formattedName)
                .orElse(Lang.i().getWords().getNone());
        String current = getState().formattedName();
        String next = getState().next()
                .map(EnumSettingValue::formattedName)
                .orElse(Lang.i().getWords().getNone());

        return new ItemBuilder(getLang().icon())
                .name(Text.text(config.name(), Tuple.of("%setting%", getName())))
                .lore(Text.list(config.description(),
                        Tuple.of("%current%", current),
                        Tuple.of("%next%", next),
                        Tuple.of("%previous%", previous),
                        Tuple.of("%description%", getDescription())
                ))
                .build();
    }

    @Override
    public void postChange(OnlineUser user, PaginatedMenu menu) {
        SettingsConfig.i().getLang().getSettingCycle().getSound().play(user);
    }
}
