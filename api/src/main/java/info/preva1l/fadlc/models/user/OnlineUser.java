package info.preva1l.fadlc.models.user;

import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.user.settings.InputSetting;
import info.preva1l.fadlc.models.user.settings.Setting;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    default <T> T getSetting(Supplier<Class<Setting<T>>> clazz, T def) {
        return getSetting(clazz.get(), def);
    }

    <T> T getSetting(Class<? extends Setting<T>> clazz, T def);

    <T> Optional<Setting<T>> getSettingAccess(Class<? extends Setting<T>> clazz);

    default <T> T updateSetting(T object, Supplier<Class<Setting<T>>> clazz) {
        return updateSetting(object, clazz.get());
    }

    <T> T updateSetting(T object, Class<? extends Setting<T>> clazz);

    <T> void requestInput(InputSetting<T> type, Consumer<T> callback);

    <T> void requestInput(String prompt, @Nullable String placeholder, Class<T> type, Consumer<T> callback);

    IPosition getPosition();
}
