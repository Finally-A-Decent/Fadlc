package info.preva1l.fadlc.menus;

import com.github.puregero.multilib.regionized.RegionizedTask;
import info.preva1l.fadlc.claim.IClaim;
import info.preva1l.fadlc.claim.IClaimChunk;
import info.preva1l.fadlc.claim.IClaimProfile;
import info.preva1l.fadlc.claim.IClaimService;
import info.preva1l.fadlc.claim.services.ClaimService;
import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.config.menus.ClaimConfig;
import info.preva1l.fadlc.menus.lib.FastInv;
import info.preva1l.fadlc.menus.profile.ProfilesMenu;
import info.preva1l.fadlc.models.ChunkStatus;
import info.preva1l.fadlc.utils.Tasks;
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

public class ClaimMenu extends FastInv<ClaimConfig> {
    private final RegionizedTask updateTask;

    private final IClaimService claimManager = ClaimService.getInstance();

    public ClaimMenu(Player player) {
        super(player, ClaimConfig.i());
       
        this.updateTask = Tasks.runAsyncRepeat(this::placeChunkItems, 20L);
        addCloseHandler((e) -> updateTask.cancel());
    }

    @Override
    protected void buttons() {
        super.buttons();
        placeChunkItems();
        placeProfileSwitchButton();
    }

    @Override
    protected void placeNavigationItems() {
        scheme.bindItem('B', config.getLang().getBuyChunks().easyItem()
                .replaceAnywhere("%chunks%", user.getAvailableChunks() + "").getBase(), e -> {
            config.getLang().getBuyChunks().getSound().play(user);
        });

        scheme.bindItem('M', config.getLang().getManageProfiles().itemStack(), e -> {
            config.getLang().getManageProfiles().getSound().play(user.asPlayer());
            new ProfilesMenu(user.asPlayer());
        });

        scheme.bindItem('S', config.getLang().getSettings().easyItem().skullOwner(user.asPlayer()).getBase(), e -> {
            config.getLang().getSettings().getSound().play(user);
            new SettingsMenu(user.asPlayer());
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
                .replaceAnywhere("%previous%", previous)
                .replaceAnywhere("%current%", Text.text(current))
                .replaceAnywhere("%next%", Text.text(next)).getBase(), e -> {
            config.getLang().getSwitchProfile().getSound().play(user);

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
                        Optional<IClaim> claim = claimManager.getClaimAt(chunk);
                        if (claim.orElseThrow().getOwner().equals(user)) {
                            config.getLang().getChunks().getClaimedYou().getSound().play(user);
                            return;
                        }

                        user.sendMessage("&cChunk is already claimed!");
                        config.getLang().getChunks().getClaimedOther().getSound().play(user);
                    }
                    case WORLD_DISABLED -> {
                        user.sendMessage("&cClaiming is disabled in this world!");
                        config.getLang().getChunks().getWorldDisabled().getSound().play(user);
                    }
                    case BLOCKED_WORLD_GUARD -> {
                        user.sendMessage("&cThis chunk is protected by world guard!");
                        config.getLang().getChunks().getRestrictedRegion().getSound().play(user);
                    }
                    case BLOCKED_ZONE_BORDER -> {
                        user.sendMessage("&cYou cannot claim within 3 chunks of the zone border!");
                        config.getLang().getChunks().getZoneBorder().getSound().play(user);
                    }
                }
            });
            ++index;
        }
    }

    private void claimChunk(IClaimChunk chunk) {
        if (user.getAvailableChunks() <= 0) {
            user.sendMessage(Lang.i().getClaimMessages().getFail().getNotEnoughChunks());
            config.getLang().getChunks().getUnclaimedExpensive().getSound().play(user);
            return;
        }

        user.getClaim().claimChunk(chunk);
        placeChunkItems();
        config.getLang().getChunks().getUnclaimed().getSound().play(user);
    }

    private ItemStack getChunkItem(int index, IClaimChunk chunk) {
        IClaim claim = claimManager.getClaimAt(chunk).orElse(null);
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
                        .replaceAnywhere("%claim_profile%", Text.text(claim.getProfiles().get(chunk.getProfileId()).getName()))
                        .replaceAnywhere("%owner%", claim.getOwner().getName())
                        .replaceInLore("%formatted_time%", Time.formatTimeSince(chunk.getClaimedSince())).getBase();
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

        if (index == 22) return centerChunkItem(stack);
        return stack;
    }

    private ItemStack centerChunkItem(ItemStack stack) {
        stack.setType(config.getLang().getChunks().getCurrent().icon());
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return stack;

        List<Component> newLore = new ArrayList<>(Text.list(config.getLang().getChunks().getCurrent().loreHeader()));
        List<Component> oldLore = meta.lore();
        if (oldLore != null) newLore.addAll(oldLore);

        meta.lore(newLore);
        meta.setCustomModelData(config.getLang().getChunks().getCurrent().modelData());
        stack.setItemMeta(meta);
        return stack;
    }

    public List<IClaimChunk> getNearByChunksRelativeToPlayerAndMenu() {
        List<IClaimChunk> chunkList = new LinkedList<>();
        BlockFace facing = user.asPlayer().getFacing();
        int playerChunkX = user.getPosition().toChunkLoc().getX();
        int playerChunkZ = user.getPosition().toChunkLoc().getX();
        String worldName = user.getPosition().getWorld();
        switch (facing) {
            case NORTH:
                for (int z = -2; z <= 2; z++) { // normal z-order for SOUTH
                    for (int x = -4; x <= 4; x++) {
                        int chunkX = playerChunkX + x, chunkZ = playerChunkZ + z;
                        chunkList.add(claimManager.getChunkAt(chunkX, chunkZ, worldName));
                    }
                }
                break;
            case EAST:
                for (int x = 2; x >= -2; x--) {
                    for (int z = -4; z <= 4; z++) {
                        int chunkX = playerChunkX + x, chunkZ = playerChunkZ + z;
                        chunkList.add(claimManager.getChunkAt(chunkX, chunkZ, worldName));
                    }
                }
                break;
            case SOUTH:
                for (int z = 2; z >= -2; z--) { // reverse z-order for NORTH
                    for (int x = 4; x >= -4; x--) {
                        int chunkX = playerChunkX + x, chunkZ = playerChunkZ + z;
                        chunkList.add(claimManager.getChunkAt(chunkX, chunkZ, worldName));
                    }
                }
                break;
            case WEST:
                for (int x = -2; x <= 2; x++) {
                    for (int z = 4; z >= -4; z--) {
                        int chunkX = playerChunkX + x, chunkZ = playerChunkZ + z;
                        chunkList.add(claimManager.getChunkAt(chunkX, chunkZ, worldName));
                    }
                }
                break;
        }
        return chunkList;
    }
}