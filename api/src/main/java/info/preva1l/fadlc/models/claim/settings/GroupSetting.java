package info.preva1l.fadlc.models.claim.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GroupSetting {
    private final String id;
    private final String name;
    private final List<String> description;

    @Override
    public String toString() {
        return id;
    }
}