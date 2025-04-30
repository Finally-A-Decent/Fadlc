package info.preva1l.fadlc.commands.admin;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.persistence.DatabaseType;
import info.preva1l.fadlc.user.CommandUser;
import info.preva1l.fadlc.user.UserService;
import info.preva1l.trashcan.chat.AboutMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

@Command(value = "fadlc")
@Permission("fadlc.admin")
public class FadlcCommand extends BaseCommand {
    private final Fadlc plugin;

    public FadlcCommand(Fadlc plugin) {
        this.plugin = plugin;
    }

    @SubCommand("give-chunks")
    @Permission("fadlc.admin.give-chunks")
    public void profile(CommandUser sender, Player player, int amount) {
        UserService.getInstance().getUser(player.getUniqueId()).ifPresentOrElse(user -> {
            user.setAvailableChunks(user.getAvailableChunks() + amount);
            sender.sendMessage("Gave %s %s claim chunks.".formatted(player.getName(), amount));
        }, () -> sender.sendMessage("Cannot give chunks to an offline player!"));
    }

    @SubCommand("about")
    public void about(CommandUser sender) {
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
                .buttons(
                        AboutMenu.Link.of("https://discord.gg/4KcF7S94HF").text("Discord Support").icon("⭐"),
                        AboutMenu.Link.of("https://docs.preva1l.info/fadlc/").text("Documentation").icon("📖")
                )
                .version(plugin.getCurrentVersion())
                .themeColor(TextColor.fromHexString("#9555FF"))
                .secondaryColor(TextColor.fromHexString("#bba4e0"))
                .build();

        sender.getAudience().sendMessage(aboutMenu.toComponent());
    }
}
