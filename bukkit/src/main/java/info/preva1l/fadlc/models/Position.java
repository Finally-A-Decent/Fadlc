package info.preva1l.fadlc.models;

import info.preva1l.fadlc.claim.IClaimChunk;
import info.preva1l.fadlc.claim.services.ClaimService;
import info.preva1l.fadlc.config.ServerSettings;

public class Position extends IPosition {
    public Position(String server, String world, int x, int y, int z) {
        super(server, world, x, y, z);
    }

    public static IPosition fromBukkit(org.bukkit.Location location) {
        return new Position(ServerSettings.getInstance().getName(), location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static IPosition fromBukkit(org.bukkit.Location location, String server) {
        return new Position(server, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public IClaimChunk getChunk() {
        return ClaimService.getInstance().getChunkAt(this);
    }
}