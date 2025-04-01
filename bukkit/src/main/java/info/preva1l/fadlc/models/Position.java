package info.preva1l.fadlc.models;

import info.preva1l.fadlc.config.ServerSettings;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.models.user.User;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

@Getter
public class Position extends IPosition {
    private final String server;
    private final String world;

    public Position(String server, String world, int x, int y, int z) {
        super(x, y, z);
        this.server = server;
        this.world = world;
    }

    public static IPosition fromBukkit(org.bukkit.Location location) {
        return new Position(ServerSettings.getInstance().getName(), location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static IPosition fromBukkit(org.bukkit.Location location, String server) {
        return new Position(server, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public IClaimChunk getChunk() {
        return ClaimManager.getInstance().getChunkAt(this);
    }

    @Override
    public void teleport(User user) {
        Player player = Bukkit.getPlayer(user.getUniqueId());
        if (player == null) {
            return;
        }
        World world = Bukkit.getWorld(getWorld());
        player.teleport(new org.bukkit.Location(world, getX(), getY(), getZ()));
    }
}