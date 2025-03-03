package info.preva1l.fadlc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.NoPermissionMessageContext;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import info.preva1l.fadlc.api.FadlcAPI;
import info.preva1l.fadlc.api.ImplFadlcAPI;
import info.preva1l.fadlc.commands.ClaimCommand;
import info.preva1l.fadlc.commands.admin.FadlcCommand;
import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.config.particles.Particles;
import info.preva1l.fadlc.config.sounds.Sounds;
import info.preva1l.fadlc.jobs.ClaimBorderJob;
import info.preva1l.fadlc.jobs.SaveJobs;
import info.preva1l.fadlc.listeners.ClaimGroupSettingsListeners;
import info.preva1l.fadlc.listeners.PlayerListeners;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.managers.PersistenceManager;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.menus.lib.FastInvManager;
import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.user.BukkitUser;
import info.preva1l.fadlc.models.user.CommandUser;
import info.preva1l.fadlc.models.user.ConsoleUser;
import info.preva1l.fadlc.models.user.settings.Setting;
import info.preva1l.fadlc.persistence.gson.SettingSerializer;
import info.preva1l.fadlc.registry.RegistryProvider;
import info.preva1l.fadlc.utils.*;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.william278.desertwell.util.UpdateChecker;
import net.william278.desertwell.util.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public final class Fadlc extends JavaPlugin implements RegistryProvider {
    private static final String POLYMART_PURCHASE = "%%__USERNAME__%%";
    private static final String BBB_PURCHASE = "%%__USERNAME__%%";
    private static final int POLYMART_ID = 6616;
    private static final int METRICS_ID = 23412;
    private final Version pluginVersion = Version.fromString(getDescription().getVersion());

    private static Fadlc instance;
    @Getter private BukkitAudiences audiences;
    @Getter private Gson gson;
    @Getter private Random random;
    @Getter private BukkitCommandManager<CommandUser> commandManager;

    private ClaimBorderJob borderJob;
    private Metrics metrics;

    @Override
    public void onEnable() {
        audiences = BukkitAudiences.create(this);
        gson = new GsonBuilder().registerTypeHierarchyAdapter(Setting.class, new SettingSerializer()).create();
        random = new Random(System.currentTimeMillis());
        instance = this;

        loadRegistries();

        Logger.info("Initializing Managers...");
        FastInvManager.register(this);
        PersistenceManager.getInstance();
        ClaimManager.getInstance();
        UserManager.getInstance();
        commandManager = BukkitCommandManager.create(
                this,
                sender -> sender instanceof Player p ? (BukkitUser) UserManager.getInstance().getUser(p.getUniqueId()).orElseThrow() : new ConsoleUser(Fadlc.i().getAudiences().console()),
                new CommandUserValidator()
        );
        commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (user, context) -> user.sendMessage(Lang.i().getCommand().getUnknownArgs()));
        commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (user, context) -> user.sendMessage(Lang.i().getCommand().getUnknownArgs()));
        commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (user, context) -> user.sendMessage(Lang.i().getCommand().getUnknownArgs()));
        commandManager.registerMessage(MessageKey.of("NO_PERMISSION", NoPermissionMessageContext.class), (user, context) -> user.sendMessage(Lang.i().getCommand().getNoPermission()));
        commandManager.registerMessage(MessageKey.of("PLAYER_ONLY", DefaultMessageContext.class), (user, context) -> user.sendMessage(Lang.i().getCommand().getMustBePlayer()));
        Logger.info("Managers initialized!");

        Sounds.update();
        Particles.update();
        Skins.load();

        populateCaches();

        Logger.info("Registering Commands...");
        Stream.of(
                new ClaimCommand(),
                new FadlcCommand()
        ).forEach(commandManager::registerCommand);
        Logger.info("Commands Registered!");

        Logger.info("Registering Listeners...");
        Stream.of(
                new ClaimGroupSettingsListeners(ClaimManager.getInstance()),
                new PlayerListeners(UserManager.getInstance())
        ).forEach(e -> getServer().getPluginManager().registerEvents(e, this));
        Logger.info("Listeners Registered!");

        Logger.info("Starting Jobs...");
        SaveJobs.startAll();
        borderJob = new ClaimBorderJob();
        borderJob.start();
        Logger.info("Jobs Started!");

        Logger.info("Loading API...");
        FadlcAPI.setInstance(new ImplFadlcAPI());
        Logger.info("API Loaded!");

        setupMetrics();

        Bukkit.getConsoleSender().sendMessage(Text.legacyMessage("&2&l------------------------------"));
        Bukkit.getConsoleSender().sendMessage(Text.legacyMessage("&a  Finally a Decent Land Claim"));
        Bukkit.getConsoleSender().sendMessage(Text.legacyMessage("&a   has successfully started!"));
        Bukkit.getConsoleSender().sendMessage(Text.legacyMessage("&2&l------------------------------"));

        Bukkit.getScheduler().runTaskLater(this, this::checkForUpdates, 60L);
    }

    @Override
    public void onDisable() {
        Skins.save();
        SaveJobs.forceRunAll();
        SaveJobs.shutdownAll();
        if (borderJob != null) {
            borderJob.shutdown();
        }

        if (metrics != null) {
            metrics.shutdown();
        }
    }

    private void populateCaches() {
        Logger.info("Populating Caches...");
        List<IClaimChunk> chunks = PersistenceManager.getInstance().getAll(IClaimChunk.class).join();
        chunks.forEach(chunk -> ClaimManager.getInstance().cacheChunk(chunk));
        Logger.info("Chunk Cache Populated!");

        List<IClaim> claims = PersistenceManager.getInstance().getAll(IClaim.class).join();
        claims.forEach(claim -> ClaimManager.getInstance().updateClaim(claim));
        Logger.info("Claim Cache Populated!");
    }

    private void setupMetrics() {
        Logger.info("Starting Metrics...");

        metrics = new Metrics(this, METRICS_ID);
        metrics.addCustomChart(new Metrics.SingleLineChart("claims_created", () -> ClaimManager.getInstance().getAllClaims().size()));
        metrics.addCustomChart(new Metrics.SingleLineChart("chunks_claimed", () -> ClaimManager.getInstance().getClaimedChunks().size()));

        Logger.info("Metrics Logging Started!");
    }

    private void checkForUpdates() {
        final UpdateChecker checker = UpdateChecker.builder()
                .currentVersion(pluginVersion)
                .endpoint(UpdateChecker.Endpoint.POLYMART)
                .resource(Integer.toString(POLYMART_ID))
                .build();
        checker.check().thenAccept(checked -> {
            if (checked.isUpToDate()) {
                return;
            }
            Bukkit.getConsoleSender().sendMessage(Text.legacyMessage("&7[Fadlc]&f Fadlc is &#D63C3COUTDATED&f! " +
                    "&7Current: &#D63C3C%s &7Latest: &#18D53A%s".formatted(checked.getCurrentVersion(), checked.getLatestVersion())));
        });
    }

    public static Fadlc i() {
        return instance;
    }
}
