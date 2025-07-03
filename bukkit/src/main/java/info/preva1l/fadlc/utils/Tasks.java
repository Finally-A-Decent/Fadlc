package info.preva1l.fadlc.utils;

import com.github.puregero.multilib.MultiLib;
import com.github.puregero.multilib.regionized.RegionizedTask;
import info.preva1l.fadlc.Fadlc;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

@UtilityClass
public class Tasks {
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
     * Run a synchronous task once. Helpful when needing to run some sync code in an async loop
     *
     * @param runnable The runnable, lambda supported yeh
     */
    public <E extends Entity> RegionizedTask runSync(E entity, Consumer<E> runnable) {
        return MultiLib.getEntityScheduler(entity).run(plugin, t -> runnable.accept(entity), null);
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
}