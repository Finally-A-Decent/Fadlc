package info.preva1l.fadlc.utils.config;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import info.preva1l.fadlc.utils.Skins;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class EasyItem {
    private final ItemStack base;

    public EasyItem skullOwner(Player player) {
        return skullOwner(player.getUniqueId());
    }

    public EasyItem skullOwner(UUID owner) {
        ItemMeta meta = base.getItemMeta();
        if (meta == null) return this;
        if (!(meta instanceof SkullMeta sMeta)) return this;
        PlayerProfile profile = Bukkit.getServer().createProfile(owner);
        profile.setProperty(new ProfileProperty("textures", Skins.getTexture(owner)));
        sMeta.setPlayerProfile(profile);
        base.setItemMeta(sMeta);
        return this;
    }

    public EasyItem replaceInName(String match, String replacement) {
        ItemMeta meta = base.getItemMeta();
        if (meta == null) return this;
        meta.setDisplayName(meta.getDisplayName().replace(match, replacement));
        base.setItemMeta(meta);
        return this;
    }

    public EasyItem replaceInLore(String match, String replacement) {
        ItemMeta meta = base.getItemMeta();
        if (meta == null) return this;
        List<String> formatted = new ArrayList<>();
        if (meta.getLore() == null) return this;
        for (String line : meta.getLore()) {
            formatted.add(line.replace(match, replacement));
        }
        meta.setLore(formatted);
        base.setItemMeta(meta);
        return this;
    }

    public EasyItem replaceAnywhere(String match, String replacement) {
        return replaceInName(match, replacement).replaceInLore(match, replacement);
    }
}
