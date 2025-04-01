package info.preva1l.fadlc.config.menus.lang;

import info.preva1l.fadlc.config.misc.ConfigurableItem;

/**
 * Created on 1/04/2025
 *
 * @author Preva1l
 */
public interface PaginatedLang extends MenuLang {
    ConfigurableItem getPrevious();
    ConfigurableItem getNext();
}
