package info.preva1l.fadlc.models.user.settings;

import info.preva1l.fadlc.config.menus.SettingsConfig;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.menus.lib.PaginatedMenu;
import info.preva1l.fadlc.models.Tuple;
import info.preva1l.fadlc.models.user.OnlineUser;
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
public abstract class DefaultBooleanSetting extends BooleanSetting implements DefaultSetting<Boolean> {
    protected DefaultBooleanSetting(boolean defaultValue) {
        super(defaultValue);
    }

    @Override
    public ItemStack getItem() {
        ItemBuilder itemStack = new ItemBuilder(getLang().icon());

        List<Component> lore = Text.list(
                SettingsConfig.i().getLang().getSettingToggle().description(),
                Tuple.of("%status%", getState()
                        ? SettingsConfig.i().getLang().getSettingToggle().enabled()
                        : SettingsConfig.i().getLang().getSettingToggle().disabled())
        );
        int i = 0;
        for (Component line : lore) {
            if (((TextComponent) line).content().contains("%description%")) {
                lore.addAll(++i, Text.list(getDescription()));
                break;
            }
        }

        itemStack.name(Text.text(
                        SettingsConfig.i().getLang().getSettingToggle().name(),
                        Tuple.of("%setting%", getName())))
                .lore(lore);

        return itemStack.build();
    }

    @Override
    public void postChange(OnlineUser user, PaginatedMenu menu) {
        SettingsConfig.i().getLang().getSettingToggle().getSound().play(user.asPlayer());
    }
}
