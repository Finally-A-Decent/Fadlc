package info.preva1l.fadlc.claim.services;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.api.events.ClaimEnterEvent;
import info.preva1l.fadlc.api.events.ClaimLeaveEvent;
import info.preva1l.fadlc.claim.IClaim;
import info.preva1l.fadlc.claim.IClaimChunk;
import info.preva1l.fadlc.claim.registry.GroupSettingsRegistry;
import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.models.Position;
import info.preva1l.fadlc.models.Tuple;
import info.preva1l.fadlc.user.OnlineUser;
import info.preva1l.fadlc.user.UserService;
import info.preva1l.fadlc.user.registry.UserSettingsRegistry;
import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;
import info.preva1l.trashcan.flavor.annotations.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

@Service
public final class ClaimGroupSettingsService implements Listener {
    public static final ClaimGroupSettingsService instance = new ClaimGroupSettingsService();

    @Inject private Fadlc plugin;
    @Inject private UserService userService;
    @Inject private ClaimService claimManager;

    @Configure
    public void configure() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        IPosition loc = Position.fromBukkit(event.getBlock().getLocation());
        OnlineUser user = userService.getUser(event.getPlayer().getUniqueId()).orElseThrow();
        if (user.canPerformAction(loc, GroupSettingsRegistry.PLACE_BLOCKS)) return;

        event.setCancelled(true);
        IClaim claimAtLocation = claimManager.getClaimAt(loc).orElseThrow();

        user.sendMessage(Lang.i().getGroupSettings().getPlaceBlocks().getMessage(),
                Tuple.of("%player%", claimAtLocation.getOwner().getName()));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        IPosition loc = Position.fromBukkit(event.getBlock().getLocation());
        OnlineUser user = userService.getUser(event.getPlayer().getUniqueId()).orElseThrow();
        if (user.canPerformAction(loc, GroupSettingsRegistry.BREAK_BLOCKS)) return;

        event.setCancelled(true);
        IClaim claimAtLocation = claimManager.getClaimAt(loc).orElseThrow();

        user.sendMessage(Lang.i().getGroupSettings().getBreakBlocks().getMessage(),
                Tuple.of("%player%", claimAtLocation.getOwner().getName()));
    }

    @EventHandler
    public void onDoorInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null
                || !(event.getClickedBlock().getBlockData() instanceof Door)
                || !(event.getClickedBlock().getBlockData() instanceof TrapDoor)
                || event.getAction().isRightClick()) return;
        IPosition loc = Position.fromBukkit(event.getClickedBlock().getLocation());
        OnlineUser user = userService.getUser(event.getPlayer().getUniqueId()).orElseThrow();
        if (user.canPerformAction(loc, GroupSettingsRegistry.USE_DOORS)) return;

        event.setCancelled(true);
        IClaim claimAtLocation = claimManager.getClaimAt(loc).orElseThrow();

        user.sendMessage(Lang.i().getGroupSettings().getUseDoors().getMessage(),
                Tuple.of("%player%", claimAtLocation.getOwner().getName()));
    }

    @EventHandler
    public void onButtonUse(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null
                || !(event.getClickedBlock().getType().toString().endsWith("_BUTTON"))
                || event.getAction().isRightClick()) return;
        IPosition loc = Position.fromBukkit(event.getClickedBlock().getLocation());
        OnlineUser user = userService.getUser(event.getPlayer().getUniqueId()).orElseThrow();
        if (user.canPerformAction(loc, GroupSettingsRegistry.USE_BUTTONS)) return;

        event.setCancelled(true);
        IClaim claimAtLocation = claimManager.getClaimAt(loc).orElseThrow();

        user.sendMessage(Lang.i().getGroupSettings().getUseButtons().getMessage(),
                Tuple.of("%player%", claimAtLocation.getOwner().getName()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();
        if (to.getChunk().equals(from.getChunk())) return;
        IClaimChunk fromChunk = claimManager.getChunkAt(from.getChunk().getX(), from.getChunk().getZ(), from.getWorld().getName());
        IClaimChunk toChunk = claimManager.getChunkAt(to.getChunk().getX(), to.getChunk().getZ(), to.getWorld().getName());
        IClaim fromClaim = claimManager.getClaimAt(fromChunk).orElse(null);
        IClaim toClaim = claimManager.getClaimAt(toChunk).orElse(null);
        OnlineUser user = userService.getUser(e.getPlayer().getUniqueId()).orElseThrow();

        if (toClaim != null) {
            if (!user.canPerformAction(Position.fromBukkit(e.getTo()), GroupSettingsRegistry.ENTER)) {
                e.setCancelled(true);

                user.sendMessage(Lang.i().getGroupSettings().getEnter().getMessage(),
                        Tuple.of("%player%", toClaim.getOwner().getName()));
                return;
            }

            if (fromClaim != null && toClaim.getOwner().equals(fromClaim.getOwner())) {
                return;
            }

            ClaimEnterEvent enterEvent = new ClaimEnterEvent(e.getPlayer(), toClaim, toChunk);
            Bukkit.getPluginManager().callEvent(enterEvent);
            if (enterEvent.isCancelled()) {
                e.setCancelled(true);
                return;
            }

            if (user.getSetting(UserSettingsRegistry.CLAIM_LEAVE_ENTER_NOTIFICATION, true)) {
                user.sendMessage(Lang.i().getClaimMessages().getEnter(),
                        Tuple.of("%player%", toClaim.getOwner().getName()));
            }
        }

        if (fromClaim != null) {
            if (toClaim != null && fromClaim.getOwner().equals(toClaim.getOwner())) {
                return;
            }

            ClaimLeaveEvent leaveEvent = new ClaimLeaveEvent(e.getPlayer(), fromClaim, fromChunk);
            Bukkit.getPluginManager().callEvent(leaveEvent);

            if (user.getSetting(UserSettingsRegistry.CLAIM_LEAVE_ENTER_NOTIFICATION, true)) {
                user.sendMessage(Lang.i().getClaimMessages().getLeave(),
                        Tuple.of("%player%", fromClaim.getOwner().getName()));
            }
        }
    }
}
