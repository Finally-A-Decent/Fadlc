package info.preva1l.fadlc.registry;

import info.preva1l.fadlc.models.claim.settings.GroupSetting;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class GroupSettingsRegistry {
    public static final Supplier<GroupSetting> PLACE_BLOCKS = () -> get("place_blocks");
    public static final Supplier<GroupSetting> BREAK_BLOCKS = () -> get("break_blocks");
    public static final Supplier<GroupSetting> USE_DOORS = () -> get("use_doors");
    public static final Supplier<GroupSetting> USE_BUTTONS = () -> get("use_buttons");
    public static final Supplier<GroupSetting> ENTER = () -> get("enter");

    private static Map<String, GroupSetting> settings = new ConcurrentHashMap<>();

    public static void register(GroupSetting value) {
        if (settings == null) {
            settings = new ConcurrentHashMap<>();
        }
        settings.put(value.getId().toLowerCase(), value);
    }

    public static GroupSetting get(String name) {
        if (settings == null) {
            settings = new ConcurrentHashMap<>();
        }
        return settings.get(name.toLowerCase());
    }

    public static Collection<GroupSetting> getAll() {
        if (settings == null) {
            settings = new ConcurrentHashMap<>();
        }
        return settings.values();
    }

    @ApiStatus.Obsolete
    public static GroupSetting[] values() {
        return getAll().toArray(new GroupSetting[0]);
    }
}
