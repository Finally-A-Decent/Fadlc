package info.preva1l.fadlc.commands;

import dev.triumphteam.cmd.bukkit.CommandPermission;

/**
 * Created on 31/03/2025
 *
 * @author Preva1l
 */
public record SubCommandInfo(
        String name,
        String description,
        CommandPermission permission
) {}