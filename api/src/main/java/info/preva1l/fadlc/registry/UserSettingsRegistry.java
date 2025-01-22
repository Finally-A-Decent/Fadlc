package info.preva1l.fadlc.registry;

import info.preva1l.fadlc.models.user.settings.MessageLocation;
import info.preva1l.fadlc.models.user.settings.Setting;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class UserSettingsRegistry {
    public static final Supplier<Class<Setting<MessageLocation>>> MESSAGE_LOCATION = () -> get("message_location");
    public static final Supplier<Class<Setting<Boolean>>> VIEW_BORDERS = () -> get("view_borders");
    public static final Supplier<Class<Setting<Boolean>>> CLAIM_ENTER_NOTIFICATION = () -> get("claim_enter_notification");

    private static Map<String, Class<? extends Setting<?>>> settings = new ConcurrentHashMap<>();

    public static void register(Class<? extends Setting<?>> setting, String id) {
        if (settings == null) {
            settings = new ConcurrentHashMap<>();
        }
        settings.put(id, setting);
    }

    public static <T> Class<Setting<T>> get(String name) {
        if (settings == null) {
            settings = new ConcurrentHashMap<>();
        }
        return (Class<Setting<T>>) settings.get(name.toLowerCase());
    }

    public static Collection<Class<? extends Setting<?>>> getAll() {
        return settings.values();
    }
}
