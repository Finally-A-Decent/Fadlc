package info.preva1l.fadlc.models.user.settings;

import org.bukkit.Material;

import java.util.List;

public interface Setting<C> {
    C getState();

    void setState(C state);

    String getName();

    List<String> getDescription();

    Material getIcon();
}