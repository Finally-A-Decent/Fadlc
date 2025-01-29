package info.preva1l.fadlc.utils.config;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import info.preva1l.fadlc.utils.Skins;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class EasyItem {
    private final ItemStack base;
    private ItemMeta cachedMeta;
    private String cachedName;
    private List<String> cachedLore;

    private ItemMeta getMeta() {
        if (cachedMeta == null) {
            cachedMeta = base.getItemMeta();
        }
        return cachedMeta;
    }

    private List<String> getCachedLore() {
        if (cachedLore == null) {
            cachedLore = Optional.ofNullable(getMeta().getLore()).map(ArrayList::new).orElse(new ArrayList<>());
        }
        return cachedLore;
    }

    private String getCachedName() {
        if (cachedName == null) {
            cachedName = getMeta().getDisplayName();
        }
        return cachedName;
    }

    public EasyItem skullOwner(Player player) {
        return skullOwner(player.getUniqueId());
    }

    public EasyItem skullOwner(UUID owner) {
        ItemMeta meta = getMeta();
        if (meta instanceof SkullMeta sMeta) {
            PlayerProfile profile = Bukkit.getServer().createProfile(owner);
            profile.setProperty(new ProfileProperty("textures", Skins.getTexture(owner)));
            sMeta.setPlayerProfile(profile);
        }
        return this;
    }

    public EasyItem replaceInName(String match, String replacement) {
        cachedName = getCachedName().replace(match, replacement);
        return this;
    }

    public EasyItem replaceInLore(String match, String replacement) {
        List<String> lore = getCachedLore();
        lore.replaceAll(s -> s.replace(match, replacement));
        return this;
    }

    public EasyItem replaceAnywhere(String match, String replacement) {
        return replaceInName(match, replacement).replaceInLore(match, replacement);
    }

    public ItemStack getBase() {
        if (cachedMeta != null) {
            if (cachedLore != null) {
                cachedMeta.setLore(cachedLore.isEmpty() ? null : new ArrayList<>(cachedLore));
                cachedLore = null;
            }
            if (cachedName != null) {
                cachedMeta.setDisplayName(cachedName);
                cachedName = null;
            }
            base.setItemMeta(cachedMeta);
            cachedMeta = null;
        }
        return base;
    }
}
