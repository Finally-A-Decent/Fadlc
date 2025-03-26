package info.preva1l.fadlc.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class for interfacing with the <a href="https://mclo.gs/">mclo.gs</a> website API.
 *
 * @author Preva1l
 */
@UtilityClass
public class McLogsAPI {
    private final ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
    private final Gson gson = new Gson();

    public CompletableFuture<Response> upload(String string) {
        return CompletableFuture.supplyAsync(() -> {
            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpRequest.Builder request = HttpRequest.newBuilder();
                request.method("POST", HttpRequest.BodyPublishers.ofString(string));
                request.header("Content-Type", "application/x-www-form-urlencoded");
                request.POST(HttpRequest.BodyPublishers.ofString("{\"content\":\"%s\"}".formatted(string)));
                HttpResponse<String> response = client.send(request.build(), HttpResponse.BodyHandlers.ofString());
                return new Response(gson.fromJson(response.body(), JsonObject.class));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, service);
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        public final JsonObject rawResponse;

        public boolean success() {
            return rawResponse.get("success").getAsBoolean();
        }

        public @Nullable String id() {
            if (!success()) {
                return null;
            }
            return rawResponse.get("id").getAsString();
        }

        public @Nullable String url() {
            if (!success()) {
                return null;
            }
            return rawResponse.get("url").getAsString();
        }

        public @Nullable String raw() {
            if (!success()) {
                return null;
            }
            return rawResponse.get("raw").getAsString();
        }

        public @Nullable String error() {
            if (!success()) {
                return rawResponse.get("error").getAsString();
            }
            return null;
        }
    }
}
