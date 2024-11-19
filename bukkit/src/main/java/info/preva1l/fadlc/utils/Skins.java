package info.preva1l.fadlc.utils;

import info.preva1l.fadlc.Fadlc;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@UtilityClass
public class Skins {
    private final String FILE_NAME = "skins.cache";
    private File file;
    private final Map<UUID, String> textureMap = new HashMap<>();

    public void load() {
        try {
            file = new File(Fadlc.i().getDataFolder(), FILE_NAME);
            if (!file.exists()) file.createNewFile();
            List<String> lines = Files.readAllLines(file.toPath());

            for (String line : lines) {
                String[] parts = line.split(":");
                UUID uuid = UUID.fromString(parts[0]);
                String texture = parts[1];
                textureMap.put(uuid, texture);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        try {
            if (file != null) {
                List<String> lines = new ArrayList<>();
                textureMap.forEach((uuid, string) -> lines.add(uuid + ":" + string));
                Files.write(file.toPath(), lines);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTexture(UUID uuid) {
        String cached = textureMap.get(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (cached != null) {
            if (player == null) {
                return cached;
            }

            String curr = textureFromPlayer(player);
            if (!curr.equals(cached)) {
                textureMap.put(uuid, curr);
                return curr;
            }
            return cached;
        }

        if (player == null) {
            return "";
        }

        String curr = textureFromPlayer(player);
        textureMap.put(player.getUniqueId(), curr);
        return curr;
    }

    public String textureFromPlayer(Player player) {
        return player.getPlayerProfile().getProperties().stream().filter(texture -> texture.getName().equals("textures")).findFirst().orElseThrow().getValue();
    }
}