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


    private String prefix = "&#9555ff&lFADLC &8&l» &r";

    private ClaimMessages claimMessages = new ClaimMessages();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ClaimMessages {
        private String enter = "&fYou have &aentered &e%player%'s&f claim!";
        private String leave = "&fYou have &cleft &e%player%'s&f claim!";
    }

    private Prevention prevention = new Prevention();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Prevention {
        private String placeBlocks = "&cYou cannot place blocks in &e%player%'s&c claim!";
        private String enter = "&cYou cannot enter &e%player%'s&c claim!";
    }

    private Command command = new Command();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Command {
        private String noPermission = "&c&l(!)&r &fInsufficient permission";
        private String unknownArgs = "&c&l(!)&r &fUnknown arguments.";
        private String mustBePlayer = "&c&l(!)&r &fYou must be a player to run this command.";
    }

    private GroupSettings groupSettings = new GroupSettings();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GroupSettings {
        private BreakBlocks breakBlocks = new BreakBlocks();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class BreakBlocks {
            private String name = "Break Blocks";
            private List<String> description = List.of("Whether or not to allow", "creepers, endermen,", "wither & enderdragon to break blocks.");
            private String message = "&cYou cannot break blocks in &e%player%'s&c claim!";
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
            private String name = "Entity Griefing";
            private List<String> description = List.of("Whether or not to allow", "creepers, endermen,", "wither & enderdragon to break blocks.");
            private boolean enabledByDefault = false;
        }

        private ExplosionDamage explosionDamage = new ExplosionDamage();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class ExplosionDamage {
            private String name = "Explosion Damage";
            private List<String> description = List.of("Whether or not to allow", "TNT, End Crystals & TNT Minecarts", "to break blocks.");
            private boolean enabledByDefault = false;
        }

        private Pvp pvp = new Pvp();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Pvp {
            private String name = "PvP";
            private List<String> description = List.of("Whether or not to allow", "players to fight each other.");
            private boolean enabledByDefault = false;
        }
    }

    public static void sendMessage(CommandSender sender, String message) {
        if (sender instanceof Player player) {
            UserManager.getInstance().getUser(player.getUniqueId()).ifPresent(user -> {
                user.sendMessage(message);
            });
            return;
        }
        sender.sendMessage(Text.legacyMessage(getInstance().getPrefix() + message));
    }

    public static void reload() {
        instance = YamlConfigurations.load(new File(Fadlc.i().getDataFolder(), "lang.yml").toPath(), Lang.class, PROPERTIES);
        Logger.info("Configuration automatically reloaded from disk.");
    }

    public static Lang getInstance() {
        if (instance == null) {
            instance = YamlConfigurations.update(new File(Fadlc.i().getDataFolder(), "lang.yml").toPath(), Lang.class, PROPERTIES);
            AutoReload.watch(Fadlc.i().getDataFolder().toPath(), "lang.yml", Lang::reload);
        }

        return instance;
    }
}