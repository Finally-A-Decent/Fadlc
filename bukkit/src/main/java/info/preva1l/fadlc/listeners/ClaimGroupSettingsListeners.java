package info.preva1l.fadlc.listeners;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.api.events.ClaimEnterEvent;
import info.preva1l.fadlc.api.events.ClaimLeaveEvent;
import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.models.Position;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.registry.GroupSettingsRegistry;
import info.preva1l.fadlc.registry.UserSettingsRegistry;
import lombok.AllArgsConstructor;
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

@AllArgsConstructor
public class ClaimGroupSettingsListeners implements Listener {
    private final Fadlc plugin;
    private final UserManager userManager;
    private final ClaimManager claimManager;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        IPosition loc = Position.fromBukkit(event.getBlock().getLocation());
        OnlineUser user = userManager.getUser(event.getPlayer().getUniqueId()).orElseThrow();
        if (plugin.isActionAllowed(user, loc, GroupSettingsRegistry.PLACE_BLOCKS.get())) return;

        event.setCancelled(true);
        IClaim claimAtLocation = claimManager.getClaimAt(loc).orElseThrow();

        Lang.sendMessage(event.getPlayer(), Lang.i().getGroupSettings().getPlaceBlocks().getMessage()
                .replace("%player%", claimAtLocation.getOwner().getName()));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        IPosition loc = Position.fromBukkit(event.getBlock().getLocation());
        OnlineUser user = userManager.getUser(event.getPlayer().getUniqueId()).orElseThrow();
        if (plugin.isActionAllowed(user, loc, GroupSettingsRegistry.BREAK_BLOCKS.get())) return;

        event.setCancelled(true);
        IClaim claimAtLocation = claimManager.getClaimAt(loc).orElseThrow();

        Lang.sendMessage(event.getPlayer(), Lang.i().getGroupSettings().getBreakBlocks().getMessage()
                .replace("%player%", claimAtLocation.getOwner().getName()));
    }

    @EventHandler
    public void onDoorInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null
                || !(event.getClickedBlock().getBlockData() instanceof Door)
                || !(event.getClickedBlock().getBlockData() instanceof TrapDoor)
                || event.getAction().isRightClick()) return;
        IPosition loc = Position.fromBukkit(event.getClickedBlock().getLocation());
        OnlineUser user = userManager.getUser(event.getPlayer().getUniqueId()).orElseThrow();
        if (plugin.isActionAllowed(user, loc, GroupSettingsRegistry.USE_DOORS.get())) return;

        event.setCancelled(true);
        IClaim claimAtLocation = claimManager.getClaimAt(loc).orElseThrow();

        Lang.sendMessage(event.getPlayer(), Lang.i().getGroupSettings().getUseDoors().getMessage()
                .replace("%player%", claimAtLocation.getOwner().getName()));
    }

    @EventHandler
    public void onButtonUse(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null
                || !(event.getClickedBlock().getType().toString().endsWith("_BUTTON"))
                || event.getAction().isRightClick()) return;
        IPosition loc = Position.fromBukkit(event.getClickedBlock().getLocation());
        OnlineUser user = userManager.getUser(event.getPlayer().getUniqueId()).orElseThrow();
        if (plugin.isActionAllowed(user, loc, GroupSettingsRegistry.USE_BUTTONS.get())) return;

        event.setCancelled(true);
        IClaim claimAtLocation = claimManager.getClaimAt(loc).orElseThrow();

        Lang.sendMessage(event.getPlayer(), Lang.i().getGroupSettings().getUseButtons().getMessage()
                .replace("%player%", claimAtLocation.getOwner().getName()));
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
        OnlineUser user = userManager.getUser(e.getPlayer().getUniqueId()).orElseThrow();

        if (toClaim != null) {
            if (!plugin.isActionAllowed(user, Position.fromBukkit(e.getTo()), GroupSettingsRegistry.ENTER.get())) {
                e.setCancelled(true);

                Lang.sendMessage(e.getPlayer(), Lang.i().getGroupSettings().getEnter().getMessage()
                        .replace("%player%", toClaim.getOwner().getName()));
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
                Lang.sendMessage(e.getPlayer(), Lang.i().getClaimMessages().getEnter()
                        .replace("%player%", toClaim.getOwner().getName()));
            }
        }

        if (fromClaim != null) {
            if (toClaim != null && fromClaim.getOwner().equals(toClaim.getOwner())) {
                return;
            }

            ClaimLeaveEvent leaveEvent = new ClaimLeaveEvent(e.getPlayer(), fromClaim, fromChunk);
            Bukkit.getPluginManager().callEvent(leaveEvent);

            if (user.getSetting(UserSettingsRegistry.CLAIM_LEAVE_ENTER_NOTIFICATION, true)) {
                Lang.sendMessage(e.getPlayer(), Lang.i().getClaimMessages().getLeave()
                        .replace("%player%", fromClaim.getOwner().getName()));
            }
        }
    }
}
