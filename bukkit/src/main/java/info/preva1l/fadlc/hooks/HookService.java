package info.preva1l.fadlc.hooks;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.hooker.Hooker;
import info.preva1l.trashcan.plugin.annotations.PluginLoad;

public final class HookService {
    public static final HookService instance = new HookService();

    @PluginLoad
    public void loadHooks() {
        Hooker.register(
                Fadlc.i(),
                "info.preva1l.fadah.hooks.impl"
        );

        Hooker.requirement("config", value -> switch (value) {
                    //case "influxdb" -> Config.i().getHooks().getInfluxdb().isEnabled();
                    default -> true;
                }
        );
    }
}