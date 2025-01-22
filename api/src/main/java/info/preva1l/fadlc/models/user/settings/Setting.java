package info.preva1l.fadlc.models.user.settings;

import info.preva1l.fadlc.menus.lib.SettingsInventory;
import info.preva1l.fadlc.models.Tuple;
import info.preva1l.fadlc.models.user.OnlineUser;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface Setting<C> {
    C getState();

    void setState(C state);


    Tuple<ItemStack, TriConsumer<InventoryClickEvent, OnlineUser, SettingsInventory>> getItem();
}