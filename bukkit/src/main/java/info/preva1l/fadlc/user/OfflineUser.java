package info.preva1l.fadlc.user;

import com.google.gson.annotations.Expose;
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
        if (!(o instanceof OfflineUser other)) return false;
        return uniqueId.equals(other.getUniqueId());
    }

    @Override
    public OnlineUser getOnlineUser() {
        return UserService.getInstance().getUser(uniqueId).orElse(null);
    }
}
