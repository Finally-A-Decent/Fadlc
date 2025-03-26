package info.preva1l.fadlc.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public final class Text {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();

    /**
     * Takes a string formatted in minimessage OR legacy and turns it into an Adventure Component.
     *
     * @param message the modernMessage
     * @return colorized component
     */
    public Component modernMessage(@NotNull String message) {
        return miniMessage.deserialize(miniMessage.serialize(legacySerializer.deserialize("<!i>" + message)));
    }

    public List<Component> modernList(@NotNull List<String> list) {
        return list.stream().map(Text::modernMessage).collect(Collectors.toList());
    }
}