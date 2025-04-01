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
public abstract class DefaultInputSetting<T> extends InputSetting<T> implements DefaultSetting<T> {
    protected DefaultInputSetting(T defaultValue) {
        super(defaultValue);
    }

    @Override
    public ItemStack getItem() {
        SettingsConfig config = SettingsConfig.i();
        ItemBuilder itemStack = new ItemBuilder(getLang().icon());

        String current = getStateAsString();

        List<Component> lore = Text.list(
                config.getLang().getSettingInput().description(),
                Tuple.of("%current%", current)
        );
        int i = 0;
        for (Component line : lore) {
            if (((TextComponent) line).content().contains("%description%")) {
                lore.addAll(++i, Text.list(getDescription()));
                break;
            }
        }

        itemStack.name(Text.text(
                        SettingsConfig.i().getLang().getSettingInput().name(),
                        Tuple.of("%setting%", getName())))
                .lore(lore);

        return itemStack.build();
    }

    @Override
    public void postChange(OnlineUser user, PaginatedMenu menu) {
        SettingsConfig.i().getLang().getSettingCycle().getSound().play(user.asPlayer());
    }
}
