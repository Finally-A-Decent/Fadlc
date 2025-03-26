package info.preva1l.fadlc.models.user;

import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.user.settings.Setting;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.NonExtendable
public interface OnlineUser extends User {
    Player asPlayer();

    int getAvailableChunks();

    void setAvailableChunks(int newAmount);

    IClaimProfile getClaimWithProfile();

    void setClaimWithProfile(IClaimProfile newProfile);

    IClaim getClaim();

    void sendMessage(String message);

    void sendMessage(String message, boolean prefixed);

    void sendMessage(@NotNull Component component);

    void sendMessage(@NotNull Component component, boolean prefixed);

    List<Setting<?>> getSettings();

    <T> T getSetting(Class<? extends Setting<T>> clazz, T def);

    <T> Setting<?> getSettingAccess(Class<? extends Setting<T>> clazz);

    <T> T updateSetting(T object, Class<? extends Setting<T>> clazz);

    @ApiStatus.Internal
    void putSettingIfEmpty(Object object, Class<? extends Setting<?>> clazz);
}
