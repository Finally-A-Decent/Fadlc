package info.preva1l.fadlc.config.misc;

import info.preva1l.fadlc.config.sounds.SoundType;
import info.preva1l.fadlc.config.sounds.Sounds;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.utils.Text;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public record ConfigurableItem(Material material, int modelData, String sound, String name, List<String> lore) {
    public ItemStack itemStack() {
        return new ItemBuilder(material())
                .modelData(modelData())
                .name(Text.text(name()))
                .lore(Text.list(lore())).build();
    }

    public EasyItem easyItem() {
        return new EasyItem(itemStack());
    }

    public SoundType getSound() {
        return Sounds.getSound(sound);
    }
}
