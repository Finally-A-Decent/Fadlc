package info.preva1l.fadlc.config.misc;

import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.utils.Text;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ConfigurableItem {
    Material getMaterial();
    int getModelData();
    String getName();
    List<String> getLore();

    default ItemStack asItemStack() {
        return new ItemBuilder(getMaterial())
                .modelData(getModelData())
                .name(Text.legacyMessage(getName()))
                .lore(Text.legacyList(getLore())).build();
    }
}
