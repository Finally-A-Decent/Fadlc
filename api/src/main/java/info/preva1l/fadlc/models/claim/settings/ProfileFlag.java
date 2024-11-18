package info.preva1l.fadlc.models.claim.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProfileFlag {
    private String id;
    private final String name;
    private final List<String> description;
    private final boolean enabledByDefault;

    @Override
    public String toString() {
        return id;
    }
}
