package info.preva1l.fadlc.commands;

import info.preva1l.fadlc.commands.lib.BasicCommand;
import info.preva1l.fadlc.commands.lib.Command;
import info.preva1l.fadlc.commands.subcommands.ProfileSubCommand;
import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.menus.ClaimMenu;
import info.preva1l.fadlc.models.user.CommandUser;

import java.util.stream.Stream;

@Command(
        name = "claim",
        aliases = {"c", "claims"},
        permission = "fadlc.claim"
)
public class ClaimCommand extends BasicCommand {
    public ClaimCommand() {
        Stream.of(
                new ProfileSubCommand()
        ).forEach(getSubCommands()::add);
    }

    @Override
    public void execute(CommandUser sender, String[] args) {
        if (args.length >= 1) {
            if (subCommandExecutor(sender, args)) return;
            sender.sendMessage(Lang.i().getCommand().getUnknownArgs());
            return;
        }

        new ClaimMenu(sender.asPlayer()).open(sender.asPlayer());
    }
}
