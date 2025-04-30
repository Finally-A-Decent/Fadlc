package info.preva1l.fadlc.user;

import info.preva1l.fadlc.persistence.DatabaseObject;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface User extends DatabaseObject {
    String getName();

    OnlineUser getOnlineUser();
}
