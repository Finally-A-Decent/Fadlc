package info.preva1l.fadlc.config.menus.lang;

import info.preva1l.fadlc.config.misc.ConfigurableItem;
import lombok.Getter;
import org.bukkit.Material;

import java.util.List;

/**
 * Created on 1/04/2025
 *
 * @author Preva1l
 */
@Getter
@SuppressWarnings("FieldMayBeFinal")
public abstract class PaginatedLang extends MenuLang {
    private ConfigurableItem previous = new ConfigurableItem(
            Material.ARROW, 0, "click",
            "&3Previous Page", List.of("&7\u2192 Click to go to the previous page")
    );
    private ConfigurableItem next = new ConfigurableItem(
            Material.ARROW, 0, "click",
            "&3Next Page", List.of("&7\u2192 Click to go to the next page")
    );
}
