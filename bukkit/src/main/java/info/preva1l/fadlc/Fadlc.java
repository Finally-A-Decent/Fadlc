package info.preva1l.fadlc;

import info.preva1l.fadlc.api.FadlcAPI;
import info.preva1l.fadlc.api.ImplFadlcAPI;
import info.preva1l.fadlc.menus.lib.FastInvManager;
import info.preva1l.fadlc.utils.Text;
import info.preva1l.trashcan.plugin.BasePlugin;
import info.preva1l.trashcan.plugin.annotations.PluginEnable;
import info.preva1l.trashcan.plugin.annotations.PluginLoad;
import org.bukkit.Bukkit;

import java.util.List;

public final class Fadlc extends BasePlugin {
    private static final String PURCHASER = "%%__USERNAME__%%";
    public static final @SuppressWarnings("ConstantValue") boolean VALID_PURCHASE = !PURCHASER.contains("__USERNAME__");

    private static Fadlc instance;

    public Fadlc() {
        instance = this;
    }

    @PluginLoad
    public void load() {
        this.flavor.inherit(new FadlcFlavorBinder());
    }

    @PluginEnable
    public void enable() {
        FastInvManager.register(this);

        FadlcAPI.setInstance(new ImplFadlcAPI(this));

        Text.list(List.of(
                "&2&l------------------------------",
                "&a Finally a Decent Land Claim",
                "&a  has successfully started!",
                "&2&l------------------------------")
        ).forEach(Bukkit.getConsoleSender()::sendMessage);
    }

    public static Fadlc i() {
        return instance;
    }
}
