package info.preva1l.fadlc.menus;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.config.sounds.Sounds;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.managers.LayoutManager;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.menus.lib.FastInv;
import info.preva1l.fadlc.models.ChunkStatus;
import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.utils.Text;
import info.preva1l.fadlc.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ClaimMenu extends FastInv {
    private final Player player;
    private final OnlineUser user;

    private final BukkitTask updateTask;

    public ClaimMenu(Player player) {
        super(54, LayoutManager.MenuType.CLAIM);
        this.player = player;
        this.user = UserManager.getInstance().getUser(player.getUniqueId()).orElseThrow();

        placeFillerItems();
        placeNavigationButtons();
        placeChunkItems();

        this.updateTask = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Fadlc.i(),
                this::placeChunkItems, 20L, 20L);
        getCloseHandlers().add((e) -> updateTask.cancel());
    }

    private void placeNavigationButtons() {
        int buyChunksSlot = getLayout().buttonSlots().get(LayoutManager.ButtonType.BUY_CHUNKS);
        int changeProfileSlot = getLayout().buttonSlots().get(LayoutManager.ButtonType.CHANGE_CLAIMING_PROFILE);
        int manageProfilesSlot = getLayout().buttonSlots().get(LayoutManager.ButtonType.MANAGE_PROFILES);
        int userSettingsSlot = getLayout().buttonSlots().get(LayoutManager.ButtonType.PLAYER_SETTINGS);

        setItem(buyChunksSlot, getLang().getItemStack("buy-chunks")
                .replaceAnywhere("%chunks%", user.getAvailableChunks() + "").getBase(), e -> {
            Sounds.playSound(player, getLang().getSound("buy-chunks.sound"));
        });

        IClaimProfile previousProfile = user.getClaim().getProfiles().get(user.getClaimWithProfile().getId() - 1);
        String previous = previousProfile == null
                ? "None"
                : previousProfile.getName();
        String current = user.getClaimWithProfile().getName();
        IClaimProfile nextProfile = user.getClaim().getProfiles().get(user.getClaimWithProfile().getId() + 1);
        String next = nextProfile == null
                ? "None"
                : nextProfile.getName();
        setItem(changeProfileSlot, getLang().getItemStack("switch-profile")
                .replaceAnywhere("%previous%", Text.legacyMessage(previous))
                .replaceAnywhere("%current%", Text.legacyMessage(current))
                .replaceAnywhere("%next%", Text.legacyMessage(next)).getBase(), e -> {
            Sounds.playSound(player, getLang().getSound("switch-profile.sound"));

            if (e.getType().isLeftClick() && previousProfile != null) {
                user.setClaimWithProfile(previousProfile);
            }

            if (e.getType().isRightClick() && nextProfile != null) {
                user.setClaimWithProfile(nextProfile);
            }

            placeNavigationButtons();
        });

        setItem(manageProfilesSlot, getLang().getItemStack("manage-profiles").getBase(), e -> {
            Sounds.playSound(player, getLang().getSound("manage-profiles.sound"));
            new ProfilesMenu(player).open(player);
        });

        setItem(userSettingsSlot, getLang().getItemStack("player-settings").skullOwner(player).getBase(), e -> {
            Sounds.playSound(player, getLang().getSound("player-settings.sound"));
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
                            Sounds.playSound(player, getLang().getSound("chunks.claimed.yours.sound"));
                            return;
                        }

                        user.sendMessage("&cChunk is already claimed!");
                        Sounds.playSound(player, getLang().getSound("chunks.claimed.other.sound"));
                    }
                    case WORLD_DISABLED -> {
                        user.sendMessage("&cClaiming is disabled in this world!");
                        Sounds.playSound(player, getLang().getSound("chunks.world-disabled.sound"));
                    }
                    case BLOCKED_WORLD_GUARD -> {
                        user.sendMessage("&cThis chunk is protected by world guard!");
                        Sounds.playSound(player, getLang().getSound("chunks.restricted-region.sound"));
                    }
                    case BLOCKED_ZONE_BORDER -> {
                        user.sendMessage("&cYou cannot claim within 3 chunks of the zone border!");
                        Sounds.playSound(player, getLang().getSound("chunks.asz-zone-border.sound"));
                    }
                }
            });
            ++index;
        }
    }

    private void claimChunk(IClaimChunk chunk) {
        if (user.getAvailableChunks() <= 0) {
            user.sendMessage(Lang.i().getClaimMessages());
            Sounds.playSound(player, getLang().getSound("chunks.unclaimed.sound.no-chunks"));
            return;
        }

        user.getClaim().claimChunk(chunk);
        placeChunkItems();
        Sounds.playSound(player, getLang().getSound("chunks.unclaimed.sound.claimed"));
    }

    private ItemStack getChunkItem(int index, IClaimChunk chunk) {
        ItemStack stack = switch (chunk.getStatus()) {
            case CLAIMABLE -> getLang().getItemStack("chunks.unclaimed")
                    .replaceAnywhere("%chunk_x%", chunk.getChunkX() + "")
                    .replaceAnywhere("%chunk_z%", chunk.getChunkZ() + "")
                    .replaceInLore("%available%", user.getAvailableChunks() + "").getBase();
            case CLAIMED -> {
                IClaim claim = ClaimManager.getInstance().getClaimAt(chunk).orElseThrow();
                if (claim.getOwner().equals(user)) {
                    yield getLang().getItemStack("chunks.claimed.yours").skullOwner(claim.getOwner().getUniqueId())
                            .replaceAnywhere("%chunk_x%", chunk.getChunkX() + "")
                            .replaceAnywhere("%chunk_z%", chunk.getChunkZ() + "")
                            .replaceAnywhere("%claim_profile%", Text.legacyMessage(claim.getProfiles().get(chunk.getProfileId()).getName()))
                            .replaceAnywhere("%owner%", claim.getOwner().getName())
                            .replaceAnywhere("%formatted_time%", Time.formatTimeSince(chunk.getClaimedSince())).getBase();
                }
                yield getLang().getItemStack("chunks.claimed.other").skullOwner(claim.getOwner().getUniqueId())
                        .replaceAnywhere("%chunk_x%", chunk.getChunkX() + "")
                        .replaceAnywhere("%chunk_z%", chunk.getChunkZ() + "")
                        .replaceAnywhere("%claim_profile%", Text.legacyMessage(claim.getProfiles().get(chunk.getProfileId()).getName()))
                        .replaceAnywhere("%owner%", claim.getOwner().getName())
                        .replaceAnywhere("%formatted_time%", Time.formatTimeSince(chunk.getClaimedSince())).getBase();
            }
            case WORLD_DISABLED -> getLang().getItemStack("chunks.world-disabled")
                    .replaceAnywhere("%chunk_x%", chunk.getChunkX() + "")
                    .replaceAnywhere("%chunk_z%", chunk.getChunkZ() + "").getBase();
            case BLOCKED_WORLD_GUARD -> getLang().getItemStack("chunks.restricted-region")
                    .replaceAnywhere("%chunk_x%", chunk.getChunkX() + "")
                    .replaceAnywhere("%chunk_z%", chunk.getChunkZ() + "").getBase();
            case BLOCKED_ZONE_BORDER -> getLang().getItemStack("asz-zone-border")
                    .replaceAnywhere("%chunk_x%", chunk.getChunkX() + "")
                    .replaceAnywhere("%chunk_z%", chunk.getChunkZ() + "").getBase();
        };


        if (index == 22) {
            stack.setType(getLang().getAsMaterial("chunks.current.icon"));
            ItemMeta meta = stack.getItemMeta();
            if (meta == null) throw new RuntimeException("chunk itemstack meta is null");
            if (meta.getLore() != null) {
                List<String> newLore = new ArrayList<>();
                newLore.addAll(getLang().getLore("chunks.current.lore-header"));
                newLore.addAll(meta.getLore());
                meta.setLore(newLore);
                meta.setCustomModelData(getLang().getInt("chunks.current.model-data"));
            }
            stack.setItemMeta(meta);
        }

        return stack;
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
