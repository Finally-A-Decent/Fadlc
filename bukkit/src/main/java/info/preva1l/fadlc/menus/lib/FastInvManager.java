package info.preva1l.fadlc.menus.lib;

import com.github.puregero.multilib.MultiLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manager for FastInv listeners.
 *
 * @author MrMicky
 */
public final class FastInvManager {
    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);

    private FastInvManager() {
        throw new UnsupportedOperationException();
    }

    /**
     * Register listeners for FastInv.
     *
     * @param plugin plugin to register
     * @throws NullPointerException  if plugin is null
     * @throws IllegalStateException if FastInv is already registered
     */
    public static void register(Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin");

        if (REGISTERED.getAndSet(true)) {
            throw new IllegalStateException("FastInv is already registered");
        }

        Bukkit.getPluginManager().registerEvents(new InventoryListener(plugin), plugin);
    }

    /**
     * Close all open FastInv inventories.
     */
    public static void closeAll(Plugin plugin) {
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getOpenInventory().getTopInventory().getHolder() instanceof FastInv)
                .forEach(p -> MultiLib.getEntityScheduler(p).execute(plugin, p::closeInventory, null, 0L));
    }

    public static final class InventoryListener implements Listener {
        private final Plugin plugin;

        public InventoryListener(Plugin plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if (e.getInventory().getHolder(false) instanceof FastInv<?> inv && e.getClickedInventory() != null) {
                boolean wasCancelled = e.isCancelled();
                e.setCancelled(true);

                inv.handleClick(e);

                // This prevents un-canceling the event if another plugin canceled it before
                if (!wasCancelled && !e.isCancelled()) {
                    e.setCancelled(false);
                }
            }
        }

        @EventHandler
        public void onInventoryDrag(InventoryDragEvent e) {
            if (e.getInventory().getHolder(false) instanceof FastInv<?> inv) {
                boolean wasCancelled = e.isCancelled();
                e.setCancelled(true);

                inv.handleDrag(e);

                // This prevents un-canceling the event if another plugin canceled it before
                if (!wasCancelled && !e.isCancelled()) {
                    e.setCancelled(false);
                }
            }
        }

        @EventHandler
        public void onInventoryOpen(InventoryOpenEvent e) {
            if (e.getInventory().getHolder(false) instanceof FastInv<?> inv) {
                inv.handleOpen(e);
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            if (e.getInventory().getHolder(false) instanceof FastInv<?> inv) {
                if (inv.handleClose(e)) {
                    Bukkit.getScheduler().runTask(this.plugin, () -> inv.open((Player) e.getPlayer()));
                }
            }
        }

        @EventHandler
        public void onPluginDisable(PluginDisableEvent e) {
            if (e.getPlugin() == this.plugin) {
                REGISTERED.set(false);
            }
        }
    }
}