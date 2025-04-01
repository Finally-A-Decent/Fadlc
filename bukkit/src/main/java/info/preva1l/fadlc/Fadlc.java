package info.preva1l.fadlc;

import info.preva1l.fadlc.api.FadlcAPI;
import info.preva1l.fadlc.api.ImplFadlcAPI;
import info.preva1l.fadlc.commands.CommandProvider;
import info.preva1l.fadlc.hooks.HookProvider;
import info.preva1l.fadlc.jobs.JobProvider;
import info.preva1l.fadlc.listeners.ClaimGroupSettingsListeners;
import info.preva1l.fadlc.listeners.PlayerListeners;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.managers.PersistenceManager;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.menus.lib.FastInvManager;
import info.preva1l.fadlc.persistence.DataProvider;
import info.preva1l.fadlc.registry.RegistryProvider;
import info.preva1l.fadlc.utils.Logger;
import info.preva1l.fadlc.utils.Text;
import info.preva1l.fadlc.utils.UpdatesProvider;
import info.preva1l.fadlc.utils.metrics.MetricsProvider;
import info.preva1l.hooker.Hooker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Stream;

public final class Fadlc extends JavaPlugin implements RegistryProvider, CommandProvider, MetricsProvider,
        DataProvider, JobProvider, UpdatesProvider, HookProvider {
    private static final String PURCHASER = "%%__USERNAME__%%";
    public static final @SuppressWarnings("ConstantValue") boolean VALID_PURCHASE = !PURCHASER.contains("__USERNAME__");

    private static Fadlc instance;

    @Override
    public void onLoad() {
        instance = this;
        loadHooks();
    }

    @Override
    public void onEnable() {
        loadRegistries();
        loadData();
        loadJobs();
        loadCommands();

        Logger.info("Registering Listeners...");
        Stream.of(
                new ClaimGroupSettingsListeners(this, UserManager.getInstance(), ClaimManager.getInstance()),
                new PlayerListeners(this, UserManager.getInstance(), PersistenceManager.getInstance())
        ).forEach(e -> getServer().getPluginManager().registerEvents(e, this));
        Logger.info("Listeners Registered!");

        FastInvManager.register(this);

        Logger.info("Loading API...");
        FadlcAPI.setInstance(new ImplFadlcAPI(this));
        Logger.info("API Loaded!");

        Hooker.enable();

        setupMetrics();

        Bukkit.getConsoleSender().sendMessage(Text.text("&2&l------------------------------"));
        Bukkit.getConsoleSender().sendMessage(Text.text("&a  Finally a Decent Land Claim"));
        Bukkit.getConsoleSender().sendMessage(Text.text("&a   has successfully started!"));
        Bukkit.getConsoleSender().sendMessage(Text.text("&2&l------------------------------"));

        Bukkit.getScheduler().runTaskLater(this, this::checkForUpdates, 60L);
    }

    @Override
    public void onDisable() {
        Hooker.disable();
        shutdownJobs();
        shutdownMetrics();
        shutdownData();
    }

    @Override
    public Fadlc getPlugin() {
        return this;
    }

    public static Fadlc i() {
        return instance;
    }
}
