package info.preva1l.fadlc.menus;

import info.preva1l.fadlc.config.menus.ProfilesConfig;
import info.preva1l.fadlc.config.particles.Particles;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.menus.lib.PaginatedFastInv;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.claim.settings.ProfileFlag;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.utils.FadlcExecutors;
import info.preva1l.fadlc.utils.TaskManager;
import info.preva1l.fadlc.utils.Text;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProfilesMenu extends PaginatedFastInv<ProfilesConfig> {
    private final OnlineUser user;

    public ProfilesMenu(Player player) {
        super(ProfilesConfig.i());
        this.user = UserManager.getInstance().getUser(player.getUniqueId()).orElseThrow();

        scheme.bindPagination('X');
        CompletableFuture.runAsync(this::buttons, FadlcExecutors.VIRTUAL_THREAD_PER_TASK)
                .thenRun(() -> TaskManager.runSync(player, () -> this.open(player)));
    }

    private void buttons() {
        fillPaginationItems();
        placeNavigationItems();
    }

    private void placeNavigationItems() {
        scheme.bindItem('B', config.getLang().getBack().itemStack(),
                e -> {
                    config.getLang().getBack().getSound().play((Player) e.getWhoClicked());
                    new ClaimMenu((Player) e.getWhoClicked());
                });
        scheme.bindItem('P', config.getLang().getPrevious().itemStack(),
                e -> {
                    config.getLang().getPrevious().getSound().play((Player) e.getWhoClicked());
                    openPrevious();
                });
        scheme.bindItem('N', config.getLang().getNext().itemStack(),
                e -> {
                    config.getLang().getNext().getSound().play((Player) e.getWhoClicked());
                    openNext();
                });
    }

    @Override
    public void fillPaginationItems() {
        clearContent();
        for (IClaimProfile profile : user.getClaim().getProfiles().values()) {
            List<String> lore = new ArrayList<>();

            int i = 0;
            for (String line : config.getLang().getProfile().lore()) {
                if (line.contains("%flags%")) {
                    for (ProfileFlag flag : profile.getFlags().keySet()) {
                        boolean status = profile.getFlags().get(flag);
                        ProfilesConfig.Lang.Flag flagConf = config.getLang().getProfile().flag();
                        String str = flagConf.format()
                                .replace("%flag%", flag.getName())
                                .replace("%status%", status ? flagConf.enabled() : flagConf.disabled());
                        lore.add(i, str);
                        i++;
                    }
                    continue;
                }
                line = line.replace("%members%", profile.getMembers().size() + "");
                line = line.replace("%border%", Particles.getParticle(profile.getBorder()).display());
                line = line.replace("%chunks%", profile.getClaimedChunks().size() + "");
                lore.add(i, line);
                i++;
            }

            ItemBuilder itemStack = new ItemBuilder(profile.getIcon())
                    .name(Text.modernMessage(
                            config.getLang().getProfile().name().replace("%profile%", profile.getName())))
                    .lore(Text.modernList(lore));

            addContent(itemStack.build(), (e) -> {
                config.getLang().getProfile().getSound().play((Player) e.getWhoClicked());
            });
        }
    }
}
