package info.preva1l.fadlc.menus;

import com.github.puregero.multilib.regionized.RegionizedTask;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.config.menus.ClaimConfig;
import info.preva1l.fadlc.config.sounds.Sounds;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.menus.lib.FastInv;
import info.preva1l.fadlc.models.ChunkStatus;
import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.utils.FadlcExecutors;
import info.preva1l.fadlc.utils.TaskManager;
import info.preva1l.fadlc.utils.Text;
import info.preva1l.fadlc.utils.Time;
import net.kyori.adventure.text.Component;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ClaimMenu extends FastInv<ClaimConfig> {
    private final Player player;
    private final OnlineUser user;

    private final RegionizedTask updateTask;

    public ClaimMenu(Player player) {
        super(ClaimConfig.i());
        this.player = player;
        this.user = UserManager.getInstance().getUser(player.getUniqueId()).orElseThrow();

        CompletableFuture.runAsync(this::buttons, FadlcExecutors.VIRTUAL_THREAD_POOL)
                .thenRunAsync(() -> this.open(player), FadlcExecutors.MAIN_THREAD);
        this.updateTask = TaskManager.runAsyncRepeat(Fadlc.i(), this::placeChunkItems, 20L);
        addCloseHandler((e) -> updateTask.cancel());
    }

    private void buttons() {
        placeChunkItems();
        placeNavigationButtons();
        placeProfileSwitchButton();
    }

    private void placeNavigationButtons() {
        scheme.bindItem('B', config.getLang().getBuyChunks().easyItem()
                .replaceAnywhere("%chunks%", user.getAvailableChunks() + "").getBase(), e -> {
            Sounds.playSound(player, config.getLang().getBuyChunks().getSound());
        });

        scheme.bindItem('M', config.getLang().getManageProfiles().itemStack(), e -> {
            Sounds.playSound(player, config.getLang().getManageProfiles().getSound());
            new ProfilesMenu(player);
        });

        scheme.bindItem('S', config.getLang().getSettings().easyItem().skullOwner(player).getBase(), e -> {
            Sounds.playSound(player, config.getLang().getSettings().getSound());
            new SettingsMenu(player);
        });
    }

    private void placeProfileSwitchButton() {
        IClaimProfile previousProfile = user.getClaim().getProfiles().get(user.getClaimWithProfile().getId() - 1);
        String previous = previousProfile == null
                ? Lang.i().getWords().getNone()
                : previousProfile.getName();
        String current = user.getClaimWithProfile().getName();
        IClaimProfile nextProfile = user.getClaim().getProfiles().get(user.getClaimWithProfile().getId() + 1);
        String next = nextProfile == null
                ? Lang.i().getWords().getNone()
                : nextProfile.getName();
        scheme.bindItem('P', config.getLang().getSwitchProfile().easyItem()
                .replaceAnywhere("%previous%", Text.legacyMessage(previous))
                .replaceAnywhere("%current%", Text.legacyMessage(current))
                .replaceAnywhere("%next%", Text.legacyMessage(next)).getBase(), e -> {
            Sounds.playSound(player, config.getLang().getSwitchProfile().getSound());

            if (e.isLeftClick() && previousProfile != null) {
                user.setClaimWithProfile(previousProfile);
            }

            if (e.isRightClick() && nextProfile != null) {
                user.setClaimWithProfile(nextProfile);
            }

            placeProfileSwitchButton();
        });
    }

    private void placeChunkItems() {
        int index = 0;
        for (IClaimChunk chunk : getNearByChunksRelativeToPlayerAndMenu()) {
            if (index == 45) return;
            ChunkStatus chunkStatus = chunk.getStatus();

            setItem(index, getChunkItem(index, chunk), e -> {
                switch (chunkStatus) {
                    case CLAIMABLE -> claimChunk(chunk);
                    case CLAIMED -> {
                        Optional<IClaim> claim = ClaimManager.getInstance().getClaimAt(chunk);
                        if (claim.orElseThrow().getOwner().equals(user)) {
                            Sounds.playSound(player, config.getLang().getChunks().getClaimedYou().getSound());
                            return;
                        }

                        user.sendMessage("&cChunk is already claimed!");
                        Sounds.playSound(player, config.getLang().getChunks().getClaimedOther().getSound());
                    }
                    case WORLD_DISABLED -> {
                        user.sendMessage("&cClaiming is disabled in this world!");
                        Sounds.playSound(player, config.getLang().getChunks().getWorldDisabled().getSound());
                    }
                    case BLOCKED_WORLD_GUARD -> {
                        user.sendMessage("&cThis chunk is protected by world guard!");
                        Sounds.playSound(player, config.getLang().getChunks().getRestrictedRegion().getSound());
                    }
                    case BLOCKED_ZONE_BORDER -> {
                        user.sendMessage("&cYou cannot claim within 3 chunks of the zone border!");
                        Sounds.playSound(player, config.getLang().getChunks().getZoneBorder().getSound());
                    }
                }
            });
            ++index;
        }
    }

    private void claimChunk(IClaimChunk chunk) {
        if (user.getAvailableChunks() <= 0) {
            user.sendMessage(Lang.i().getClaimMessages().getFail().getNotEnoughChunks());
            Sounds.playSound(player, config.getLang().getChunks().getUnclaimedExpensive().getSound());
            return;
        }

        user.getClaim().claimChunk(chunk);
        placeChunkItems();
        Sounds.playSound(player, config.getLang().getChunks().getUnclaimed().getSound());
    }

    private ItemStack getChunkItem(int index, IClaimChunk chunk) {
        IClaim claim = ClaimManager.getInstance().getClaimAt(chunk).orElse(null);
        boolean isOwned = claim != null && claim.getOwner().equals(user);

        ItemStack stack = switch (chunk.getStatus()) {
            case CLAIMABLE -> config.getLang().getChunks().getUnclaimed().easyItem()
                    .replaceAnywhere("%chunk_x%", chunk.getChunkX() + "")
                    .replaceAnywhere("%chunk_z%", chunk.getChunkZ() + "")
                    .replaceInLore("%available%", user.getAvailableChunks() + "").getBase();
            case CLAIMED -> {
                if (claim == null) yield config.getLang().getChunks().getClaimedOther().easyItem().getBase();
                yield (isOwned ? config.getLang().getChunks().getClaimedYou() : config.getLang().getChunks().getClaimedOther())
                        .easyItem().skullOwner(claim.getOwner().getUniqueId())
                        .replaceAnywhere("%chunk_x%", chunk.getChunkX() + "")
                        .replaceAnywhere("%chunk_z%", chunk.getChunkZ() + "")
                        .replaceAnywhere("%claim_profile%", Text.legacyMessage(claim.getProfiles().get(chunk.getProfileId()).getName()))
                        .replaceAnywhere("%owner%", claim.getOwner().getName())
                        .replaceAnywhere("%formatted_time%", Time.formatTimeSince(chunk.getClaimedSince())).getBase();
            }
            case WORLD_DISABLED -> config.getLang().getChunks().getWorldDisabled().easyItem()
                    .replaceAnywhere("%chunk_x%", chunk.getChunkX() + "")
                    .replaceAnywhere("%chunk_z%", chunk.getChunkZ() + "").getBase();
            case BLOCKED_WORLD_GUARD -> config.getLang().getChunks().getRestrictedRegion().easyItem()
                    .replaceAnywhere("%chunk_x%", chunk.getChunkX() + "")
                    .replaceAnywhere("%chunk_z%", chunk.getChunkZ() + "").getBase();
            case BLOCKED_ZONE_BORDER -> config.getLang().getChunks().getZoneBorder().easyItem()
                    .replaceAnywhere("%chunk_x%", chunk.getChunkX() + "")
                    .replaceAnywhere("%chunk_z%", chunk.getChunkZ() + "").getBase();
        };

        if (index == 22) centerChunkItem(stack);
        return stack;
    }

    private void centerChunkItem(ItemStack stack) {
        stack.setType(config.getLang().getChunks().getCurrent().icon());
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return;

        List<Component> newLore = new ArrayList<>(Text.modernList(config.getLang().getChunks().getCurrent().loreHeader()));
        if (meta.lore() != null) newLore.addAll(meta.lore());

        meta.lore(newLore);
        meta.setCustomModelData(config.getLang().getChunks().getCurrent().modelData());
        stack.setItemMeta(meta);
    }

    public List<IClaimChunk> getNearByChunksRelativeToPlayerAndMenu() {
        List<IClaimChunk> chunkList = new LinkedList<>();
        BlockFace facing = player.getFacing();
        int playerChunkX = player.getLocation().getChunk().getX();
        int playerChunkZ = player.getLocation().getChunk().getZ();
        switch (facing) {
            case NORTH:
                for (int z = -2; z <= 2; z++) { // normal z-order for SOUTH
                    for (int x = -4; x <= 4; x++) {
                        int chunkX = playerChunkX + x, chunkZ = playerChunkZ + z;
                        chunkList.add(ClaimManager.getInstance()
                                .getChunkAt(chunkX, chunkZ, player.getWorld().getName()));
                    }
                }
                break;
            case EAST:
                for (int x = 2; x >= -2; x--) {
                    for (int z = -4; z <= 4; z++) {
                        int chunkX = playerChunkX + x, chunkZ = playerChunkZ + z;
                        chunkList.add(ClaimManager.getInstance()
                                .getChunkAt(chunkX, chunkZ, player.getWorld().getName()));
                    }
                }
                break;
            case SOUTH:
                for (int z = 2; z >= -2; z--) { // reverse z-order for NORTH
                    for (int x = 4; x >= -4; x--) {
                        int chunkX = playerChunkX + x, chunkZ = playerChunkZ + z;
                        chunkList.add(ClaimManager.getInstance()
                                .getChunkAt(chunkX, chunkZ, player.getWorld().getName()));
                    }
                }
                break;
            case WEST:
                for (int x = -2; x <= 2; x++) {
                    for (int z = 4; z >= -4; z--) {
                        int chunkX = playerChunkX + x, chunkZ = playerChunkZ + z;
                        chunkList.add(ClaimManager.getInstance()
                                .getChunkAt(chunkX, chunkZ, player.getWorld().getName()));
                    }
                }
                break;
        }
        return chunkList;
    }
}