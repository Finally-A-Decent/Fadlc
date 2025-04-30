package info.preva1l.fadlc.commands;

import dev.triumphteam.cmd.bukkit.BukkitCommand;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.BukkitSubCommand;
import dev.triumphteam.cmd.bukkit.CommandPermission;
import dev.triumphteam.cmd.bukkit.message.NoPermissionMessageContext;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import dev.triumphteam.cmd.core.requirement.RequirementKey;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.commands.admin.FadlcCommand;
import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.models.Tuple;
import info.preva1l.fadlc.user.BukkitUser;
import info.preva1l.fadlc.user.CommandUser;
import info.preva1l.fadlc.user.ConsoleUser;
import info.preva1l.fadlc.user.UserService;
import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;
import info.preva1l.trashcan.flavor.annotations.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created on 20/03/2025
 *
 * @author Preva1l
 */
@Service
@SuppressWarnings("unchecked")
public final class CommandService {
    public static final CommandService instance = new CommandService();

    @Inject private Fadlc plugin;

    private BukkitCommandManager<CommandUser> commandManager;
    private static final Map<String, BukkitCommand<CommandUser>> commands = new HashMap<>();
    private static final Map<String, Map<String, BukkitSubCommand<CommandUser>>> subCommands = new HashMap<>();

    @Configure
    public void configure() {
        loadCommandManager();
        registerCommands();
    }

    private void registerCommands() {
        Stream.of(
                new ClaimCommand(),
                new FadlcCommand(plugin)
        ).forEach(this::registerCommand);
    }

    public void registerCommand(BaseCommand command) {
        commandManager.registerCommand(command);
        Command bukkitCommand = getCommandMap().getKnownCommands().get("fadah:" + command.getCommand());
        if (!(bukkitCommand instanceof BukkitCommand<?>)) return;
        BukkitCommand<CommandUser> triumphCommand = (BukkitCommand<CommandUser>) bukkitCommand;

        commands.put(triumphCommand.getName(), triumphCommand);
        subCommands.put(triumphCommand.getName(), extractSubCommands(triumphCommand));
    }

    public void unregisterCommand(String command) {
        subCommands.remove(command);
        BukkitCommand<CommandUser> cmd = commands.remove(command);

        cmd.unregister(getCommandMap());
    }

    public List<SubCommandInfo> getSubCommands(String command) {
        List<SubCommandInfo> infos = new ArrayList<>();
        for (BukkitSubCommand<CommandUser> cmd : subCommands.get(command).values()) {
            Tuple<List<String>, String> descriptionAndAliases = getSubCommandInfo(cmd.getName());

            CommandPermission permission = cmd.getPermission() != null
                    ? cmd.getPermission()
                    : new CommandPermission(List.of(), "Default Permission Handle", PermissionDefault.TRUE);

            infos.add(new SubCommandInfo(cmd.getName(), descriptionAndAliases.second(), permission));
        }
        return infos;
    }

    private void loadCommandManager() {
        commandManager = BukkitCommandManager.create(
                plugin,
                sender -> sender instanceof Player p
                        ? (BukkitUser) UserService.getInstance().getUser(p.getUniqueId()).orElseThrow()
                        : ConsoleUser.SELF,
                new CommandUserValidator()
        );
        registerMessages();
        registerArguments();

        commandManager.registerRequirement(
                RequirementKey.of("enabled"),
                user -> {
                    boolean enabled = Config.i().isEnabled();
                    if (!enabled) user.sendMessage(Lang.i().getCommand().getDisabled());
                    return enabled;
                }
        );
    }

    private void registerMessages() {
        commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, this::badArgs);
        commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, this::badArgs);
        commandManager.registerMessage(
                MessageKey.UNKNOWN_COMMAND,
                (user, context) -> user.sendMessage(Lang.i().getCommand().getCommandNotFound())
        );
        commandManager.registerMessage(
                MessageKey.of("NO_PERMISSION", NoPermissionMessageContext.class),
                (user, context) -> user.sendMessage(Lang.i().getCommand().getNoPermission())
        );
        commandManager.registerMessage(
                MessageKey.of("PLAYER_ONLY", DefaultMessageContext.class),
                (user, context) -> user.sendMessage(Lang.i().getCommand().getMustBePlayer())
        );
    }

    private void registerArguments() {
        commandManager.registerArgument(
                OfflinePlayer.class,
                (sender, argument) -> Bukkit.getOfflinePlayerIfCached(argument)
        );
        commandManager.registerArgument(
                UUID.class,
                (sender, argument) -> {
                    try {
                        return UUID.fromString(argument);
                    } catch (IllegalArgumentException ignored) {
                        return null;
                    }
                }
        );
//        commandManager.registerArgument(
//                Double.TYPE,
//                (sender, argument) -> {
//                    try {
//                        return Text.getAmountFromString(argument);
//                    } catch (NumberFormatException ignored) {
//                        return null;
//                    }
//                }
//        );
        // Plugin Migrators
        commandManager.registerArgument(
                Plugin.class,
                (sender, argument) -> {
                    Plugin plugin = Bukkit.getPluginManager().getPlugin(argument);
                    if (plugin == null || !plugin.isEnabled()) {
                        return null;
                    }
                    return plugin;
                }
        );
//        commandManager.registerSuggestion(
//                Plugin.class,
//                (sender, context) -> MigrationService.instance.getMigratorNames()
//        );
    }

    private <T extends MessageContext> void badArgs(CommandUser user, T context) {
        if (context instanceof InvalidArgumentContext invalid) {
            if (invalid.getArgumentType().isAssignableFrom(OfflinePlayer.class)) {
                user.sendMessage(
                        Lang.i().getCommand().getPlayerNotFound(),
                        Tuple.of("%player%", invalid.getTypedArgument())
                );
                return;
            }

            if (invalid.getArgumentType().isAssignableFrom(Double.TYPE)) {
                //user.sendMessage(Lang.i().getCommand().getMustBeNumber());
                return;
            }

            user.sendMessage(Lang.i().getCommand().getInvalidArgument(),
                    Tuple.of("%arg%", invalid.getTypedArgument()),
                    Tuple.of("%type%", invalid.getArgumentType().getSimpleName())
            );
            return;
        }

        user.sendMessage(Lang.i().getCommand().getUnknownArgs());
    }

    private CommandMap getCommandMap() {
        try {
            Field commandMapField = commandManager.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            return (CommandMap) commandMapField.get(commandManager);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, BukkitSubCommand<CommandUser>> extractSubCommands(BukkitCommand<CommandUser> command) {
        try {
            Field subCommandsField = command.getClass().getDeclaredField("subCommands");
            subCommandsField.setAccessible(true);

            Map<String, BukkitSubCommand<CommandUser>> subCommands = new HashMap<>();

            for (Map.Entry<String, BukkitSubCommand<CommandUser>> entry
                    : ((Map<String, BukkitSubCommand<CommandUser>>) subCommandsField.get(command)).entrySet()) {
                String subCommandName = entry.getKey();
                BukkitSubCommand<CommandUser> subCommand = entry.getValue();

                for (String alias : getSubCommandInfo(subCommandName).first()) {
                    command.addSubCommand(alias, subCommand);
                    subCommands.put(alias, subCommand);
                }
                subCommands.put(entry.getKey(), entry.getValue());
            }

            return subCommands;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Tuple<List<String>, String> getSubCommandInfo(String commandName) {
        return switch (commandName) {
            // todo: impl
            default -> Tuple.of(List.of("bla"), "Unknown");
        };
    }
}