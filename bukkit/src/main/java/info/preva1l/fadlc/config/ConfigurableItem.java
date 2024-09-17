package info.preva1l.fadlc.config;

import org.bukkit.Material;

import java.util.List;

public interface ConfigurableItem {
    Material getMaterial();
    int getModelData();
    String getName();
    List<String> getLore();
}