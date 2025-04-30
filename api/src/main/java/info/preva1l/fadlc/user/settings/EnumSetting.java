package info.preva1l.fadlc.user.settings;

import info.preva1l.fadlc.menus.lib.PaginatedMenu;
import info.preva1l.fadlc.user.OnlineUser;
import info.preva1l.fadlc.user.settings.values.EnumSettingValue;
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
public abstract class EnumSetting<T extends EnumSettingValue<T>> implements Setting<T> {
    private T state;

    public EnumSetting(T defaultValue) {
        this.state = defaultValue;
    }

    @Override
    public final TriConsumer<InventoryClickEvent, OnlineUser, PaginatedMenu> getHandler() {
        return (e, user, menu) -> {
            getState().previous().ifPresent(prev -> {
                if (e.getClick().isLeftClick()) {
                    setState(prev);
                }
            });
            getState().next().ifPresent(n -> {
                if (e.getClick().isRightClick()) {
                    setState(n);
                }
            });
            menu.openPage(menu.currentPage());
            postChange(user, menu);
        };
    }
}
