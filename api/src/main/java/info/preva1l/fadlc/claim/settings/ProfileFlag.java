package info.preva1l.fadlc.claim.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProfileFlag {
    private final String id;
    private final Material icon;
    private final int modelData;
    private final String name;
    private final List<String> description;
    private final boolean enabledByDefault;

    @Override
    public String toString() {
        return id;
    }
}
