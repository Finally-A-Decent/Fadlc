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
public abstract class MenuLang {
    private ConfigurableItem filler = new ConfigurableItem(
            Material.BLACK_STAINED_GLASS_PANE, 0, "",
            "&r ", List.of("&8I <3 Fadlc")
    );
}
