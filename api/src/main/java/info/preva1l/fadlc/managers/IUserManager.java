package info.preva1l.fadlc.managers;

import info.preva1l.fadlc.models.user.OnlineUser;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApiStatus.NonExtendable
public interface IUserManager {
    List<OnlineUser> getAllUsers();

    Optional<OnlineUser> getUser(String name);

    Optional<OnlineUser> getUser(UUID uniqueId);

    Optional<OnlineUser> getUser(Player player);
}
