package info.preva1l.fadlc.models.user.settings.impl;

import info.preva1l.fadlc.models.MessageLocation;
import info.preva1l.fadlc.models.user.settings.Setting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MessageLocationSetting implements Setting<MessageLocation> {
    private MessageLocation state;

    @Override
    public String getName() {
        return "";
    }

    @Override
    public List<String> getDescription() {
        return List.of();
    }
}
