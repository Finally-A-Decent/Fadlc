package info.preva1l.fadlc.claim;

import info.preva1l.fadlc.claim.settings.GroupSetting;
import info.preva1l.fadlc.persistence.DatabaseObject;
import info.preva1l.fadlc.user.User;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Map;

@ApiStatus.NonExtendable
public interface IProfileGroup extends DatabaseObject {
    int getId();

    String getName();

    List<User> getUsers();

    Map<GroupSetting, Boolean> getSettings();
}
