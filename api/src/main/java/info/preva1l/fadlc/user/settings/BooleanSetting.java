package info.preva1l.fadlc.user.settings;

import info.preva1l.fadlc.menus.lib.PaginatedMenu;
import info.preva1l.fadlc.user.OnlineUser;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created on 1/04/2025
 *
 * @author Preva1l
 */
@Getter
@Setter
public abstract class BooleanSetting implements Setting<Boolean> {
    private Boolean state;

    public BooleanSetting(boolean defaultState) {
        this.state = defaultState;
    }

    @Override
    public final TriConsumer<InventoryClickEvent, OnlineUser, PaginatedMenu> getHandler() {
        return (e, user, menu) -> {
            setState(!getState());
            menu.openPage(menu.currentPage());
            postChange(user, menu);
        };
    }
}
