package info.preva1l.fadlc.listeners;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.managers.PersistenceManager;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.models.MessageLocation;
import info.preva1l.fadlc.models.user.BukkitUser;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.utils.Logger;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class PlayerListeners implements Listener {
    private final UserManager userManager;

    private final Map<UUID, BukkitTask> invalidateIfNoJoin = new HashMap<>();

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        if (!PersistenceManager.getInstance().isConnected()) {
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            Logger.severe("User tried to join before database was ready! (Blocked)");
        }

        invalidateIfNoJoin.put(e.getUniqueId(), Bukkit.getScheduler().runTaskLater(Fadlc.i(), () -> {
            leave(e.getUniqueId(), e.getName());
            invalidateIfNoJoin.remove(e.getUniqueId());
        }, 1200L));

        Optional<OnlineUser> user = PersistenceManager.getInstance().get(OnlineUser.class, e.getUniqueId()).join();
        OnlineUser onlineUser;

        if (user.isEmpty()) {
            onlineUser = new BukkitUser(e.getName(), e.getUniqueId(), 0, true,
                    true, true, MessageLocation.CHAT, 1); // todo: config first chunks
            PersistenceManager.getInstance().save(OnlineUser.class, onlineUser).join();
        } else {
            onlineUser = user.get();
        }

        userManager.cacheUser(onlineUser);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        BukkitTask task = invalidateIfNoJoin.remove(e.getPlayer().getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        userManager.getUser(e.getPlayer().getUniqueId()).ifPresent(user -> {
            PersistenceManager.getInstance().save(OnlineUser.class, user);
        });

        leave(e.getPlayer().getUniqueId(), e.getPlayer().getName());
    }

    private void leave(UUID uuid, String name) {
        userManager.invalidate(uuid);
        userManager.invalidate(name);
    }
}
