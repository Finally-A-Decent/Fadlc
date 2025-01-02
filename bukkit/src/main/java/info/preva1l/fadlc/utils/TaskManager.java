package info.preva1l.fadlc.utils;

import com.github.puregero.multilib.MultiLib;
import com.github.puregero.multilib.regionized.RegionizedTask;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Easy creation of Bukkit Tasks
 */
public class TaskManager {
    /**
     * Run a synchronous task once. Helpful when needing to run some sync code in an async loop
     * @param plugin The current plugin
     * @param runnable The runnable, lambda supported yeh
     */
    public static void runSync(JavaPlugin plugin, Runnable runnable) {
        plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    /**
     * Run a synchronous task forever with a delay between runs.
     * @param plugin The current plugin
     * @param runnable The runnable, lambda supported yeh
     * @param interval Time between each run
     */
    public static void runSyncRepeat(JavaPlugin plugin, Runnable runnable, long interval) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, 0L, interval);
    }
    /**
     * Run a synchronous task once with a delay. Helpful when needing to run some sync code in an async loop
     * @param plugin The current plugin
     * @param runnable The runnable, lambda supported yeh
     * @param delay Time before running.
     */
    public static void runSyncDelayed(JavaPlugin plugin, Runnable runnable, long delay) {
        plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
    }

    /**
     * Run an asynchronous task once. Helpful when needing to run some sync code in an async loop
     * @param plugin The current plugin
     * @param runnable The runnable, lambda supported yeh
     */
    public static RegionizedTask runAsync(JavaPlugin plugin, Runnable runnable) {
        return MultiLib.getAsyncScheduler().runNow(plugin, c -> runnable.run());
    }

    /**
     * Run an asynchronous task forever with a delay between runs.
     * @param plugin The current plugin
     * @param runnable The runnable, lambda supported yeh
     * @param interval Time between each run
     */
    public static RegionizedTask runAsyncRepeat(JavaPlugin plugin, Runnable runnable, long interval) {
        return MultiLib.getAsyncScheduler().runAtFixedRate(plugin, c -> runnable.run(), 0L, interval);
    }

    /**
     * Run an asynchronous task once with a delay. Helpful when needing to run some sync code in an async loop
     * @param plugin The current plugin
     * @param runnable The runnable, lambda supported yeh
     * @param delay Time before running.
     */
    public static RegionizedTask runAsyncDelayed(JavaPlugin plugin, Runnable runnable, long delay) {
        return MultiLib.getAsyncScheduler().runDelayed(plugin, c -> runnable.run(), delay);
    }
}