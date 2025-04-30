package info.preva1l.fadlc.menus.profile;

import info.preva1l.fadlc.claim.IClaimProfile;
import info.preva1l.fadlc.config.menus.profile.ProfilesConfig;
import info.preva1l.fadlc.config.particles.Particles;
import info.preva1l.fadlc.menus.ClaimMenu;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.menus.lib.PaginatedFastInv;
import info.preva1l.fadlc.models.Tuple;
import info.preva1l.fadlc.utils.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public class ProfilesMenu extends PaginatedFastInv<ProfilesConfig> {
    public ProfilesMenu(Player player) {
        super(player, ProfilesConfig.i());
    }

    @Override
    protected void placeNavigationItems() {
        super.placeNavigationItems();

        scheme.bindItem('B', config.getLang().getBack().itemStack(),
                e -> {
                    config.getLang().getBack().getSound().play(user);
                    new ClaimMenu(user.asPlayer());
                });
    }

    @Override
    public void fillPaginationItems() {
        clearContent();
        for (IClaimProfile profile : user.getClaim().getProfiles().values()) {
            ProfilesConfig.Lang.Profile conf = config.getLang().getProfile();

            List<Component> lore = Text.list(conf.lore(),
                    Tuple.of("%members%", profile.getMembers().size()),
                    Tuple.of("%border%", Particles.getParticle(profile.getBorder()).display()),
                    Tuple.of("%chunks%", profile.getClaimedChunks().size()),
                    Tuple.of("%flags%",
                            profile.getFlags().entrySet().stream()
                                    .map(entry -> Text.text(
                                            conf.flag().format(),
                                            Tuple.of("%flag%", entry.getKey().getName()),
                                            Tuple.of("%status%", entry.getValue() ? conf.flag().enabled() : conf.flag().disabled())
                                    )).toList()
                    )
            );

            ItemBuilder itemStack = new ItemBuilder(profile.getIcon())
                    .name(Text.text(conf.name(), Tuple.of("%profile%", profile.getName())))
                    .lore(lore);

            addContent(itemStack.build(), (e) -> {
                conf.getSound().play(user);
                new ProfileFlagsMenu(user.asPlayer(), profile);
            });
        }
    }
}