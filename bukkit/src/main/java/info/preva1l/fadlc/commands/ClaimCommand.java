package info.preva1l.fadlc.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import info.preva1l.fadlc.menus.ClaimMenu;
import info.preva1l.fadlc.menus.SettingsMenu;
import info.preva1l.fadlc.menus.profile.ProfilesMenu;
import info.preva1l.fadlc.user.CommandUser;

@Command(value = "claim", alias = {"c", "claims"})
@Permission("fadlc.claim")
public class ClaimCommand extends BaseCommand {
    @Default
    public void root(CommandUser sender) {
        new ClaimMenu(sender.asPlayer());
    }

    @SubCommand("profiles")
    @Permission("fadlc.profiles")
    public void profile(CommandUser sender) {
        new ProfilesMenu(sender.asPlayer());
    }

    @SubCommand("settings")
    @Permission("fadlc.settings")
    public void settings(CommandUser sender) {
        new SettingsMenu(sender.asPlayer());
    }
}
