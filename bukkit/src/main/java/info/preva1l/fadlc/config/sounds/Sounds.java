package info.preva1l.fadlc.config.sounds;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.utils.config.BasicConfig;
import lombok.experimental.UtilityClass;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class Sounds {
    private Map<String, SoundType> sounds = new ConcurrentHashMap<>();
    private final BasicConfig soundsFile = new BasicConfig(Fadlc.i(), "sounds.yml");

    public void update() {
        sounds = getSoundsFromFile();
    }

    public SoundType getSound(String name) {
        return sounds.get(name);
    }

    public Map<String, SoundType> getSoundsFromFile() {
        Map<String, SoundType> list = new HashMap<>();
        for (String key : soundsFile.getConfiguration().getKeys(false)) {
            if (soundsFile.getString(key + ".value").equals("none")) continue;
            Sound bukkit = Sound.valueOf(soundsFile.getString(key + ".value"));
            float volume = soundsFile.getFloat(key + ".volume");
            float pitch = soundsFile.getFloat(key + ".pitch");

            list.put(key, new SoundType(key, bukkit, volume, pitch));
        }
        return list;
    }

    public void playSound(Player player, SoundType soundType) {
        player.playSound(player.getLocation(), soundType.getBukkit(), SoundCategory.MASTER, soundType.getVolume(), soundType.getPitch());
    }
}
