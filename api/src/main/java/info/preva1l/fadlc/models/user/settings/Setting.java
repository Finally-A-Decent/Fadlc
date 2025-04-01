package info.preva1l.fadlc.models.user.settings;

import info.preva1l.fadlc.menus.lib.PaginatedMenu;
import info.preva1l.fadlc.models.user.OnlineUser;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Setting<C> {
    C getState();

    void setState(C state);

    default Class<C> getStateClass() {
        // noinspection unchecked
        return (Class<C>) getState().getClass();
    }

    String getName();

    List<String> getDescription();

    ItemStack getItem();

    TriConsumer<InventoryClickEvent, OnlineUser, PaginatedMenu> getHandler();

    void postChange(OnlineUser user, PaginatedMenu menu);
}