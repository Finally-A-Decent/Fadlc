package info.preva1l.fadlc.config.menus;

import de.exlll.configlib.*;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.AutoReload;
import info.preva1l.fadlc.config.menus.lang.MenuLang;
import info.preva1l.fadlc.config.misc.ConfigurableItem;
import info.preva1l.fadlc.utils.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("FieldMayBeFinal")
public class ClaimConfig implements MenuConfig<MenuLang> {
    private static ClaimConfig instance;
    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE).build();

    private String title = "&8Claim Chunks";

    private Lang lang = new Lang();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Lang implements MenuLang {
        private Chunks chunks = new Chunks();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Chunks {
            private Current current = new Current(
                    Material.NETHER_STAR, 0, List.of("&d&oYou are standing in this chunk.")
            );
            public record Current(Material icon, int modelData, List<String> loreHeader) {}
            private ConfigurableItem claimedYou = new ConfigurableItem(
                    Material.PLAYER_HEAD, 0, "click",
                    "%claim_profile%",
                    List.of(
                            "&7&l‣ &3Owner: &f%owner%",
                            "&7&l‣ &3Chunk: &f%chunk_x%, %chunk_z%",
                            "&7&l‣ &3Claimed: &f%formatted_time% ago",
                            "",
                            "&a\u2192 Click &3to manage this claim!"
                    )
            );
            private ConfigurableItem claimedOther = new ConfigurableItem(
                    Material.PLAYER_HEAD, 0, "fail",
                    "%claim_profile%",
                    List.of(
                            "&7&l‣ &3Owner: &f%owner%",
                            "&7&l‣ &3Chunk: &f%chunk_x%, %chunk_z%",
                            "&7&l‣ &3Claimed: &f%formatted_time% ago"
                    )
            );
            private ConfigurableItem unclaimed = new ConfigurableItem(
                    Material.GRAY_STAINED_GLASS_PANE, 0, "success",
                    "&7Unclaimed Chunk",
                    List.of(
                            "&7&l‣ &3Chunk: &f%chunk_x%, %chunk_z%",
                            "&7&l‣ &3Cost: &f1 Claim Chunk &7(You have: &f%available%&7)",
                            "",
                            "&a\u2192 Click &3to claim this chunk!"
                    )
            );
            private ConfigurableItem unclaimedExpensive = new ConfigurableItem(
                    Material.GRAY_STAINED_GLASS_PANE, 0, "fail",
                    "&7Unclaimed Chunk",
                    List.of(
                            "&7&l‣ &3Chunk: &f%chunk_x%, %chunk_z%",
                            "&7&l‣ &3Cost: &f1 Claim Chunk &7(You have: &f%available%&7)",
                            "",
                            "&c\u2192 You need more chunks!"
                    )
            );
            private ConfigurableItem worldDisabled = new ConfigurableItem(
                    Material.RED_STAINED_GLASS_PANE, 0, "fail",
                    "&c&oClaiming is disabled in this world!",
                    List.of(
                            "&7&l‣ &3Chunk: &f%chunk_x%, %chunk_z%",
                            "",
                            "&c\u2192 &oClaiming is disabled in this world!"
                    )
            );
            private ConfigurableItem restrictedRegion = new ConfigurableItem(
                    Material.RED_STAINED_GLASS_PANE, 0, "fail",
                    "&c&oYou cannot claim in protected areas!",
                    List.of(
                            "&7&l‣ &3Chunk: &f%chunk_x%, %chunk_z%",
                            "",
                            "&c\u2192 &oYou cannot claim in protected areas!"
                    )
            );
            @Comment("Hooks Into: AdvancedServerZones")
            private ConfigurableItem zoneBorder = new ConfigurableItem(
                    Material.PURPLE_STAINED_GLASS_PANE, 0, "fail",
                    "&c&oYou cannot claim near the zone border!",
                    List.of(
                            "&7&l‣ &3Chunk: &f%chunk_x%, %chunk_z%",
                            "",
                            "&c\u2192 &oYou cannot claim near the zone border!"
                    )
            );
        }
        private ConfigurableItem buyChunks = new ConfigurableItem(
                Material.BELL, 0, "click",
                "&3Buy Chunks", List.of("&fYou have &3%chunks% &fchunks", "&7Click to purchase more")
        );
        private ConfigurableItem switchProfile = new ConfigurableItem(
                Material.PAPER, 0, "click", "&3Switch Profile",
                List.of(
                        "&7\u2192 Left Click to cycle up",
                        "&7\u2192 Right Click to cycle down",
                        "&8-------------------------",
                        "&f%previous%",
                        "&8> &3%current%",
                        "&f%next%",
                        "&8-------------------------"
                )
        );
        private ConfigurableItem manageProfiles = new ConfigurableItem(
                Material.ANVIL, 0, "click",
                "&3Manage Profiles", List.of("&7\u2192 Click to manage your claim profiles")
        );
        private ConfigurableItem settings = new ConfigurableItem(
                Material.PLAYER_HEAD, 0, "click",
                "&3Settings", List.of("&7\u2192 Click to manage your settings")
        );
    }

    @Comment({
            "E = Empty (Gets filled with the chunks)",
            "0 = Filler",
            "B = Buy Chunks",
            "P = Switch Profile",
            "M = Manage Profiles",
            "S = Settings"
    })
    private List<String> layout = List.of(
            "EEEEEEEEE",
            "EEEEEEEEE",
            "EEEEEEEEE",
            "EEEEEEEEE",
            "EEEEEEEEE",
            "0B0P0M0S0"
    );

    public static void reload() {
        instance = YamlConfigurations.load(
                new File(Fadlc.i().getDataFolder(), "menus/claim.yml").toPath(),
                ClaimConfig.class,
                PROPERTIES
        );
        Logger.info("claim.yml automatically reloaded from disk.");
    }

    public static ClaimConfig i() {
        if (instance == null) {
            instance = YamlConfigurations.update(
                    new File(Fadlc.i().getDataFolder(), "menus/claim.yml").toPath(),
                    ClaimConfig.class,
                    PROPERTIES
            );
            AutoReload.watch(Fadlc.i().getDataFolder().toPath(), "menus/claim.yml", ClaimConfig::reload);
        }

        return instance;
    }
}