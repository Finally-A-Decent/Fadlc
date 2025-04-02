package info.preva1l.fadlc.menus;

import info.preva1l.fadlc.config.menus.ManageProfileConfig;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.menus.lib.PaginatedFastInv;
import info.preva1l.fadlc.models.Tuple;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.claim.settings.ProfileFlag;
import info.preva1l.fadlc.utils.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;


public class ManageProfileMenu extends PaginatedFastInv<ManageProfileConfig> {
    private final IClaimProfile profile;

    public ManageProfileMenu(Player player, IClaimProfile profile) {
        super(player, ManageProfileConfig.i());
        this.profile = profile;
    }

    @Override
    protected void fillPaginationItems() {
        clearContent();
        for (Map.Entry<ProfileFlag, Boolean> entry : profile.getFlags().entrySet()) {
            ProfileFlag flag = entry.getKey();
            ManageProfileConfig.Lang.Flag conf = config.getLang().getFlag();

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
