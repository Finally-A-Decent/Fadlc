package info.preva1l.fadlc.utils;

import info.preva1l.fadlc.hooks.impl.PapiHook;
import info.preva1l.fadlc.models.Tuple;
import info.preva1l.hooker.Hooker;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.internal.parser.Token;
import net.kyori.adventure.text.minimessage.internal.parser.TokenParser;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@UtilityClass
public final class Text {
    private final MiniMessage miniMessage = MiniMessage.builder().build();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();
    private final Pattern REMOVE_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-fA-F])|&[0-9a-fA-Fk-orK-OR]");

    @SafeVarargs
    public Component text(@NotNull String message, Tuple<String, Object>... args) {
        return text(null, message, args);
    }

    @SafeVarargs
    public Component text(@NotNull List<String> message, Tuple<String, Object>... args) {
        return text(String.join("\n", message), args);
    }

    @SafeVarargs
    public Component text(@Nullable Player player, @NotNull String message, Tuple<String, Object>... args) {
        Optional<PapiHook> hook = Hooker.getHook(PapiHook.class);
        if (hook.isPresent()) message = hook.get().format(player, message);
        return replace(miniMessage.deserialize(unescape(miniMessage.serialize(
                legacySerializer.deserialize("<!italic>" + message)))), args);
    }

    @SafeVarargs
    public List<Component> list(@NotNull List<String> list, Tuple<String, Object>... args) {
        return list(null, list, args);
    }

    @SafeVarargs
    public List<Component> list(@Nullable Player player, List<String> list, Tuple<String, Object>... args) {
        return replace(list.stream().map(string -> Text.text(player, string)).toList(), args);
    }

    /**
     * Unescapes minimessage tags.
     * <p>
     *     This will be removed once minimessage adds the option to prevent the serializer from escaping them in the first place.
     * </p>
     *
     * @param input the minimessage formatted string with escaped tags
     * @return the minimessage formatted string without escaped tags
     */
    @SuppressWarnings("UnstableApiUsage")
    private String unescape(@NotNull String input) {
        List<Token> tokens = TokenParser.tokenize(input, false);
        tokens.sort(Comparator.comparingInt(Token::startIndex));
        StringBuilder output = new StringBuilder();
        int lastIndex = 0;
        for (Token token : tokens) {
            int start = token.startIndex();
            int end = token.endIndex();
            if (lastIndex < start) output.append(input, lastIndex, start);
            output.append(
                    TokenParser.unescape(input.substring(start, end),
                            0, end - start,
                            escape -> escape == TokenParser.TAG_START || escape == TokenParser.ESCAPE)
            );
            lastIndex = end;
        }

        if (lastIndex < input.length()) output.append(input.substring(lastIndex));
        return output.toString();
    }

    /**
     * Formats a message with placeholders.
     *
     * @param message message with placeholders
     * @param args    placeholders to replace
     * @return formatted string
     */
    @SafeVarargs
    public String replace(String message, Tuple<String, Object>... args) {
        for (Tuple<String, Object> replacement : args) {
            if (!message.contains(replacement.getFirst())) continue;

            if (replacement.getSecond() instanceof Component comp) {
                message = message.replace(replacement.getFirst(), legacySerializer.serialize(comp));
                continue;
            }

            message = message.replace(replacement.getFirst(), String.valueOf(replacement.getSecond()));
        }
        return message;
    }

    /**
     * Formats a message with placeholders.
     *
     * @param message message with placeholders
     * @param args    placeholders to replace
     * @return formatted string
     */
    @SafeVarargs
    public Component replace(Component message, Tuple<String, Object>... args) {
        if (args == null) return message;
        for (Tuple<String, Object> replacement : args) {
            message = finishReplacement(message, replacement);
        }
        return message;
    }

    /**
     * Formats a list with placeholders.
     *
     * @param list list with placeholders
     * @param args    placeholders to replace
     * @return formatted list
     */
    @SafeVarargs
    public List<Component> replace(List<Component> list, Tuple<String, Object>... args) {
        if (args == null) return list;
        List<Component> result = new ArrayList<>();

        for (Component line : list) {
            for (Tuple<String, Object> replacement : args) {
                if (!((TextComponent) line).content().contains(replacement.getFirst())) continue;
                if (replacement.getSecond() instanceof List<?> l) {
                    for (Object additionalLine : l) {
                        if (additionalLine instanceof Component comp) {
                            result.add(comp);
                        }
                        result.add(text(String.valueOf(additionalLine)));
                    }
                    continue;
                }
                result.add(finishReplacement(line, replacement));
            }
        }
        return result;
    }

    private Component finishReplacement(Component message, Tuple<String, Object> replacement) {
        if (!((TextComponent) message).content().contains(replacement.getFirst())) return message;

        if (replacement.getSecond() instanceof Component comp) {
            message = message.replaceText(conf -> conf.match(replacement.getFirst()).replacement(comp));
        }

        message = message.replaceText(conf -> conf.match(replacement.getFirst()).replacement(String.valueOf(replacement.getSecond())));
        return message;
    }

    /**
     * Strip color codes from a string, including hex codes, codes starting with the section symbol (§),
     * codes starting with an ampersand and minimessage codes.
     *
     * @param str String with color codes.
     * @return String without color codes.
     */
    public String removeColorCodes(String str) {
        str = legacySerializer.serialize(miniMessage.deserialize(str));
        str = REMOVE_PATTERN.matcher(str).replaceAll("");
        return str;
    }
}