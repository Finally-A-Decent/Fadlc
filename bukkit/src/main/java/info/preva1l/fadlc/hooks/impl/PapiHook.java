package info.preva1l.fadlc.hooks.impl;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.claim.IClaimProfile;
import info.preva1l.fadlc.claim.IProfileGroup;
import info.preva1l.fadlc.claim.services.ClaimService;
import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.models.ChunkStatus;
import info.preva1l.fadlc.user.OnlineUser;
import info.preva1l.fadlc.user.UserService;
import info.preva1l.hooker.annotation.Hook;
import info.preva1l.hooker.annotation.OnStart;
import info.preva1l.hooker.annotation.Require;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created on 1/04/2025
 *
 * @author Preva1l
 */
@Hook(id = "placeholders")
@Require("PlaceholderAPI")
public class PapiHook {
    @OnStart
    public boolean onEnable() {
        return new Expansion().register();
    }

    public String format(@Nullable final Player player, final String string) {
        return PlaceholderAPI.setPlaceholders(player, string);
    }

    @Getter
    private static class Expansion extends PlaceholderExpansion {
        public final String identifier = "fadlc";
        public final String author = "Preva1l";
        public final String version = Fadlc.i().getCurrentVersion().toString();
        @Override public boolean persist() { return true; }

        @Override
        public String onRequest(OfflinePlayer p, @NotNull String params) {
            return Placeholder.parse(p, params.toLowerCase());
        }
    }

    static class Placeholder<T> {
        private static final Map<String, Placeholder<?>> placeholders = new HashMap<>();

        static {
            online("chunks_claimed", user -> user.getClaim().getClaimedChunks().size());
            online("chunks_available", OnlineUser::getAvailableChunks);

            online("current_is_claimed", user -> user.getPosition().getChunk().getStatus() == ChunkStatus.CLAIMED);
            online("current_chunk_status", user -> user.getPosition().getChunk().getStatus().name());

            online("current_claim", user ->
                    ClaimService.getInstance().getClaimAt(user.getPosition())
                            .flatMap(claim -> claim.getProfile(user.getPosition().getChunk()))
                            .map(IClaimProfile::getName)
                            .orElse(Lang.i().getWords().getNone())
            );
            online("current_claim_owner", user ->
                    ClaimService.getInstance().getClaimAt(user.getPosition())
                            .map(claim -> claim.getOwner().getName())
                            .orElse(Lang.i().getWords().getNone())
            );
            online("current_claim_group", user ->
                    ClaimService.getInstance().getClaimAt(user.getPosition())
                            .flatMap(claim -> claim.getProfile(user.getPosition().getChunk()))
                            .map(profile -> profile.getPlayerGroup(user))
                            .map(IProfileGroup::getName)
                            .orElse(Lang.i().getWords().getNone())
            );

            any("enabled", () -> Config.i().isEnabled());
        }

        private final Class<T> type;
        private final Function<T, Object> parser;

        private Placeholder(Class<T> type, Function<T, Object> parser) {
            this.type = type;
            this.parser = parser;
        }

        public static String parse(@Nullable OfflinePlayer player, @NotNull String params) {
            if (player instanceof Player online) {
                var user = UserService.getInstance().getUser(online).orElseThrow();
                if (get(params).type.isAssignableFrom(OnlineUser.class)) return String.valueOf(get(params).parse(user));
            }
            return String.valueOf(get(params).parse(player));
        }

        private static void online(String match, Function<OnlineUser, Object> parser) {
            placeholders.put(match, new Placeholder<>(OnlineUser.class, parser));
        }

        private static void onlineBukkit(String match, Function<Player, Object> parser) {
            placeholders.put(match, new Placeholder<>(Player.class, parser));
        }

        private static void offline(String match, Function<OfflinePlayer, Object> parser) {
            placeholders.put(match, new Placeholder<>(OfflinePlayer.class, parser));
        }

        private static void any(String match, Supplier<Object> parser) {
            placeholders.put(match, new Placeholder<>(Void.class, v -> parser.get()));
        }

        private static <E> Placeholder<E> get(String match) {
            return (Placeholder<E>) placeholders.get(match);
        }

        private Object parse(T target) {
            if (!type.isInstance(target)) return null;
            return parser.apply(target);
        }
    }
}