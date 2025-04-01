package info.preva1l.fadlc.menus;

import info.preva1l.fadlc.config.menus.SettingsConfig;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.menus.lib.PaginatedFastInv;
import info.preva1l.fadlc.menus.lib.PaginatedMenu;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.models.user.settings.Setting;
import info.preva1l.fadlc.utils.FadlcExecutors;
import info.preva1l.fadlc.utils.TaskManager;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class SettingsMenu extends PaginatedFastInv<SettingsConfig> implements PaginatedMenu {
    private final OnlineUser user;

    public SettingsMenu(Player player) {
        super(SettingsConfig.i());
        this.user = UserManager.getInstance().getUser(player.getUniqueId()).orElseThrow();

        scheme.bindPagination('X');
        CompletableFuture.runAsync(this::buttons, FadlcExecutors.VIRTUAL_THREAD_PER_TASK)
                .thenRun(() -> TaskManager.runSync(player, () -> this.open(player)));
    }

    private void buttons() {
        fillPaginationItems();
        placeNavigationItems();
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