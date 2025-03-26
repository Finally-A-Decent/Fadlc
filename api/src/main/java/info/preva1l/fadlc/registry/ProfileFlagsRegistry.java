package info.preva1l.fadlc.registry;

import info.preva1l.fadlc.models.claim.settings.ProfileFlag;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class ProfileFlagsRegistry {
    public static final Supplier<ProfileFlag> EXPLOSION_DAMAGE = () -> get("explosion_damage");
    public static final Supplier<ProfileFlag> PVP = () -> get("pvp");
    public static final Supplier<ProfileFlag> ENTITY_GRIEFING = () -> get("entity_griefing");
    public static final Supplier<ProfileFlag> PASSIVE_MOB_SPAWN = () -> get("passive_mob_spawn");
    public static final Supplier<ProfileFlag> HOSTILE_MOB_SPAWN = () -> get("hostile_mob_spawn");

    private static Map<String, ProfileFlag> flags = new ConcurrentHashMap<>();

    public static void register(ProfileFlag value) {
        if (flags == null) {
            flags = new ConcurrentHashMap<>();
        }
        flags.put(value.getId().toLowerCase(), value);
    }

    public static ProfileFlag get(String id) {
        if (flags == null) {
            flags = new ConcurrentHashMap<>();
        }
        return flags.get(id.toLowerCase());
    }

    public static Collection<ProfileFlag> getAll() {
        if (flags == null) {
            flags = new ConcurrentHashMap<>();
        }
        return flags.values();
    }

    @ApiStatus.Obsolete
    public static ProfileFlag[] values() {
        return getAll().toArray(new ProfileFlag[0]);
    }
}
