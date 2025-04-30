package info.preva1l.fadlc.utils;

import info.preva1l.fadlc.hooks.impl.PapiHook;
import info.preva1l.fadlc.models.Replacer;
import info.preva1l.hooker.Hooker;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.internal.parser.Token;
import net.kyori.adventure.text.minimessage.internal.parser.TokenParser;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class Text {
    private final MiniMessage miniMessage = MiniMessage.builder().build();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();
    private final Pattern REMOVE_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-fA-F])|&[0-9a-fA-Fk-orK-OR]");

    public Component text(@NotNull String message, Replacer... args) {
        return text(null, message, args);
    }

    public Component text(@NotNull List<String> message, Replacer... args) {
        return text(String.join("\n", message), args);
    }

    public Component text(@Nullable Player player, @NotNull String message, Replacer... args) {
        Optional<PapiHook> hook = Hooker.getHook(PapiHook.class);
        if (hook.isPresent()) message = hook.get().format(player, message);
        return miniMessage.deserialize(unescape(miniMessage.serialize(
                legacySerializer.deserialize("<!italic>" + replace(message, args)))));
    }

    public List<Component> list(@NotNull List<String> list, Replacer... args) {
        return list(null, list, args);
    }

    public List<Component> list(@Nullable Player player, List<String> list, Replacer... args) {
        return list.stream().map(string -> Text.text(player, string, args)).collect(Collectors.toList());
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
    public String replace(String message, Replacer... args) {
        for (Replacer replacement : args) {
            if (!message.contains(replacement.first())) continue;

            if (replacement.second() instanceof Component comp) {
                message = message.replace(replacement.first(), legacySerializer.serialize(comp));
                continue;
            }

            message = message.replace(replacement.first(), String.valueOf(replacement.second()));
        }
        return message;
    }

    /**
     * Capitalizes the first letter in a string.
     *
     * @param str String
     * @return Capitalized String
     */
    public String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
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

    public double getAmountFromString(String priceString) throws NumberFormatException {
        if (priceString.toLowerCase().contains("nan") || priceString.toLowerCase().contains("infinity")) {
            throw new NumberFormatException();
        }

        double multi = 1;

        if (priceString.toLowerCase().endsWith("k")) {
            multi = 1000;
            priceString = priceString.replace("k", "");
            priceString = priceString.replace("K", "");
        } else if (priceString.toLowerCase().endsWith("m")) {
            multi = 1_000_000;
            priceString = priceString.replace("m", "");
            priceString = priceString.replace("M", "");
        } else if (priceString.toLowerCase().endsWith("b")) {
            multi = 1_000_000_000;
            priceString = priceString.replace("b", "");
            priceString = priceString.replace("B", "");
        } else if (priceString.toLowerCase().endsWith("t")) {
            multi = 1_000_000_000_000L;
            priceString = priceString.replace("t", "");
            priceString = priceString.replace("T", "");
        } else if (priceString.toLowerCase().endsWith("q")) {
            multi = 1_000_000_000_000_000L;
            priceString = priceString.replace("q", "");
            priceString = priceString.replace("Q", "");
        }

        return Double.parseDouble(priceString) * multi;
    }

    /**
     * Gets an item name from an item stack
     *
     * @param item item stack
     * @return formatted item name
     */
    public String extractItemName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Component display = meta.displayName();
            if (display != null) {
                return ((TextComponent) display).content();
            }

            if (meta.hasLocalizedName()) {
                return meta.getLocalizedName();
            }
        }
        String[] split = item.getType().name().toLowerCase().split("_");
        StringBuilder builder = new StringBuilder();
        for (String s : split) {
            builder.append(capitalizeFirst(s)).append(" ");
        }
        return builder.toString().trim();
    }
}