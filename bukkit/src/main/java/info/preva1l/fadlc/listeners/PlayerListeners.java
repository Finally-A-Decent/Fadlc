package info.preva1l.fadlc.listeners;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.managers.PersistenceManager;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.models.user.BukkitUser;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.models.user.settings.Setting;
import info.preva1l.fadlc.registry.UserSettingsRegistry;
import info.preva1l.fadlc.utils.Logger;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@AllArgsConstructor
public class PlayerListeners implements Listener {
    private final Fadlc plugin;
    private final UserManager userManager;
    private final PersistenceManager persistenceManager;

    private final Map<UUID, BukkitTask> invalidateIfNoJoin = new HashMap<>();

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        if (!persistenceManager.isConnected()) {
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            Logger.severe("User tried to join before database was ready! (Blocked)");
        }

        invalidateIfNoJoin.put(e.getUniqueId(), Bukkit.getScheduler().runTaskLater(plugin, () -> {
            leave(e.getUniqueId(), e.getName());
            invalidateIfNoJoin.remove(e.getUniqueId());
        }, 1200L));

        Optional<OnlineUser> user = persistenceManager.get(OnlineUser.class, e.getUniqueId()).join();
        OnlineUser onlineUser;

        if (user.isEmpty()) {
            onlineUser = new BukkitUser(
                    e.getName(),
                    e.getUniqueId(),
                    Config.i().getGeneral().getStartingChunks(),
                    1,
                    new ArrayList<>()
            );
            persistenceManager.save(OnlineUser.class, onlineUser).join();
        } else {
            onlineUser = user.get();
        }

        for (Class<? extends Setting<?>> setting : UserSettingsRegistry.getAll()) {
            onlineUser.putSettingIfEmpty(null, setting);
        }
        userManager.cacheUser(onlineUser);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        BukkitTask task = invalidateIfNoJoin.remove(e.getPlayer().getUniqueId());
        if (task != null) {
            task.cancel();
        }

        plugin.notifyUpdate(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        userManager.getUser(e.getPlayer().getUniqueId()).ifPresent(user -> {
            persistenceManager.save(OnlineUser.class, user);
        });

        leave(e.getPlayer().getUniqueId(), e.getPlayer().getName());
    }

    private void leave(UUID uuid, String name) {
        userManager.invalidate(uuid);
        userManager.invalidate(name);
    }
}
