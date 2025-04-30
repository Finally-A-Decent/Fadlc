package info.preva1l.fadlc.user.registry;

import info.preva1l.fadlc.user.settings.Setting;
import info.preva1l.fadlc.user.settings.values.MessageLocation;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class UserSettingsRegistry {
    public static final Supplier<Class<Setting<MessageLocation>>> MESSAGE_LOCATION = () -> get("message_location");
    public static final Supplier<Class<Setting<Boolean>>> VIEW_BORDERS = () -> get("view_borders");
    public static final Supplier<Class<Setting<Boolean>>> CLAIM_LEAVE_ENTER_NOTIFICATION = () -> get("claim_leave_enter_notification");

    private static final Map<String, Class<? extends Setting<?>>> settings = new ConcurrentHashMap<>();

    public static void register(Class<? extends Setting<?>> setting, String id) {
        settings.put(id, setting);
    }

    public static <T> Class<Setting<T>> get(String name) {
        return (Class<Setting<T>>) settings.get(name.toLowerCase());
    }

    public static Collection<Class<? extends Setting<?>>> getAll() {
        return settings.values();
    }
}
