package info.preva1l.fadlc.menus.lib;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import info.preva1l.fadlc.persistence.Skins;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "deprecation"})
public class ItemBuilder {
    private final ItemStack item;
    private ItemMeta cachedMeta;

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder(ItemStack item) {
        this.item = Objects.requireNonNull(item, "item");
    }

    public static ItemBuilder copyOf(ItemStack item) {
        return new ItemBuilder(item.clone());
    }

    private ItemMeta getMeta() {
        if (cachedMeta == null) {
            cachedMeta = item.getItemMeta();
        }
        return cachedMeta;
    }

    private void applyMeta() {
        if (cachedMeta != null) {
            item.setItemMeta(cachedMeta);
            cachedMeta = null; // Prevent stale meta reference
        }
    }

    public ItemBuilder edit(Consumer<ItemStack> function) {
        function.accept(this.item);
        return this;
    }

    public ItemBuilder meta(Consumer<ItemMeta> metaConsumer) {
        ItemMeta meta = getMeta();
        if (meta != null) {
            metaConsumer.accept(meta);
        }
        return this;
    }

    public <T extends ItemMeta> ItemBuilder meta(Class<T> metaClass, Consumer<T> metaConsumer) {
        ItemMeta meta = getMeta();
        if (metaClass.isInstance(meta)) {
            metaConsumer.accept(metaClass.cast(meta));
        }
        return this;
    }

    public ItemBuilder type(Material material) {
        return edit(item -> item.setType(material));
    }

    public ItemBuilder durability(int data) {
        return durability((short) data);
    }

    public ItemBuilder durability(short durability) {
        return edit(item -> item.setDurability(durability));
    }

    public ItemBuilder amount(int amount) {
        return edit(item -> item.setAmount(amount));
    }

    public ItemBuilder enchant(Enchantment enchantment) {
        return enchant(enchantment, 1);
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        return meta(meta -> meta.addEnchant(enchantment, level, true));
    }

    public ItemBuilder removeEnchant(Enchantment enchantment) {
        return meta(meta -> meta.removeEnchant(enchantment));
    }

    public ItemBuilder removeEnchants() {
        return meta(meta -> meta.getEnchants().keySet().forEach(meta::removeEnchant));
    }

    public ItemBuilder name(String name) {
        return meta(meta -> meta.setDisplayName(name));
    }

    public ItemBuilder name(Component name) {
        return meta(meta -> meta.displayName(name));
    }

    public ItemBuilder lore(String lore) {
        return lore(Collections.singletonList(lore));
    }

    public ItemBuilder lore(String... lore) {
        return lore(Arrays.asList(lore));
    }

    public ItemBuilder lore(Collection<String> lore) {
        return meta(meta -> meta.setLore(new ArrayList<>(lore)));
    }

    public ItemBuilder lore(List<Component> lore) {
        return meta(meta -> meta.lore(lore));
    }

    public ItemBuilder addLore(String line) {
        return meta(meta -> {
            List<String> lore = meta.getLore();
            if (lore == null) {
                meta.setLore(Collections.singletonList(line));
            } else {
                lore.add(line);
                meta.setLore(lore);
            }
        });
    }

    public ItemBuilder addLore(String... lines) {
        return addLore(Arrays.asList(lines));
    }

    public ItemBuilder addLore(List<String> lines) {
        return meta(meta -> {
            List<String> lore = meta.getLore();
            if (lore == null) {
                meta.setLore(new ArrayList<>(lines));
            } else {
                lore.addAll(lines);
                meta.setLore(lore);
            }
        });
    }

    public ItemBuilder flags(ItemFlag... flags) {
        return meta(meta -> meta.addItemFlags(flags));
    }

    public ItemBuilder flags() {
        return flags(ItemFlag.values());
    }

    public ItemBuilder removeFlags(ItemFlag... flags) {
        return meta(meta -> meta.removeItemFlags(flags));
    }

    public ItemBuilder removeFlags() {
        return removeFlags(ItemFlag.values());
    }

    public ItemBuilder armorColor(Color color) {
        return meta(LeatherArmorMeta.class, meta -> meta.setColor(color));
    }

    public ItemBuilder skullOwner(UUID player) {
        if (this.item.getType() != Material.PLAYER_HEAD) return this;
        return meta(SkullMeta.class, meta -> {
            PlayerProfile profile = Bukkit.getServer().createProfile(player);
            profile.setProperty(new ProfileProperty("textures", Skins.getTexture(player)));
            meta.setPlayerProfile(profile);
        });
    }

    public ItemBuilder modelData(int modelData) {
        return meta(meta -> meta.setCustomModelData(modelData));
    }

    public ItemStack build() {
        applyMeta();
        return this.item;
    }
}