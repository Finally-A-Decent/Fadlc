package info.preva1l.fadlc.models.user;

import com.google.gson.annotations.Expose;
import info.preva1l.fadlc.managers.UserManager;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class OfflineUser implements User {
    @Expose private final UUID uniqueId;
    @Expose private final String name;

    @Override
    public int hashCode() {
        return uniqueId.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OfflineUser || o instanceof BukkitUser)) return false;
        User other = (User) o;
        return uniqueId.equals(other.getUniqueId());
    }

    @Override
    public OnlineUser getOnlineUser() {
        return UserManager.getInstance().getUser(uniqueId).orElse(null);
    }
}
