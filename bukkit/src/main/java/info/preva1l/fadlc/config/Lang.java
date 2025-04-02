package info.preva1l.fadlc.config;

import de.exlll.configlib.Configuration;
import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.utils.Logger;
import info.preva1l.fadlc.utils.Text;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("FieldMayBeFinal")
public class Lang {
    private static Lang instance;

    private static final String FILE_NAME = "lang.yml";
    private static final String CONFIG_HEADER = """
            ##########################################
            #                  Fadlc                 #
            #     Language/Message Configuration     #
            ##########################################
            """;

    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .header(CONFIG_HEADER).build();


    private String prefix = "&#9555ff&lFADLC &8&l\u00bb &r";

    private ClaimMessages claimMessages = new ClaimMessages();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ClaimMessages {
        private String enter = "&fYou have &aentered &e%player%'s&f claim!";
        private String leave = "&fYou have &cleft &e%player%'s&f claim!";

        private Fail fail = new Fail();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Fail {
            private String notEnoughChunks = "&fYou do not have enough claim chunks!";
        }
    }

    private Command command = new Command();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Command {
        private String noPermission = "&cInsufficient permission.";
        private String unknownArgs = "&cUnknown arguments.";
        private String mustBePlayer = "&cYou must be a player to run this command.";
    }

    private GroupSettings groupSettings = new GroupSettings();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GroupSettings {
        private Enter enter = new Enter();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Enter {
            private String name = "Enter Claim";
            private List<String> description = List.of("Whether or not to allow", "players to enter your claim.");
            private String message = "&cYou cannot enter &e%player%'s&c claim!";
        }

        private BreakBlocks breakBlocks = new BreakBlocks();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class BreakBlocks {
            private String name = "Break Blocks";
            private List<String> description = List.of("Whether or not to allow", "players to break blocks.");
            private String message = "&cYou cannot break blocks in &e%player%'s&c claim!";
        }

        private PlaceBlocks placeBlocks = new PlaceBlocks();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class PlaceBlocks {
            private String name = "Place Blocks";
            private List<String> description = List.of("Whether or not to allow", "players to place blocks.");
            private String message = "&cYou cannot place blocks in &e%player%'s&c claim!";
        }

        private UseDoors useDoors = new UseDoors();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class UseDoors {
            private String name = "Use Doors";
            private List<String> description = List.of("Whether or not to allow", "players to use doors.");
            private String message = "&cYou cannot use doors in &e%player%'s&c claim!";
        }

        private UseButtons useButtons = new UseButtons();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class UseButtons {
            private String name = "Use Buttons";
            private List<String> description = List.of("Whether or not to allow", "players to use buttons.");
            private String message = "&cYou cannot use buttons in &e%player%'s&c claim!";
        }
    }

    private ProfileFlags profileFlags = new ProfileFlags();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ProfileFlags {
        private EntityGriefing entityGriefing = new EntityGriefing();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class EntityGriefing {
            private Material icon = Material.CREEPER_SPAWN_EGG;
            private String name = "Entity Griefing";
            private List<String> description = List.of("&7Whether or not to allow", "&7creepers, endermen, etc...", "&7to break blocks.");
            private boolean enabledByDefault = false;
            private String message = "&cYou cannot use frost walker in &e%player%'s&c claim!";
        }

        private ExplosionDamage explosionDamage = new ExplosionDamage();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class ExplosionDamage {
            private Material icon = Material.TNT;
            private String name = "Explosion Damage";
            private List<String> description = List.of("&7Whether or not to allow", "&7TNT, End Crystals & TNT Minecarts", "&7to break blocks.");
            private boolean enabledByDefault = false;
        }

        private PvP pvp = new PvP();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class PvP {
            private Material icon = Material.DIAMOND_SWORD;
            private String name = "PvP";
            private List<String> description = List.of("&7Whether or not to allow", "&7players to fight each other.");
            private boolean enabledByDefault = false;
            private String message = "&cYou cannot attack players in &e%player%'s&c claim!";
        }

        private HostileMobSpawn hostileMobSpawn = new HostileMobSpawn();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class HostileMobSpawn {
            private Material icon = Material.ZOMBIE_SPAWN_EGG;
            private String name = "Monster Spawning";
            private List<String> description = List.of("&7Whether or not to allow", "&7hostile mobs to spawn.");
            private boolean enabledByDefault = false;
        }

        private PassiveMobSpawn passiveMobSpawn = new PassiveMobSpawn();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class PassiveMobSpawn {
            private Material icon = Material.PIG_SPAWN_EGG;
            private String name = "Animal Spawning";
            private List<String> description = List.of("&7Whether or not to allow", "&7passive mobs to spawn.");
            private boolean enabledByDefault = true;
        }
    }

    private Words words = new Words();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Words {
        private String none = "None";
    }

    public static void sendMessage(CommandSender sender, String message) {
        if (message.isEmpty()) return;
        if (sender instanceof Player player) {
            UserManager.getInstance().getUser(player.getUniqueId()).ifPresent(user -> user.sendMessage(message));
            return;
        }
        sender.sendMessage(Text.text(i().getPrefix() + message));
    }

    public static void reload() {
        instance = YamlConfigurations.load(new File(Fadlc.i().getDataFolder(), FILE_NAME).toPath(), Lang.class, PROPERTIES);
        Logger.info("Configuration '%s' automatically reloaded from disk.".formatted(FILE_NAME));
    }

    public static Lang i() {
        if (instance == null) {
            instance = YamlConfigurations.update(new File(Fadlc.i().getDataFolder(), FILE_NAME).toPath(), Lang.class, PROPERTIES);
            AutoReload.watch(Fadlc.i().getDataFolder().toPath(), FILE_NAME, Lang::reload);
        }

        return instance;
    }
}