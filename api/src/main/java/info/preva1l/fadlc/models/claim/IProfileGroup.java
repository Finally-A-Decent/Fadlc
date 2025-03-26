package info.preva1l.fadlc.models.claim;

import info.preva1l.fadlc.models.claim.settings.GroupSetting;
import info.preva1l.fadlc.models.user.User;
import info.preva1l.fadlc.persistence.DatabaseObject;
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
