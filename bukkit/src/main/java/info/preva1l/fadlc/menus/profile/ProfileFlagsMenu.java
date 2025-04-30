package info.preva1l.fadlc.menus.profile;

import info.preva1l.fadlc.claim.IClaimProfile;
import info.preva1l.fadlc.claim.settings.ProfileFlag;
import info.preva1l.fadlc.config.menus.profile.ProfileFlagsConfig;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.menus.lib.PaginatedFastInv;
import info.preva1l.fadlc.models.Tuple;
import info.preva1l.fadlc.utils.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class ProfileFlagsMenu extends PaginatedFastInv<ProfileFlagsConfig> {
    private final IClaimProfile profile;

    public ProfileFlagsMenu(Player player, IClaimProfile profile) {
        super(player, ProfileFlagsConfig.i());
        this.profile = profile;
    }

    @Override
    protected void placeNavigationItems() {
        scheme.bindItem('B', config.getLang().getBack().itemStack(),
                e -> {
                    config.getLang().getBack().getSound().play(user);
                    new ManageProfileMenu(user.asPlayer(), profile);
                });
    }

    @Override
    protected void fillPaginationItems() {
        clearContent();
        for (Map.Entry<ProfileFlag, Boolean> entry : profile.getFlags().entrySet()) {
            ProfileFlag flag = entry.getKey();
            ProfileFlagsConfig.Lang.Flag conf = config.getLang().getFlag();

            List<Component> lore = Text.list(conf.lore(),
                    Tuple.of("%flag%", flag.getName()),
                    Tuple.of("%info%", Text.text(
                            conf.info().format(),
                            Tuple.of("%status%", entry.getValue() ? conf.info().enabled() : conf.info().disabled())
                    )),
                    Tuple.of("%description%", flag.getDescription())
            );

            ItemBuilder itemStack = new ItemBuilder(flag.getIcon())
                    .name(Text.text(conf.name(), Tuple.of("%flag%", flag.getName())))
                    .lore(lore);

            addContent(itemStack.build(), (e) -> {
                if (profile.setFlag(flag, !entry.getValue())) {
                    conf.getEnableSound().play(user);
                } else {
                    conf.getDisableSound().play(user);
                }
            });
        }
    }
}
