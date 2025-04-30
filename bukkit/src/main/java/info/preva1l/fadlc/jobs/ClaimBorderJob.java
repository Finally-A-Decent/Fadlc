package info.preva1l.fadlc.jobs;

import info.preva1l.fadlc.claim.IClaim;
import info.preva1l.fadlc.claim.IClaimChunk;
import info.preva1l.fadlc.claim.IClaimProfile;
import info.preva1l.fadlc.claim.services.ClaimService;
import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.config.particles.Particles;
import info.preva1l.fadlc.models.ChunkStatus;
import info.preva1l.fadlc.user.OnlineUser;
import info.preva1l.fadlc.user.UserService;
import info.preva1l.fadlc.user.registry.UserSettingsRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Optional;

public class ClaimBorderJob extends Job {
    public ClaimBorderJob() {
        super("Claim Borders", Duration.ofMillis(Config.i().getOptimization().getParticleFrequencyMillis()), true);
    }

    @Override
    protected void execute() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            OnlineUser user = UserService.getInstance().getUser(player.getUniqueId()).orElseThrow();
            if (!user.getSetting(UserSettingsRegistry.VIEW_BORDERS, true)) continue;
            Location playerLocation = player.getLocation();
            int playerY = playerLocation.getBlockY();

            Chunk playerChunk = playerLocation.getChunk();
            int playerChunkX = playerChunk.getX();
            int playerChunkZ = playerChunk.getZ();

            int chunkRadius = Config.i().getOptimization().getParticleViewDistance() / 16 + 1;

            for (int chunkX = playerChunkX - chunkRadius; chunkX <= playerChunkX + chunkRadius; chunkX++) {
                for (int chunkZ = playerChunkZ - chunkRadius; chunkZ <= playerChunkZ + chunkRadius; chunkZ++) {
                    Chunk chunk = player.getWorld().getChunkAt(chunkX, chunkZ);
                    IClaimChunk claimChunk = ClaimService.getInstance().getChunkAt(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());

                    if (claimChunk.getStatus() != ChunkStatus.CLAIMED) continue;

                    outlineChunkWithParticles(player, chunk, playerY, playerLocation);
                }
            }
        }
    }

    private void outlineChunkWithParticles(Player player, Chunk chunk, int playerY, Location playerLocation) {
        int startX = chunk.getX() << 4;
        int startZ = chunk.getZ() << 4;
        int endX = startX + 16;
        int endZ = startZ + 16;
        IClaimChunk claimChunk = ClaimService.getInstance().getChunkAt(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());
        Optional<IClaim> claim = ClaimService.getInstance().getClaimAt(claimChunk);

        if (claim.isEmpty()) {
            return;
        }

        for (int x = startX; x <= endX; x++) {
            if (isWithinViewDistance(playerLocation, x, startZ)
                    && !isConnectingChunk(player.getWorld().getChunkAt(chunk.getX(), chunk.getZ() - 1), chunk)) {
                spawnParticleAt(player, new Location(player.getWorld(), x, playerY + 1, startZ),
                        claim.get().getProfiles().get(claimChunk.getProfileId()));
            }
            if (isWithinViewDistance(playerLocation, x, endZ)
                    && !isConnectingChunk(player.getWorld().getChunkAt(chunk.getX(), chunk.getZ() + 1), chunk)) {
                spawnParticleAt(player, new Location(player.getWorld(), x, playerY + 1, endZ),
                        claim.get().getProfiles().get(claimChunk.getProfileId()));
            }
        }

        for (int z = startZ; z <= endZ; z++) {
            if (isWithinViewDistance(playerLocation, startX, z)
                    && !isConnectingChunk(player.getWorld().getChunkAt(chunk.getX() - 1, chunk.getZ()), chunk)) {
                spawnParticleAt(player, new Location(player.getWorld(), startX, playerY + 1, z),
                        claim.get().getProfiles().get(claimChunk.getProfileId()));
            }
            if (isWithinViewDistance(playerLocation, endX, z)
                    && !isConnectingChunk(player.getWorld().getChunkAt(chunk.getX() + 1, chunk.getZ()), chunk)) {
                spawnParticleAt(player, new Location(player.getWorld(), endX, playerY + 1, z),
                        claim.get().getProfiles().get(claimChunk.getProfileId()));
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isConnectingChunk(Chunk chnk1, Chunk chnk2) {
        IClaimChunk chunk1 = ClaimService.getInstance().getChunkAt(chnk1.getX(), chnk1.getZ(), chnk1.getWorld().getName());
        IClaimChunk chunk2 = ClaimService.getInstance().getChunkAt(chnk2.getX(), chnk2.getZ(), chnk2.getWorld().getName());

        Optional<IClaim> claim1 = ClaimService.getInstance().getClaimAt(chunk1);
        Optional<IClaim> claim2 = ClaimService.getInstance().getClaimAt(chunk2);

        if (claim1.isEmpty() || claim2.isEmpty()) return false;

        if (!claim1.get().getOwner().equals(claim2.get().getOwner())) return false;

        return chunk1.getProfileId() == chunk2.getProfileId();
    }

    private boolean isWithinViewDistance(Location playerLocation, int x, int z) {
        double distance = playerLocation.distance(new Location(playerLocation.getWorld(), x, playerLocation.getY(), z));
        return distance <= Config.i().getOptimization().getParticleViewDistance();
    }

    private void spawnParticleAt(Player player, Location loc, IClaimProfile profile) {
        Particles.showParticle(player, profile.getBorder(), loc);
        Particles.showParticle(player, profile.getBorder(), loc.clone().add(0, 1, 0));
        Particles.showParticle(player, profile.getBorder(), loc.clone().add(0, 2, 0));
        Particles.showParticle(player, profile.getBorder(), loc.clone().add(0, -1, 0));
        Particles.showParticle(player, profile.getBorder(), loc.clone().add(0, -2, 0));
    }
}
