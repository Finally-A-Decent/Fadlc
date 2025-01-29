package info.preva1l.fadlc.commands.admin;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.models.user.CommandUser;
import org.bukkit.entity.Player;

@Command(value = "fadlc")
@Permission("fadlc.admin")
public class FadlcCommand extends BaseCommand {
    @SubCommand("give-chunks")
    @Permission("fadlc.admin.give-chuunks")
    public void profile(CommandUser sender, Player player, int amount) {
        UserManager.getInstance().getUser(player.getUniqueId()).ifPresentOrElse(user -> {
            user.setAvailableChunks(user.getAvailableChunks() + amount);
            sender.sendMessage("Gave %s %s claim chunks.".formatted(player.getName(), amount));
        }, () -> sender.sendMessage("Cannot give chunks to an offline player!"));
    }
}
