package info.preva1l.fadlc.api;

import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.models.Location;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.models.user.User;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public interface IAdapter {
    OnlineUser player(Player player);

    User offlinePlayer(OfflinePlayer offlinePlayer);

    IClaimChunk chunk(Chunk chunk);

    IPosition location(org.bukkit.Location location, String server);

    IPosition location(org.bukkit.Location location);

    Location vector(org.bukkit.Location location);

    Location vector(Vector location);
}
