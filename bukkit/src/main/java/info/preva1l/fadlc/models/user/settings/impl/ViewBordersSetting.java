package info.preva1l.fadlc.models.user.settings.impl;

import info.preva1l.fadlc.config.menus.SettingsConfig;
import info.preva1l.fadlc.config.sounds.Sounds;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.menus.lib.PaginatedMenu;
import info.preva1l.fadlc.models.Tuple;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.models.user.settings.Setting;
import info.preva1l.fadlc.utils.Text;
import info.preva1l.fadlc.utils.config.EasyItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewBordersSetting implements Setting<Boolean> {
    private Boolean state = true;

    @Override
    public Tuple<ItemStack, TriConsumer<InventoryClickEvent, OnlineUser, PaginatedMenu>> getItem() {
        SettingsConfig config = SettingsConfig.i();
        ItemBuilder itemStack = new ItemBuilder(config.getLang().getSettings().getViewBorders().icon());

        List<String> lore = new ArrayList<>();
        int i = 0;
        for (String line : config.getLang().getSettingToggle().description()) {
            if (line.contains("%description%")) {
                for (String description : config.getLang().getSettings().getViewBorders().description()) {
                    lore.add(i, description);
                    i++;
                }
                continue;
            }
            line = line.replace("%status%", getState()
                    ? config.getLang().getSettingToggle().enabled()
                    : config.getLang().getSettingToggle().disabled());
            lore.add(i, line);
            i++;
        }

        itemStack.name(Text.modernMessage(config.getLang().getSettingToggle().name()))
                .lore(Text.modernList(lore));

        return Tuple.of(new EasyItem(itemStack.build())
                .replaceAnywhere("%setting%", config.getLang().getSettings().getViewBorders().name())
                .getBase(), (e, user, menu) -> {
            user.updateSetting(!getState(), getClass());
            menu.openPage(menu.currentPage());
            Sounds.playSound((Player) e.getWhoClicked(), config.getLang().getSettingToggle().getSound());
        });
    }
}
