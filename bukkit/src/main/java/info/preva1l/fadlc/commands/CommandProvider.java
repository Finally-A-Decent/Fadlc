package info.preva1l.fadlc.commands;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.NoPermissionMessageContext;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.commands.admin.FadlcCommand;
import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.models.user.BukkitUser;
import info.preva1l.fadlc.models.user.CommandUser;
import info.preva1l.fadlc.models.user.ConsoleUser;
import info.preva1l.fadlc.utils.Logger;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

/**
 * Created on 20/03/2025
 *
 * @author Preva1l
 */
public interface CommandProvider {
    Fadlc getPlugin();

    default void loadCommands() {
        loadCommandManager();
        registerCommands();
    }

    private void registerCommands() {
        Logger.info("Registering Commands...");
        Stream.of(
                new ClaimCommand(),
                new FadlcCommand()
        ).forEach(CommandManagerHolder.self.commandManager::registerCommand);
        Logger.info("Commands Registered!");
    }

    private void loadCommandManager() {
        Logger.info("Loading CommandManager...");
        CommandManagerHolder.self.commandManager = BukkitCommandManager.create(
                getPlugin(),
                sender -> sender instanceof Player p
                        ? (BukkitUser) UserManager.getInstance().getUser(p.getUniqueId()).orElseThrow()
                        : ConsoleUser.SELF,
                new CommandUserValidator()
        );

        CommandManagerHolder.self.commandManager.registerMessage(
                MessageKey.NOT_ENOUGH_ARGUMENTS,
                (user, context) -> user.sendMessage(Lang.i().getCommand().getUnknownArgs())
        );
        CommandManagerHolder.self.commandManager.registerMessage(
                MessageKey.INVALID_ARGUMENT,
                (user, context) -> user.sendMessage(Lang.i().getCommand().getUnknownArgs())
        );
        CommandManagerHolder.self.commandManager.registerMessage(
                MessageKey.UNKNOWN_COMMAND,
                (user, context) -> user.sendMessage(Lang.i().getCommand().getUnknownArgs())
        );
        CommandManagerHolder.self.commandManager.registerMessage(
                MessageKey.of("NO_PERMISSION", NoPermissionMessageContext.class),
                (user, context) -> user.sendMessage(Lang.i().getCommand().getNoPermission())
        );
        CommandManagerHolder.self.commandManager.registerMessage(
                MessageKey.of("PLAYER_ONLY", DefaultMessageContext.class),
                (user, context) -> user.sendMessage(Lang.i().getCommand().getMustBePlayer())
        );
        Logger.info("CommandManager Registered!");
    }
    
    class CommandManagerHolder {
        private static final CommandManagerHolder self = new CommandManagerHolder();
        
        private BukkitCommandManager<CommandUser> commandManager;
    }
}
