package info.preva1l.fadlc.claim.registry;

import info.preva1l.fadlc.claim.settings.GroupSetting;
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

    private static final Map<String, GroupSetting> settings = new ConcurrentHashMap<>();

    public static void register(GroupSetting value) {
        settings.put(value.getId().toLowerCase(), value);
    }

    public static GroupSetting get(String name) {
        return settings.get(name.toLowerCase());
    }

    public static Collection<GroupSetting> getAll() {
        return settings.values();
    }

    @ApiStatus.Obsolete
    public static GroupSetting[] values() {
        return getAll().toArray(new GroupSetting[0]);
    }
}
