package info.preva1l.fadlc.menus;

import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.config.menus.ProfilesConfig;
import info.preva1l.fadlc.config.menus.SettingsConfig;
import info.preva1l.fadlc.config.particles.Particles;
import info.preva1l.fadlc.config.sounds.Sounds;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.menus.lib.PaginatedFastInv;
import info.preva1l.fadlc.models.ScrollableEnum;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.claim.settings.ProfileFlag;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.models.user.settings.MessageLocation;
import info.preva1l.fadlc.models.user.settings.Setting;
import info.preva1l.fadlc.models.user.settings.impl.ClaimEnterNotificationSetting;
import info.preva1l.fadlc.models.user.settings.impl.MessageLocationSetting;
import info.preva1l.fadlc.models.user.settings.impl.ViewBordersSetting;
import info.preva1l.fadlc.utils.Text;
import info.preva1l.fadlc.utils.config.EasyItem;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SettingsMenu extends PaginatedFastInv<SettingsConfig> {
    private final OnlineUser user;

    public SettingsMenu(Player player) {
        super(SettingsConfig.i());
        this.user = UserManager.getInstance().getUser(player.getUniqueId()).orElseThrow();

        scheme.bindPagination('X');
        fillPaginationItems();
        placeNavigationItems();
    }

    private void placeNavigationItems() {
        scheme.bindItem('B', config.getLang().getBack().itemStack(), e -> {
            Sounds.playSound((Player) e.getWhoClicked(), config.getLang().getBack().getSound());
            new ClaimMenu(user.asPlayer()).open(user.asPlayer());
        });
        scheme.bindItem('P', config.getLang().getPrevious().itemStack(), e -> {
            Sounds.playSound((Player) e.getWhoClicked(), config.getLang().getPrevious().getSound());
            openPrevious();
        });
        scheme.bindItem('N', config.getLang().getNext().itemStack(), e -> {
            Sounds.playSound((Player) e.getWhoClicked(), config.getLang().getNext().getSound());
            openNext();
        });
    }

    private void fillPaginationItems() {
        for (Setting<?> settingWildCard : user.getSettings()) {
            ItemBuilder itemStack = new ItemBuilder(settingWildCard.getIcon());
            switch (settingWildCard) {
                case MessageLocationSetting setting -> placeMessageLocation(itemStack, setting);
                case ViewBordersSetting setting -> placeViewBorders(itemStack, setting);
                case ClaimEnterNotificationSetting setting -> {
                    itemStack.name(Text.modernMessage(config.getLang().getSettingToggle().name()))
                            .lore(Text.modernList(config.getLang().getSettingToggle().description()));

                    addContent(new EasyItem(itemStack.build())
                            .replaceAnywhere("%status%", setting.getState()
                                    ? config.getLang().getSettingToggle().enabled()
                                    : config.getLang().getSettingToggle().disabled())
                            .getBase(), e -> {
                        user.updateSetting(!setting.getState(), setting.getClass());
                        openPage(currentPage());
                        Sounds.playSound((Player) e.getWhoClicked(), config.getLang().getSettingToggle().getSound());
                    });
                }
                default -> {
                    itemStack.name(Text.modernMessage(config.getLang().getSettingToggle().name()))
                            .lore(Text.modernList(config.getLang().getSettingToggle().description()));

                    addContent(new EasyItem(itemStack.build())
                            .replaceAnywhere("%status%", "Unknown")
                            .getBase(), e -> e.getWhoClicked().sendMessage(Component.text("Error")));
                }
            }
        }
    }

    private void placeMessageLocation(ItemBuilder itemStack, MessageLocationSetting setting) {
        ScrollableEnum previousLocation = setting.getState().previous();
        String previous = previousLocation == null
                ? Lang.i().getWords().getNone()
                : previousLocation.formattedName();
        String current = setting.getState().formattedName();
        ScrollableEnum nextLocation = setting.getState().next();
        String next = nextLocation == null
                ? Lang.i().getWords().getNone()
                : nextLocation.formattedName();

        List<String> lore = new ArrayList<>();
        int i = 0;
        for (String line : config.getLang().getSettingCycle().description()) {

            line = line.replace("%current%", current)
                    .replace("%next%", next)
                    .replace("%previous%", previous);
            lore.add(i, line);
            i++;
        }

        itemStack.name(Text.modernMessage(config.getLang().getSettingCycle().name()))
                .lore(Text.modernList(lore));

        addContent(itemStack.build(), e -> {
            user.updateSetting((MessageLocation) setting.getState().next(), MessageLocationSetting.class);
            openPage(currentPage());
            Sounds.playSound((Player) e.getWhoClicked(), config.getLang().getSettingCycle().getSound());
        });
    }

    private void placeViewBorders(ItemBuilder itemStack, ViewBordersSetting setting) {
        itemStack.name(Text.modernMessage(config.getLang().getSettingToggle().name()))
                .lore(Text.modernList(config.getLang().getSettingToggle().description()));

        addContent(new EasyItem(itemStack.build())
                .replaceAnywhere("%status%", setting.getState()
                        ? config.getLang().getSettingToggle().enabled()
                        : config.getLang().getSettingToggle().disabled())
                .getBase(), e -> {
            user.updateSetting(!setting.getState(), setting.getClass());
            openPage(currentPage());
            Sounds.playSound((Player) e.getWhoClicked(), config.getLang().getSettingToggle().getSound());
        });
    }

    private List<String> applyDescription(List<String> original, Setting<?> setting) {
        List<String> lore = new ArrayList<>();
        int i = 0;
        for (String line : config.getLang().getSettingCycle().description()) {
            if (line.contains("%description%")) {
                for (String description : setting.getDescription()) {
                    lore.add(i, description);
                    i++;
                }
                continue;
            }
            lore.add(i, line);
            i++;
        }
        return lore;
    }
}