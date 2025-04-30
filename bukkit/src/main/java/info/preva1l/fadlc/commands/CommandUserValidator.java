package info.preva1l.fadlc.commands;

import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.SubCommand;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import info.preva1l.fadlc.user.BukkitUser;
import info.preva1l.fadlc.user.CommandUser;
import info.preva1l.fadlc.user.ConsoleUser;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class CommandUserValidator implements SenderValidator<CommandUser> {
    @Override
    public @NotNull Set<Class<? extends CommandUser>> getAllowedSenders() {
        return Set.of(BukkitUser.class, ConsoleUser.class, CommandUser.class);
    }

    @Override
    public boolean validate(final @NotNull MessageRegistry<CommandUser> messageRegistry, final @NotNull SubCommand<CommandUser> subCommand, final @NotNull CommandUser sender) {
        Class<? extends CommandUser> senderClass = subCommand.getSenderType();
        if (BukkitUser.class.isAssignableFrom(senderClass) && !(sender instanceof BukkitUser)) {
            messageRegistry.sendMessage(BukkitMessageKey.PLAYER_ONLY, sender, new DefaultMessageContext(subCommand.getParentName(), subCommand.getName()));
            return false;
        } else if (ConsoleUser.class.isAssignableFrom(senderClass) && !(sender instanceof ConsoleUser)) {
            messageRegistry.sendMessage(BukkitMessageKey.CONSOLE_ONLY, sender, new DefaultMessageContext(subCommand.getParentName(), subCommand.getName()));
            return false;
        } else {
            return true;
        }
    }
}