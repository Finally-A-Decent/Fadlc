package info.preva1l.fadlc.utils.config;

import info.preva1l.fadlc.config.sounds.SoundType;
import info.preva1l.fadlc.config.sounds.Sounds;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.utils.Logger;
import info.preva1l.fadlc.utils.Text;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LanguageConfig {
    private final ConfigurationSection superSection;

    public LanguageConfig(@NotNull ConfigurationSection superSection) {
        this.superSection = superSection;
    }

    public int getInt(String path, int def) {
        return superSection.getInt(path, def);
    }

    public int getInt(String path) {
        return superSection.getInt(path, 0);
    }

    public @NotNull Material getAsMaterial(String path) {
        return getAsMaterial(path, Material.APPLE);
    }

    public @NotNull Material getAsMaterial(String path, Material def) {
        Material material;
        String s = superSection.getString(path);
        if (s == null || s.isEmpty()) {
            throw new RuntimeException("No value at path %s".formatted(path));
        }
        try {
            material = Material.valueOf(s.toUpperCase());
        } catch (EnumConstantNotPresentException | IllegalArgumentException e) {
            material = def;
            Logger.severe("-----------------------------");
            Logger.severe("Config Incorrect!");
            Logger.severe("Material: " + s);
            Logger.severe("Does Not Exist!");
            Logger.severe("Defaulting to " + def.toString());
            Logger.severe("-----------------------------");
        }
        return material;
    }

    public @NotNull String getStringFormatted(String path) {
        return getStringFormatted(path, "Regenerate Config");
    }

    public @NotNull String getString(String path) {
        return getString(path, "Regenerate Config");
    }

    public @NotNull String getString(String path, String def) {
        return superSection.getString(path, def);
    }

    public @NotNull String getStringFormatted(String path, String def) {
        return Text.legacyMessage(getString(path, def));
    }

    public @NotNull List<String> getLore(String path) {
        List<String> str = superSection.getStringList(path);
        List<String> newStr = new ArrayList<>();
        if (str.isEmpty() || str.getFirst().equals(path) || str.getFirst().equals("null")) {
            return Collections.emptyList();
        }
        for (String s : str) {
            newStr.add(Text.legacyMessage(s));
        }
        return newStr;
    }

    public @NotNull List<String> getLore(String path, List<String> def) {
        List<String> str = superSection.getStringList(path);
        List<String> newStr = new ArrayList<>();
        if (str.isEmpty() || str.getFirst().equals(path) || str.getFirst().equals("null")) {
            for (String s : str) {
                newStr.add(Text.legacyMessage(s));
            }
            return newStr;
        }
        for (String s : str) {
            newStr.add(Text.legacyMessage(s));
        }
        return newStr;
    }

    public EasyItem getItemStack(String configSection) {
        return new EasyItem(new ItemBuilder(getAsMaterial(configSection + ".icon"))
                .name(getStringFormatted(configSection + ".name"))
                .lore(getLore(configSection + ".lore"))
                .modelData(getInt(configSection + ".model-data")).build());
    }

    public SoundType getSound(String path) {
        SoundType soundType;
        String s = superSection.getString(path);
        if (s == null || s.isEmpty()) {
            throw new RuntimeException("No value at path %s".formatted(path));
        }
        try {
            soundType = Sounds.getSound(s);
        } catch (EnumConstantNotPresentException | IllegalArgumentException e) {
            soundType = new SoundType("DEFAULT", Sound.UI_BUTTON_CLICK, 1F, 1.2F);
            Logger.severe("-----------------------------");
            Logger.severe("Config Incorrect!");
            Logger.severe("Sound: " + s);
            Logger.severe("Does Not Exist! Chunk sounds.yml");
            Logger.severe("Defaulting to a click sound!");
            Logger.severe("-----------------------------");
        }
        return soundType;
    }
}