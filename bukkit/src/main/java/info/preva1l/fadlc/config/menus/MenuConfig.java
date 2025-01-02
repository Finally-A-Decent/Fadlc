package info.preva1l.fadlc.config.menus;

import info.preva1l.fadlc.utils.Text;
import net.kyori.adventure.text.Component;

import java.util.List;

public interface MenuConfig {
    String getTitle();

    default Component title() {
        return Text.modernMessage(getTitle());
    }

    <T> T getLang();

    default int getSize() {
        return getLayout().size();
    }

    List<String> getLayout();
}
