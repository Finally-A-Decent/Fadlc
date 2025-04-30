package info.preva1l.fadlc.menus;

import info.preva1l.fadlc.config.menus.SettingsConfig;
import info.preva1l.fadlc.menus.lib.PaginatedFastInv;
import info.preva1l.fadlc.menus.lib.PaginatedMenu;
import info.preva1l.fadlc.user.settings.Setting;
import org.bukkit.entity.Player;

public class SettingsMenu extends PaginatedFastInv<SettingsConfig> implements PaginatedMenu {
    public SettingsMenu(Player player) {
        super(player, SettingsConfig.i());
    }

    @Override
    protected void placeNavigationItems() {
        super.placeNavigationItems();

        scheme.bindItem('B', config.getLang().getBack().itemStack(), e -> {
            config.getLang().getBack().getSound().play((Player) e.getWhoClicked());
            new ClaimMenu(user.asPlayer());
        });
    }

    @Override
    public void fillPaginationItems() {
        clearContent();
        for (Setting<?> setting : user.getSettings()) {
            addContent(setting.getItem(), e -> setting.getHandler().accept(e, user, this));
        }
    }
}