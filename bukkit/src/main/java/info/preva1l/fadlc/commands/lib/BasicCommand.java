package info.preva1l.fadlc.commands.lib;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.models.user.CommandUser;
import info.preva1l.fadlc.models.user.ConsoleUser;
import info.preva1l.fadlc.utils.Text;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BasicCommand {
    private final Command info;
    @Getter private final List<BasicCommand> subCommands;

    public BasicCommand() {
        this.info = getClass().getAnnotation(Command.class);
        this.subCommands = new ArrayList<>();

        if (info == null) {
            throw new RuntimeException("BasicCommand must be annotated with @Command");
        }
    }

    public abstract void execute(CommandUser sender, String[] args);

    public List<String> tabComplete(CommandUser sender, String[] args) {
        return new ArrayList<>();
    }

    /**
     * Handles subcommand execution easily.
     * @param sender command sender
     * @param args command args
     * @return true if the subcommand was executed,
     * false if the sender does not have permission or if the sender was console on a player only command
     */
    public boolean subCommandExecutor(CommandUser sender, String[] args) {
        for (BasicCommand subCommand : getSubCommands()) {
            if (args[0].equalsIgnoreCase(subCommand.getName())
                    || Arrays.stream(subCommand.getAliases()).toList().contains(args[0])) {

                if (subCommand.isInGameOnly() && sender instanceof ConsoleUser) {
                    sender.sendMessage(Text.modernMessage(Lang.i().getCommand().getMustBePlayer()));
                    return false;
                }

                if (!subCommand.getPermission().isEmpty() && !sender.hasPermission(subCommand.getPermission())) {
                    sender.sendMessage(Text.modernMessage(Lang.i().getCommand().getNoPermission()));
                    return false;
                }

                if (subCommand.isAsync()) {
                    Bukkit.getServer().getScheduler().runTaskAsynchronously(Fadlc.getPlugin(Fadlc.class),
                            () -> subCommand.execute(sender, removeFirstElement(args)));
                } else {
                    Bukkit.getServer().getScheduler().runTask(Fadlc.getPlugin(Fadlc.class),
                            () -> subCommand.execute(sender, removeFirstElement(args)));
                }
                return true;
            }
        }
        return false;
    }

    protected String[] removeFirstElement(String[] array) {
        if (array == null || array.length == 0) {
            return new String[]{};
        }

        String[] newArray = new String[array.length - 1];
        System.arraycopy(array, 1, newArray, 0, array.length - 1);

        return newArray;
    }

    public String getName() {
        return info.name();
    }

    public String getPermission() {
        return info.permission();
    }

    public String[] getAliases() {
        return info.aliases();
    }

    public boolean isInGameOnly() {
        return info.inGameOnly();
    }

    public boolean isAsync() {
        return info.async();
    }
}