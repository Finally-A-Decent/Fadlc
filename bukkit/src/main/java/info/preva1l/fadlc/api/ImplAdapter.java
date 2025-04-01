package info.preva1l.fadlc.api;

import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.models.Location;
import info.preva1l.fadlc.models.Position;
import info.preva1l.fadlc.models.user.OfflineUser;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.models.user.User;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ImplAdapter implements Adapter {
    private static ImplAdapter instance;

    public static ImplAdapter getInstance() {
        if (instance == null) {
            instance = new ImplAdapter();
        }
        return instance;
    }

    @Override
    public OnlineUser player(Player player) {
        return UserManager.getInstance().getUser(player.getUniqueId()).orElseThrow();
    }

    @Override
    public User offlinePlayer(OfflinePlayer offlinePlayer) {
        return new OfflineUser(offlinePlayer.getUniqueId(), offlinePlayer.getName());
    }

    @Override
    public IClaimChunk chunk(Chunk chunk) {
        return ClaimManager.getInstance().getChunkAt(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());
    }

    @Override
    public IPosition location(org.bukkit.Location location, String server) {
        return Position.fromBukkit(location, server);
    }

    @Override
    public IPosition location(org.bukkit.Location location) {
        return Position.fromBukkit(location);
    }

    @Override
    public Location vector(org.bukkit.Location location) {
        return new Location(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public Location vector(Vector location) {
        return new Location(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
