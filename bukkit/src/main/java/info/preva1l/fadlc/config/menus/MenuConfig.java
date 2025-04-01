package info.preva1l.fadlc.config.menus;

import info.preva1l.fadlc.config.menus.lang.MenuLang;
import info.preva1l.fadlc.utils.Text;
import net.kyori.adventure.text.Component;

import java.util.List;

public interface MenuConfig<T extends MenuLang> {
    String getTitle();

    default Component title() {
        return Text.text(getTitle());
    }

    T getLang();

    default int getSize() {
        return getLayout().size();
    }

    List<String> getLayout();
}
