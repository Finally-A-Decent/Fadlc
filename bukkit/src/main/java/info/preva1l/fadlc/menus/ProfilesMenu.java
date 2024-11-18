package info.preva1l.fadlc.menus;

import info.preva1l.fadlc.config.particles.Particles;
import info.preva1l.fadlc.config.sounds.Sounds;
import info.preva1l.fadlc.managers.LayoutManager;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.menus.lib.pagination.PaginatedFastInv;
import info.preva1l.fadlc.menus.lib.pagination.PaginatedItem;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.claim.settings.ProfileFlag;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.utils.Text;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ProfilesMenu extends PaginatedFastInv {
    private final OnlineUser user;

    public ProfilesMenu(Player player) {
        super(player, LayoutManager.MenuType.PROFILES, List.of(11, 12, 13, 14, 15, 20, 21, 22, 23, 24));
        this.user = UserManager.getInstance().getUser(player.getUniqueId()).orElseThrow();

        setPaginationMappings(getLayout().paginationSlots());

        placeFillerItems();
        placeNavigationItems();
        updatePagination();
    }

    private void placeNavigationItems() {
        setItem(getLayout().buttonSlots().getOrDefault(LayoutManager.ButtonType.BACK, -1),
                getLang().getItemStack("back").getBase(),
                e -> Sounds.playSound(e.getPlayer(), getLang().getSound("back.sound")));
    }

    @Override
    protected void fillPaginationItems() {
        for (IClaimProfile profile : user.getClaim().getProfiles().values()) {
            List<String> lore = new ArrayList<>();

            int i = 0;
            for (String line : getLang().getLore("profile.lore")) {
                if (line.contains("%flags%")) {
                    for (ProfileFlag flag : profile.getFlags().keySet()) {
                        boolean status = profile.getFlags().get(flag);
                        String str = getLang().getString("profile.flag.format")
                                .replace("%flag%", flag.getName())
                                .replace("%status%", getLang().getString(status ? "profile.flag.enabled" : "profile.flag.disabled"));
                        lore.add(i, Text.legacyMessage(str));
                        i++;
                    }
                    continue;
                }
                line = line.replace("%members%", profile.getMembers().size() + "");
                line = line.replace("%border%", Text.legacyMessage(Particles.getParticle(profile.getBorder()).getDisplayName()));
                line = line.replace("%chunks%", profile.getClaimedChunks().size() + "");
                lore.add(i, line);
                i++;
            }

            ItemBuilder itemStack = new ItemBuilder(profile.getIcon())
                    .name(Text.legacyMessage(getLang().getStringFormatted("profile.name").replace("%profile%", profile.getName())))
                    .lore(lore);

            addPaginationItem(new PaginatedItem(itemStack.build(), (e) -> {
                Sounds.playSound(e.getPlayer(), getLang().getSound("profile.sound"));
            }));
        }
    }

    @Override
    protected void addPaginationControls() {

    }
}
