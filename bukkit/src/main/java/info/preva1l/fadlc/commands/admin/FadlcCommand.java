package info.preva1l.fadlc.commands.admin;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import info.preva1l.fadlc.menus.ClaimMenu;
import info.preva1l.fadlc.menus.ProfilesMenu;
import info.preva1l.fadlc.models.user.CommandUser;

@Command(value = "fadlc")
@Permission("fadlc.admin")
public class FadlcCommand extends BaseCommand {
    @Default
    public void root(CommandUser sender) {
        new ClaimMenu(sender.asPlayer());
    }

    @SubCommand("profiles")
    @Permission("fadlc.profiles")
    public void profile(CommandUser sender) {
        new ProfilesMenu(sender.asPlayer());
    }
}
