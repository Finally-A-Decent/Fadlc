package info.preva1l.fadlc.utils;

import info.preva1l.fadlc.Fadlc;
import net.william278.desertwell.util.UpdateChecker;
import net.william278.desertwell.util.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 20/03/2025
 *
 * @author Preva1l
 */
public interface UpdatesProvider {
    default Version getVersion() {
        return UpdatesHolder.self.pluginVersion;
    }

    default void checkForUpdates() {
        final UpdateChecker checker = UpdateChecker.builder()
                .currentVersion(UpdatesHolder.self.pluginVersion)
                .endpoint(UpdateChecker.Endpoint.POLYMART)
                .resource(Integer.toString(UpdatesHolder.POLYMART_ID))
                .build();
        checker.check().thenAccept(checked -> {
            UpdatesHolder.self.completed = checked;
            if (checked.isUpToDate()) return;

            notifyUpdate(Bukkit.getConsoleSender());
        });
    }

    default void notifyUpdate(@NotNull CommandSender recipient) {
        if (!recipient.hasPermission("fadlc.updates")) return;
        var checked = UpdatesHolder.self.completed;
        if (checked.isUpToDate()) return;

        recipient.sendMessage(Text.modernMessage(
                "&7[Fadlc]&f Fadlc is &#D63C3COUTDATED&f! &7Current: &#D63C3C%s &7Latest: &#18D53A%s %s"
                        .formatted(checked.getCurrentVersion(),
                                checked.getLatestVersion(),
                                isCritical(checked)
                                        ? "\n&#D63C3C&lThis update is marked as critical. Update as soon as possible."
                                        : ""
                        )
        ));
    }

    private boolean isCritical(UpdateChecker.Completed checked) {
        return !checked.isUpToDate() &&
                (checked.getLatestVersion().getMetadata().equalsIgnoreCase("hotfix") ||
                        checked.getLatestVersion().getMajor() > checked.getCurrentVersion().getMajor() ||
                        checked.getLatestVersion().getMinor() > checked.getCurrentVersion().getMinor() + 5);
    }

    class UpdatesHolder {
        private static final UpdatesHolder self = new UpdatesHolder();
        private static final int POLYMART_ID = 6616;

        private final Version pluginVersion = Version.fromString(Fadlc.i().getDescription().getVersion());
        private UpdateChecker.Completed completed;
    }
}
