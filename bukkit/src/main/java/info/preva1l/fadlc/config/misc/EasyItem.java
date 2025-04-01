package info.preva1l.fadlc.config.misc;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import info.preva1l.fadlc.models.Tuple;
import info.preva1l.fadlc.persistence.Skins;
import info.preva1l.fadlc.utils.Text;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.intellij.lang.annotations.RegExp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class EasyItem {
    private final ItemStack base;
    private ItemMeta cachedMeta;
    private Component cachedName;
    private List<Component> cachedLore;

    private ItemMeta getMeta() {
        if (cachedMeta == null) {
            cachedMeta = base.getItemMeta();
        }
        return cachedMeta;
    }

    private List<Component> getCachedLore() {
        if (cachedLore == null) {
            cachedLore = Optional.ofNullable(getMeta().lore()).map(ArrayList::new).orElse(new ArrayList<>());
        }
        return cachedLore;
    }

    private Component getCachedName() {
        if (cachedName == null) {
            cachedName = getMeta().displayName();
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

    public EasyItem replaceInName(@RegExp String match, Component replacement) {
        cachedName = Text.replace(getCachedName(), Tuple.of(match, replacement));
        return this;
    }

    public EasyItem replaceInLore(@RegExp String match, String replacement) {
        replaceInLore(match, Text.text(replacement));
        return this;
    }

    public EasyItem replaceInLore(@RegExp String match, Component replacement) {
        cachedLore = Text.replace(getCachedLore(), Tuple.of(match, replacement));
        return this;
    }

    public EasyItem replaceAnywhere(@RegExp String match, String replacement) {
        return replaceAnywhere(match, Text.text(replacement));
    }

    public EasyItem replaceAnywhere(@RegExp String match, Component replacement) {
        return replaceInName(match, replacement).replaceInLore(match, replacement);
    }

    public ItemStack getBase() {
        if (cachedMeta != null) {
            if (cachedLore != null) {
                cachedMeta.lore(cachedLore.isEmpty() ? null : new ArrayList<>(cachedLore));
                cachedLore = null;
            }
            if (cachedName != null) {
                cachedMeta.displayName(cachedName);
                cachedName = null;
            }
            base.setItemMeta(cachedMeta);
            cachedMeta = null;
        }
        return base;
    }
}
