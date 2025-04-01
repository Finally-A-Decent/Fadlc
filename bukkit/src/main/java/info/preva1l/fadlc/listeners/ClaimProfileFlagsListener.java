package info.preva1l.fadlc.listeners;

import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.models.Position;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.claim.settings.ProfileFlag;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.registry.ProfileFlagsRegistry;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.*;

import java.util.Optional;
import java.util.function.Supplier;

@AllArgsConstructor
public class ClaimProfileFlagsListener implements Listener {
    private final UserManager userManager;
    private final ClaimManager claimManager;

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player a) || !(event.getDamager() instanceof Player d)) return;

        OnlineUser attacker = userManager.getUser(a).orElseThrow();
        OnlineUser damager = userManager.getUser(d).orElseThrow();

        if (!isFlagEnabledForUser(attacker) || !isFlagEnabledForUser(damager)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block ->
                !isFlagEnabledAtLoc(ProfileFlagsRegistry.EXPLOSION_DAMAGE, Position.fromBukkit(block.getLocation())));
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {
        if (!isFlagEnabledAtLoc(ProfileFlagsRegistry.EXPLOSION_DAMAGE, Position.fromBukkit(event.getLocation()))) {
            event.setCancelled(true);
            return;
        }

        event.blockList().removeIf(block ->
                !isFlagEnabledAtLoc(ProfileFlagsRegistry.EXPLOSION_DAMAGE, Position.fromBukkit(block.getLocation())));
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        IPosition pos = Position.fromBukkit(event.getLocation());

        if (event.getEntity() instanceof Monster && isFlagEnabledAtLoc(ProfileFlagsRegistry.HOSTILE_MOB_SPAWN, pos)) return;
        if (event.getEntity() instanceof Animals && isFlagEnabledAtLoc(ProfileFlagsRegistry.PASSIVE_MOB_SPAWN, pos)) return;

        if (!(event.getEntity() instanceof Monster || event.getEntity() instanceof Animals)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityGrief(EntityBlockFormEvent event) {
        IPosition position = Position.fromBukkit(event.getBlock().getLocation());
        if (!isFlagEnabledAtLoc(ProfileFlagsRegistry.ENTITY_GRIEFING, position)) {
            event.setCancelled(true);
            if (event.getEntity() instanceof Player player) {
                IClaim claim = claimManager.getClaimAt(position).orElseThrow();
                Lang.sendMessage(player, Lang.i().getProfileFlags().getEntityGriefing().getMessage()
                        .replace("%player%", claim.getOwner().getName()));
            }
        }
    }

    @EventHandler
    public void onEntityGrief(EntityBreakDoorEvent event) {
        if (isFlagEnabledAtLoc(ProfileFlagsRegistry.ENTITY_GRIEFING, Position.fromBukkit(event.getBlock().getLocation()))) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityGrief(EntityChangeBlockEvent event) {
        if (isFlagEnabledAtLoc(ProfileFlagsRegistry.ENTITY_GRIEFING, Position.fromBukkit(event.getBlock().getLocation()))) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityGrief(EntityInteractEvent event) {
        if (isFlagEnabledAtLoc(ProfileFlagsRegistry.ENTITY_GRIEFING, Position.fromBukkit(event.getBlock().getLocation()))) return;

        event.setCancelled(true);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isFlagEnabledForUser(OnlineUser user) {
        Optional<IClaimProfile> profileAtUser = getClaimProfileAt(user.getPosition());

        if (profileAtUser.isPresent() && !profileAtUser.get().getFlag(ProfileFlagsRegistry.PVP)) {
            user.sendMessage(Lang.i().getProfileFlags().getPvp().getMessage()
                    .replace("%player%", profileAtUser.get().getParent().getOwner().getName()));
            return false;
        }
        return true;
    }

    private boolean isFlagEnabledAtLoc(Supplier<ProfileFlag> flag, IPosition position) {
        Optional<IClaimProfile> profileAtUser = getClaimProfileAt(position);

        return profileAtUser.isPresent() && profileAtUser.get().getFlag(flag.get());
    }

    private Optional<IClaimProfile> getClaimProfileAt(IPosition position) {
        return claimManager.getClaimAt(position).flatMap(claim -> claim.getProfile(position.getChunk()));
    }
}
