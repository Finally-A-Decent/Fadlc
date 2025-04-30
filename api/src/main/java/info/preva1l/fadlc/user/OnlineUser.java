package info.preva1l.fadlc.user;

import info.preva1l.fadlc.claim.IClaim;
import info.preva1l.fadlc.claim.IClaimProfile;
import info.preva1l.fadlc.claim.settings.GroupSetting;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.models.Replacer;
import info.preva1l.fadlc.user.settings.InputSetting;
import info.preva1l.fadlc.user.settings.Setting;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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

    List<Setting<?>> getSettings();

    default <T> T getSetting(Supplier<Class<Setting<T>>> clazz, T def) {
        return getSetting(clazz.get(), def);
    }

    <T> T getSetting(Class<? extends Setting<T>> clazz, T def);

    <T> Optional<Setting<T>> getSettingAccess(Class<? extends Setting<T>> clazz);

    default <T> void updateSetting(T object, Supplier<Class<Setting<T>>> clazz) {
        updateSetting(object, clazz.get());
    }

    <T> void updateSetting(T object, Class<? extends Setting<T>> clazz);

    boolean canPerformAction(IPosition location, GroupSetting setting);

    boolean canPerformAction(IPosition location, Supplier<GroupSetting> setting);

    void sendMessage(String message, Replacer... replacements);

    void sendMessage(String message, boolean prefixed, Replacer... replacements);

    <T> void requestInput(InputSetting<T> type, Consumer<@Nullable T> callback);

    void requestInput(String prompt, @Nullable String placeholder, Consumer<@Nullable String> callback);

    <T> void requestInput(String prompt, @Nullable String placeholder, Class<T> type, Consumer<@Nullable T> callback);

    IPosition getPosition();

    CompletableFuture<Boolean> teleport(IPosition position);
}
