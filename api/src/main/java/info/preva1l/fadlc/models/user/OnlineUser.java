package info.preva1l.fadlc.models.user;

import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.user.settings.SettingHolder;
import org.bukkit.entity.Player;

import java.util.List;

public interface OnlineUser extends User {
    Player asPlayer();

    int getAvailableChunks();

    void setAvailableChunks(int newAmount);

    IClaimProfile getClaimWithProfile();

    void setClaimWithProfile(IClaimProfile newProfile);

    IClaim getClaim();

    void sendMessage(String message);

    void sendMessage(String message, boolean prefixed);

    List<SettingHolder<?>> getSettings();

    <T> T getSetting(Class<T> clazz);

    <T> SettingHolder<T> getSettingHolder(Class<T> clazz);

    <T> void updateSetting(T object, Class<T> clazz);
}
