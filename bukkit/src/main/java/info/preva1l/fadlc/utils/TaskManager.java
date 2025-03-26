package info.preva1l.fadlc.utils;

import com.github.puregero.multilib.MultiLib;
import com.github.puregero.multilib.regionized.RegionizedTask;
import info.preva1l.fadlc.Fadlc;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Easy creation of Bukkit Tasks
 */
@UtilityClass
public class TaskManager {
    private final JavaPlugin plugin = Fadlc.i();

    /**
     * Run a synchronous task once. Helpful when needing to run some sync code in an async loop
     *
     * @param runnable The runnable, lambda supported yeh
     */
    public RegionizedTask runSync(Runnable runnable) {
        return MultiLib.getGlobalRegionScheduler().run(plugin, t -> runnable.run());
    }

    /**
     * Run a synchronous task once. Helpful when needing to run some sync code in an async loop
     *
     * @param runnable The runnable, lambda supported yeh
     */
    public RegionizedTask runSync(Entity entity, Runnable runnable) {
        return MultiLib.getEntityScheduler(entity).run(plugin, t -> runnable.run(), null);
    }

    /**
     * Run a synchronous task forever with a delay between runs.
     *
     * @param runnable The runnable, lambda supported yeh
     * @param interval Time between each run
     */
    public RegionizedTask runSyncRepeat(Runnable runnable, long interval) {
        return MultiLib.getGlobalRegionScheduler().runAtFixedRate(plugin, t -> runnable.run(), 0L, interval);
    }

    /**
     * Run a synchronous task once with a delay. Helpful when needing to run some sync code in an async loop
     *
     * @param runnable The runnable, lambda supported yeh
     * @param delay Time before running.
     */
    public RegionizedTask runSyncDelayed(Runnable runnable, long delay) {
        return MultiLib.getGlobalRegionScheduler().runDelayed(plugin, t -> runnable.run(), delay);
    }

    /**
     * Run an asynchronous task once. Helpful when needing to run some sync code in an async loop
     *
     * @param runnable The runnable, lambda supported yeh
     */
    public RegionizedTask runAsync(Runnable runnable) {
        return MultiLib.getAsyncScheduler().runNow(plugin, c -> runnable.run());
    }

    /**
     * Run an asynchronous task forever with a delay between runs.
     *
     * @param runnable The runnable, lambda supported yeh
     * @param interval Time between each run
     */
    public RegionizedTask runAsyncRepeat(Runnable runnable, long interval) {
        return MultiLib.getAsyncScheduler().runAtFixedRate(plugin, c -> runnable.run(), 0L, interval);
    }

    /**
     * Run an asynchronous task once with a delay. Helpful when needing to run some sync code in an async loop
     *
     * @param runnable The runnable, lambda supported yeh
     * @param delay Time before running.
     */
    public RegionizedTask runAsyncDelayed(Runnable runnable, long delay) {
        return MultiLib.getAsyncScheduler().runDelayed(plugin, c -> runnable.run(), delay);
    }
}