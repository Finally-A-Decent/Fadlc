package info.preva1l.fadlc.models.user.settings.impl;

import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.config.menus.SettingsConfig;
import info.preva1l.fadlc.config.sounds.Sounds;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.menus.lib.SettingsInventory;
import info.preva1l.fadlc.models.ScrollableEnum;
import info.preva1l.fadlc.models.Tuple;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.models.user.settings.MessageLocation;
import info.preva1l.fadlc.models.user.settings.Setting;
import info.preva1l.fadlc.utils.Text;
import info.preva1l.fadlc.utils.config.EasyItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
public class MessageLocationSetting implements Setting<MessageLocation> {
    private MessageLocation state;

    @Override
    public Tuple<ItemStack, TriConsumer<InventoryClickEvent, OnlineUser, SettingsInventory>> getItem() {
        SettingsConfig config = SettingsConfig.i();
        ItemBuilder itemStack = new ItemBuilder(config.getLang().getSettings().getMessageLocation().icon());

        ScrollableEnum previousLocation = getState().previous();
        String previous = previousLocation == null
                ? Lang.i().getWords().getNone()
                : previousLocation.formattedName();
        String current = getState().formattedName();
        ScrollableEnum nextLocation = getState().next();
        String next = nextLocation == null
                ? Lang.i().getWords().getNone()
                : nextLocation.formattedName();

        List<String> lore = new ArrayList<>();
        int i = 0;
        for (String line : config.getLang().getSettingCycle().description()) {
            if (line.contains("%description%")) {
                for (String description : config.getLang().getSettings().getMessageLocation().description()) {
                    lore.add(i, description);
                    i++;
                }
                continue;
            }
            line = line.replace("%current%", current)
                    .replace("%next%", next)
                    .replace("%previous%", previous);
            lore.add(i, line);
            i++;
        }

        itemStack.name(Text.modernMessage(config.getLang().getSettingCycle().name()))
                .lore(Text.modernList(lore));

        return Tuple.of(new EasyItem(itemStack.build())
                .replaceAnywhere("%setting%", config.getLang().getSettings().getViewBorders().name())
                .getBase(), (e, user, menu) -> {
            user.updateSetting((MessageLocation) getState().next(), getClass());
            menu.openPage(menu.currentPage());
            Sounds.playSound((Player) e.getWhoClicked(), config.getLang().getSettingCycle().getSound());
        });
    }
}
