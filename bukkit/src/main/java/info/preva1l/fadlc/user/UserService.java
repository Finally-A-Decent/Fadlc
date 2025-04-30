package info.preva1l.fadlc.user;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.persistence.DataService;
import info.preva1l.fadlc.user.registry.UserSettingsRegistry;
import info.preva1l.fadlc.user.settings.impl.ClaimLeaveEnterNotificationSetting;
import info.preva1l.fadlc.user.settings.impl.MessageLocationSetting;
import info.preva1l.fadlc.user.settings.impl.ViewBordersSetting;
import info.preva1l.fadlc.utils.UpdateService;
import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;
import info.preva1l.trashcan.flavor.annotations.inject.Inject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Service
public final class UserService implements IUserService, Listener {
    @Getter private static final UserService instance = new UserService();

    private final Map<String, OnlineUser> usersCacheName = new ConcurrentHashMap<>();
    private final Map<UUID, OnlineUser> usersCacheUuid = new ConcurrentHashMap<>();

    private final Map<UUID, BukkitTask> invalidateIfNoJoin = new HashMap<>();

    @Inject private Fadlc plugin;
    @Inject private Logger logger;
    @Inject private DataService dataService;

    @Configure
    public void configure() {
        UserSettingsRegistry.register(ViewBordersSetting.class, "view_borders");
        UserSettingsRegistry.register(MessageLocationSetting.class, "message_location");
        UserSettingsRegistry.register(ClaimLeaveEnterNotificationSetting.class, "claim_leave_enter_notification");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        if (!dataService.isConnected()) {
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            logger.severe("User tried to join before database was ready! (Blocked)");
        }

        invalidateIfNoJoin.put(e.getUniqueId(), Bukkit.getScheduler().runTaskLater(plugin, () -> {
            leave(e.getUniqueId(), e.getName());
            invalidateIfNoJoin.remove(e.getUniqueId());
        }, 1200L));

        Optional<OnlineUser> user = dataService.get(OnlineUser.class, e.getUniqueId()).join();
        OnlineUser onlineUser;

        if (user.isEmpty()) {
            onlineUser = new BukkitUser(
                    e.getName(),
                    e.getUniqueId(),
                    Config.i().getGeneral().getStartingChunks(),
                    1,
                    new ArrayList<>()
            );
            dataService.save(OnlineUser.class, onlineUser).join();
        } else {
            onlineUser = user.get();
        }

        cacheUser(onlineUser);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        BukkitTask task = invalidateIfNoJoin.remove(e.getPlayer().getUniqueId());
        if (task != null) {
            task.cancel();
        }

        UpdateService.instance.notifyUpdate(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        getUser(e.getPlayer().getUniqueId())
                .ifPresent(user -> dataService.save(OnlineUser.class, user));

        leave(e.getPlayer().getUniqueId(), e.getPlayer().getName());
    }

    private void leave(UUID uuid, String name) {
        invalidate(uuid);
        invalidate(name);
    }

    public void cacheUser(OnlineUser user) {
        usersCacheUuid.put(user.getUniqueId(), user);
        usersCacheName.put(user.getName(), user);
    }

    public void invalidate(UUID uuid) {
        usersCacheUuid.remove(uuid);
    }

    public void invalidate(String username) {
        usersCacheName.remove(username);
    }

    @Override
    public List<OnlineUser> getAllUsers() {
        return new ArrayList<>(usersCacheUuid.values());
    }

    @Override
    public Optional<OnlineUser> getUser(String name) {
        return Optional.ofNullable(usersCacheName.get(name));
    }

    @Override
    public Optional<OnlineUser> getUser(UUID uniqueId) {
        return Optional.ofNullable(usersCacheUuid.get(uniqueId));
    }

    @Override
    public Optional<OnlineUser> getUser(Player player) {
        return Optional.ofNullable(usersCacheUuid.get(player.getUniqueId()));
    }
}
