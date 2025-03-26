package info.preva1l.fadlc.hooks;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.hooker.Hooker;

public interface HookProvider {
    default void loadHooks() {
        Hooker.register(
                getPlugin(),
                "info.preva1l.fadlc.hooks"
        );

        Hooker.requirement("config", value -> switch (value) {
                    case "influxdb" -> true/*Config.i().getHooks().getInfluxdb().isEnabled()*/;
                    default -> true;
                }
        );

        Hooker.load();
    }

    Fadlc getPlugin();
}
