package info.preva1l.fadlc.claim.services;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.claim.IClaimProfile;
import info.preva1l.fadlc.claim.registry.ProfileFlagsRegistry;
import info.preva1l.fadlc.claim.settings.ProfileFlag;
import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.models.Position;
import info.preva1l.fadlc.user.OnlineUser;
import info.preva1l.fadlc.user.UserService;
import info.preva1l.trashcan.flavor.annotations.Service;
import info.preva1l.trashcan.flavor.annotations.inject.Inject;
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

@Service
public final class ClaimProfileFlagsService implements Listener {
    public static final ClaimProfileFlagsService instance = new ClaimProfileFlagsService();

    @Inject private Fadlc plugin;
    @Inject private UserService userService;
    @Inject private ClaimService claimManager;

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player a) || !(event.getDamager() instanceof Player d)) return;

        OnlineUser attacker = userService.getUser(a).orElseThrow();
        OnlineUser damager = userService.getUser(d).orElseThrow();

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
            if (event.getEntity() instanceof Player player) return;
            event.setCancelled(true);
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
