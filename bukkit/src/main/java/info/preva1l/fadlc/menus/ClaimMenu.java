package info.preva1l.fadlc.menus;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.Menus;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.managers.LayoutManager;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.menus.lib.FastInv;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.models.ChunkStatus;
import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.utils.Text;
import info.preva1l.fadlc.utils.sounds.Sounds;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ClaimMenu extends FastInv {
    private final Player player;
    private final Audience audience;
    private final OnlineUser user;

    public ClaimMenu(Player player) {
        super(54, LayoutManager.MenuType.CLAIM);
        this.player = player;
        this.audience = Fadlc.i().getAudiences().player(player);
        this.user = UserManager.getInstance().getUser(player.getUniqueId()).orElseThrow();

        placeFillerItems();
        placeNavigationButtons();
        placeChunkItems();
    }

    private void placeFillerItems() {
        List<Integer> fillerSlots = getLayout().fillerSlots();
        if (!fillerSlots.isEmpty()) {
            setItems(fillerSlots.stream().mapToInt(Integer::intValue).toArray(), Menus.getInstance().getFiller().asItemStack());
        }
    }

    private void placeNavigationButtons() {
        int buyChunksSlot = getLayout().buttonSlots().get(LayoutManager.ButtonType.BUY_CHUNKS);
        int changeProfileSlot = getLayout().buttonSlots().get(LayoutManager.ButtonType.CHANGE_CLAIMING_PROFILE);
        int manageProfilesSlot = getLayout().buttonSlots().get(LayoutManager.ButtonType.MANAGE_PROFILES);

        setItem(buyChunksSlot, getLang().getItemStack(""));
    }

    private void placeChunkItems() {
        int index = 0;
        for (IClaimChunk chunk : getNearByChunksRelativeToPlayerAndMenu()) {
            if (index == 45) return;
            ChunkStatus chunkStatus = chunk.getStatus();

            setItem(index, getChunkItem(index, chunk), e -> {
                switch (chunkStatus) {
                    case CLAIMABLE -> claimChunk(chunk);
                    case ALREADY_CLAIMED -> {
                        Optional<IClaim> claim = ClaimManager.getInstance().getClaimAt(chunk);
                        if (claim.orElseThrow().getOwner().equals(user)) {
                            Sounds.playSound(player, getLang().getSound("chunk-sounds.manage-chunk"));
                            return;
                        }

                        audience.sendActionBar(Text.modernMessage("&cChunk is already claimed!"));
                        Sounds.playSound(player, getLang().getSound("chunk-sounds.already-claimed"));
                    }
                    case WORLD_DISABLED -> {
                        audience.sendActionBar(Text.modernMessage("&cClaiming is disabled in this world!"));
                        Sounds.playSound(player, getLang().getSound("chunk-sounds.cant-claim.other"));
                    }
                    case BLOCKED_WORLD_GUARD -> {
                        audience.sendActionBar(Text.modernMessage("&cThis chunk is protected by world guard!"));
                        Sounds.playSound(player, getLang().getSound("chunk-sounds.cant-claim.other"));
                    }
                    case BLOCKED_ZONE_BORDER -> {
                        audience.sendActionBar(Text.modernMessage("&cYou cannot claim within 3 chunks of the zone border!"));
                        Sounds.playSound(player, getLang().getSound("chunk-sounds.cant-claim.other"));
                    }
                }
            });
            ++index;
        }
    }

    private void claimChunk(IClaimChunk chunk) {
        user.getClaim().claimChunk(chunk);
        placeChunkItems();
        Sounds.playSound(player, getLang().getSound("chunk-sounds.claim-created"));
    }

    private ItemStack getChunkItem(int index, IClaimChunk chunk) {
        ItemBuilder itemBuilder = chunkMaterial(index, chunk);
        itemBuilder = chunkName(itemBuilder, chunk);
        itemBuilder = chunkLore(itemBuilder, chunk);

        return itemBuilder.build();
    }

    private ItemBuilder chunkMaterial(int index, IClaimChunk chunk) {
        ItemBuilder itemBuilder = null;
        switch (chunk.getStatus()) {
            case CLAIMABLE -> itemBuilder = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE);
            case ALREADY_CLAIMED -> {
                Optional<IClaim> claim = ClaimManager.getInstance().getClaimAt(chunk);
                itemBuilder = new ItemBuilder(Material.PLAYER_HEAD)
                        .skullOwner(Bukkit.getOfflinePlayer(claim.orElseThrow().getOwner().getUniqueId()));
            }
            case WORLD_DISABLED -> itemBuilder = new ItemBuilder(Material.RED_STAINED_GLASS_PANE);
            case BLOCKED_ZONE_BORDER -> itemBuilder = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE);
            case BLOCKED_WORLD_GUARD -> itemBuilder = new ItemBuilder(Material.RED_STAINED_GLASS_PANE);
        }

        if (index == 22) {
            itemBuilder = new ItemBuilder(getLang().getAsMaterial("chunks.current-chunk.icon", Material.NETHER_STAR))
                    .addLore(getLang().getStringFormatted("chunks.current-chunk.lore-header"));
        }

        return itemBuilder;
    }

    private ItemBuilder chunkName(ItemBuilder itemBuilder, IClaimChunk chunk) {
        return switch (chunk.getStatus()) {
            case CLAIMABLE -> itemBuilder.name(Text.legacyMessage("&7Unclaimed Chunk"));
            case ALREADY_CLAIMED -> {
                Optional<IClaim> claim = ClaimManager.getInstance().getClaimAt(chunk);
                yield itemBuilder.name(Text.legacyMessage(claim.orElseThrow().getProfile(chunk).orElseThrow().getName()));
            }
            case WORLD_DISABLED -> itemBuilder.name(Text.legacyMessage("&c&oClaiming is disabled in this world!"));
            case BLOCKED_ZONE_BORDER -> itemBuilder.name(Text.legacyMessage("&c&oYou cannot claim near the border!"));
            case BLOCKED_WORLD_GUARD ->
                    itemBuilder.name(Text.legacyMessage("&c&oYou cannot claim in protected areas!"));
        };
    }

    private ItemBuilder chunkLore(ItemBuilder itemBuilder, IClaimChunk chunk) {
        return switch (chunk.getStatus()) {
            case CLAIMABLE -> itemBuilder.addLore(Text.legacyList(List.of(
                    "&7&l‣ &3Chunk: &f%s, %s".formatted(chunk.getChunkX(), chunk.getChunkZ()),
                    "&7&l‣ &3Cost: &f1 Claim Chunk &7(You have: &f%s&7)".formatted(user.getAvailableChunks()),
                    "",
                    "&a→ Click &3to claim this chunk!")));
            case ALREADY_CLAIMED -> {
                Optional<IClaim> claim = ClaimManager.getInstance().getClaimAt(chunk);
                if (claim.orElseThrow().getOwner().equals(user)) {
                    yield itemBuilder.addLore(Text.legacyList(List.of(
                            "&7&l‣ &3Owner: &f%s".formatted(claim.orElseThrow().getOwner().getName()),
                            "&7&l‣ &3Chunk: &f%s, %s".formatted(chunk.getChunkX(), chunk.getChunkZ()),
                            "&7&l‣ &3Claimed: &f%s".formatted(chunk.getClaimedSince()),
                            "",
                            "&a→ Click &3to manage this claim!")));
                }
                yield itemBuilder.addLore(Text.legacyList(List.of(
                        "&7&l‣ &3Owner: &f%s".formatted(claim.orElseThrow().getOwner().getName()),
                        "&7&l‣ &3Chunk: &f%s, %s".formatted(chunk.getChunkX(), chunk.getChunkZ()),
                        "&7&l‣ &3Claimed: &f%s".formatted(chunk.getClaimedSince()),
                        "")));
            }
            case WORLD_DISABLED -> itemBuilder.name(Text.legacyMessage("&c&oClaiming is disabled in this world!"));
            case BLOCKED_ZONE_BORDER -> itemBuilder.name(Text.legacyMessage("&c&oYou cannot claim near the border!"));
            case BLOCKED_WORLD_GUARD ->
                    itemBuilder.name(Text.legacyMessage("&c&oYou cannot claim in protected areas!"));
        };
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
                                .getChunkAtChunk(chunkX, chunkZ, player.getWorld().getName()));
                    }
                }
                break;
            case EAST:
                for (int x = 2; x >= -2; x--) {
                    for (int z = -4; z <= 4; z++) {
                        int chunkX = playerChunkX + x, chunkZ = playerChunkZ + z;
                        chunkList.add(ClaimManager.getInstance()
                                .getChunkAtChunk(chunkX, chunkZ, player.getWorld().getName()));
                    }
                }
                break;
            case SOUTH:
                for (int z = 2; z >= -2; z--) { // reverse z-order for NORTH
                    for (int x = 4; x >= -4; x--) {
                        int chunkX = playerChunkX + x, chunkZ = playerChunkZ + z;
                        chunkList.add(ClaimManager.getInstance()
                                .getChunkAtChunk(chunkX, chunkZ, player.getWorld().getName()));
                    }
                }
                break;
            case WEST:
                for (int x = -2; x <= 2; x++) {
                    for (int z = 4; z >= -4; z--) {
                        int chunkX = playerChunkX + x, chunkZ = playerChunkZ + z;
                        chunkList.add(ClaimManager.getInstance()
                                .getChunkAtChunk(chunkX, chunkZ, player.getWorld().getName()));
                    }
                }
                break;
        }
        return chunkList;
    }
}
