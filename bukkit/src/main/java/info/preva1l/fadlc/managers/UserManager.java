package info.preva1l.fadlc.managers;

import info.preva1l.fadlc.models.user.OnlineUser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserManager implements IUserManager {
    @Getter private static final UserManager instance = new UserManager();

    private final Map<String, OnlineUser> usersCacheName = new ConcurrentHashMap<>();
    private final Map<UUID, OnlineUser> usersCacheUuid = new ConcurrentHashMap<>();

    public void cacheUser(OnlineUser user) {
        usersCacheUuid.put(user.getUniqueId(), user);
        usersCacheName.put(user.getName(), user);
    }

    public void invalidate(UUID uuid) {
        usersCacheUuid.remove(uuid);
    }

    public void invalidate(String username) {
        usersCacheName.remove(username);
    }

    @Override
    public List<OnlineUser> getAllUsers() {
        return new ArrayList<>(usersCacheUuid.values());
    }

    @Override
    public Optional<OnlineUser> getUser(String name) {
        return Optional.ofNullable(usersCacheName.get(name));
    }

    @Override
    public Optional<OnlineUser> getUser(UUID uniqueId) {
        return Optional.ofNullable(usersCacheUuid.get(uniqueId));
    }

    @Override
    public Optional<OnlineUser> getUser(Player player) {
        return Optional.ofNullable(usersCacheUuid.get(player.getUniqueId()));
    }
}
