package info.preva1l.fadlc.commands.admin;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.models.user.CommandUser;
import info.preva1l.fadlc.persistence.DatabaseType;
import info.preva1l.hooker.Hooker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.william278.desertwell.about.AboutMenu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command(value = "fadlc")
@Permission("fadlc.admin")
public class FadlcCommand extends BaseCommand {
    @SubCommand("give-chunks")
    @Permission("fadlc.admin.give-chunks")
    public void profile(CommandUser sender, Player player, int amount) {
        UserManager.getInstance().getUser(player.getUniqueId()).ifPresentOrElse(user -> {
            user.setAvailableChunks(user.getAvailableChunks() + amount);
            sender.sendMessage("Gave %s %s claim chunks.".formatted(player.getName(), amount));
        }, () -> sender.sendMessage("Cannot give chunks to an offline player!"));
    }

    @SubCommand("about")
    public void about(CommandUser sender) {
        List<AboutMenu.Credit> hooks = getHooks();
        DatabaseType dbType = Config.i().getStorage().getType();

        final AboutMenu aboutMenu = AboutMenu.builder()
                .title(Component.text("Finally a Decent Land Claim"))
                .description(Component.text("Fadlc is the fast, modern and advanced land claiming plugin that you have been looking for!"))
                .credits("Author",
                        AboutMenu.Credit.of("Preva1l")
                                .description("Click to visit website").url("https://docs.preva1l.info/")
                )
//                .credits("Contributors",
//                        AboutMenu.Credit.of("Your Name")
//                )
                .credits("Plugin Information",
                        AboutMenu.Credit
                                .of("Distribution: " + (Fadlc.VALID_PURCHASE ? "Premium" : "Free"))
                                .description(Fadlc.VALID_PURCHASE
                                        ? "You are running the full version of Fadlc!"
                                        : "You are running the free version of Fadlc, support will not be provided!"
                                ),
                        AboutMenu.Credit
                                .of("Database: " + dbType.getFriendlyName())
                                .description(dbType.isLocal() ? "Local/File Database" : "Remote/Server Database"),
                        AboutMenu.Credit
                                .of("Performance Mode: " + Config.i().getOptimization().getPerformanceMode().name())
                                .description("Currently optimized for: " + Config.i().getOptimization().getPerformanceMode().getPretty())
                )
                .credits("Loaded Hooks (%s)".formatted(hooks.size()), hooks.toArray(AboutMenu.Credit[]::new))
                .buttons(
                        AboutMenu.Link.of("https://discord.gg/4KcF7S94HF").text("Discord Support").icon("⭐"),
                        AboutMenu.Link.of("https://docs.preva1l.info/fadlc/").text("Documentation").icon("📖")
                )
                .version(Fadlc.i().getVersion())
                .themeColor(TextColor.fromHexString("#9555FF"))
                .secondaryColor(TextColor.fromHexString("#bba4e0"))
                .build();

        sender.sendMessage(aboutMenu.toComponent());
    }

    private List<AboutMenu.Credit> getHooks() {
        List<AboutMenu.Credit> hooks = new ArrayList<>();

        for (Object hook : Hooker.getLoadedHooks()) {
            hooks.add(AboutMenu.Credit
                    .of(hook.getClass().getSimpleName().replace("Hook", ""))
                    .description(""));
        }

        if (hooks.isEmpty()) hooks.add(AboutMenu.Credit.of("None").description("No hooks are loaded!"));
        return hooks;
    }
}
