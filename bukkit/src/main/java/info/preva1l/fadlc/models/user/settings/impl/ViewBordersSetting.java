package info.preva1l.fadlc.models.user.settings.impl;

import info.preva1l.fadlc.models.user.settings.Setting;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ViewBordersSetting implements Setting<Boolean> {
    private Boolean state;

    @Override
    public String getName() {
        return "";
    }

    @Override
    public List<String> getDescription() {
        return List.of();
    }
}
