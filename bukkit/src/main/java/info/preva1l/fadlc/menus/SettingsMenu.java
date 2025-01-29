package info.preva1l.fadlc.menus;

import info.preva1l.fadlc.config.menus.SettingsConfig;
import info.preva1l.fadlc.config.sounds.Sounds;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.menus.lib.PaginatedFastInv;
import info.preva1l.fadlc.menus.lib.PaginatedMenu;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.models.user.settings.Setting;
import info.preva1l.fadlc.utils.FadlcExecutors;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;


public class SettingsMenu extends PaginatedFastInv<SettingsConfig> implements PaginatedMenu {
    private final OnlineUser user;

    public SettingsMenu(Player player) {
        super(SettingsConfig.i());
        this.user = UserManager.getInstance().getUser(player.getUniqueId()).orElseThrow();

        scheme.bindPagination('X');
        CompletableFuture.runAsync(this::buttons, FadlcExecutors.VIRTUAL_THREAD_POOL).thenRun(() -> this.open(player));
    }

    private void buttons() {
        fillPaginationItems();
        placeNavigationItems();
    }

    private void placeNavigationItems() {
        scheme.bindItem('B', config.getLang().getBack().itemStack(), e -> {
            Sounds.playSound((Player) e.getWhoClicked(), config.getLang().getBack().getSound());
            new ClaimMenu(user.asPlayer());
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

    @Override
    public void fillPaginationItems() {
        clearContent();
        for (Setting<?> setting : user.getSettings()) {
            addContent(setting.getItem().getFirst(), e -> setting.getItem().getSecond().accept(e, user, this));
        }
    }
}