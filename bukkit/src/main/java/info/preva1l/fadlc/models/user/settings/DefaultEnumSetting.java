package info.preva1l.fadlc.models.user.settings;

import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.config.menus.SettingsConfig;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.menus.lib.PaginatedMenu;
import info.preva1l.fadlc.models.Tuple;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.models.user.settings.values.EnumSettingValue;
import info.preva1l.fadlc.utils.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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
        SettingsConfig config = SettingsConfig.i();
        ItemBuilder itemStack = new ItemBuilder(getLang().icon());

        String previous = getState().previous()
                .map(EnumSettingValue::formattedName)
                .orElse(Lang.i().getWords().getNone());
        String current = getState().formattedName();
        String next = getState().next()
                .map(EnumSettingValue::formattedName)
                .orElse(Lang.i().getWords().getNone());

        List<Component> lore = Text.list(
                config.getLang().getSettingCycle().description(),
                Tuple.of("%current%", current),
                Tuple.of("%next%", next),
                Tuple.of("%previous%", previous)
        );
        int i = 0;
        for (Component line : lore) {
            if (((TextComponent) line).content().contains("%description%")) {
                lore.addAll(++i, Text.list(getDescription()));
                break;
            }
        }

        itemStack.name(Text.text(
                        SettingsConfig.i().getLang().getSettingCycle().name(),
                        Tuple.of("%setting%", getName())))
                .lore(lore);

        return itemStack.build();
    }

    @Override
    public void postChange(OnlineUser user, PaginatedMenu menu) {
        SettingsConfig.i().getLang().getSettingCycle().getSound().play(user.asPlayer());
    }
}
