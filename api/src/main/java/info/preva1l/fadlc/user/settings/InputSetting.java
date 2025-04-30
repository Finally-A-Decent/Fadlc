package info.preva1l.fadlc.user.settings;

import info.preva1l.fadlc.menus.lib.PaginatedMenu;
import info.preva1l.fadlc.user.OnlineUser;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 1/04/2025
 *
 * @author Preva1l
 */
@Getter
@Setter
public abstract class InputSetting<T> implements Setting<T> {
    private T state;

    public InputSetting(T defaultValue) {
        this.state = defaultValue;
    }

    public abstract String getStateAsString();

    public abstract @Nullable T parse(Class<T> type, String string);

    @Override
    public final TriConsumer<InventoryClickEvent, OnlineUser, PaginatedMenu> getHandler() {
        return (e, user, menu) -> {
            user.requestInput(this, this::setState);
            menu.openPage(menu.currentPage());
            postChange(user, menu);
        };
    }
}
