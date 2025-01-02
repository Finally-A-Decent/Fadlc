package info.preva1l.fadlc.config.sounds;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.utils.config.BasicConfig;
import lombok.experimental.UtilityClass;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Blocking;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class Sounds {
    private final BasicConfig soundsFile = new BasicConfig(Fadlc.i(), "sounds.yml");
    private Map<String, SoundType> sounds = new ConcurrentHashMap<>();

    public void update() {
        sounds = getSoundsFromFile();
    }

    public SoundType getSound(String name) {
        return sounds.get(name);
    }

    @Blocking
    public Map<String, SoundType> getSoundsFromFile() {
        Map<String, SoundType> list = new HashMap<>();
        for (String key : soundsFile.getConfiguration().getKeys(false)) {
            if (soundsFile.getString(key + ".value").equals("none")) continue;
            String bukkit = soundsFile.getString(key + ".value");
            float volume = soundsFile.getFloat(key + ".volume");
            float pitch = soundsFile.getFloat(key + ".pitch");

            list.put(key, new SoundType(key, bukkit, volume, pitch));
        }
        return list;
    }

    public void playSound(Player player, SoundType soundType) {
        if (soundType == null) return;
        player.playSound(player.getLocation(), soundType.getBukkit(), SoundCategory.MASTER, soundType.getVolume(), soundType.getPitch());
    }
}
