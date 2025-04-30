package info.preva1l.fadlc.menus.profile;

import info.preva1l.fadlc.claim.IClaimProfile;
import info.preva1l.fadlc.config.menus.profile.ManageProfileConfig;
import info.preva1l.fadlc.menus.lib.FastInv;
import org.bukkit.entity.Player;

public class ManageProfileMenu extends FastInv<ManageProfileConfig> {
    private final IClaimProfile profile;

    public ManageProfileMenu(Player player, IClaimProfile profile) {
        super(player, ManageProfileConfig.i());
        this.profile = profile;
    }

    @Override
    protected void placeNavigationItems() {
        scheme.bindItem('B', config.getLang().getBack().itemStack(),
                e -> {
                    config.getLang().getBack().getSound().play(user);
                    new ProfilesMenu(user.asPlayer());
                });

        for (int i = 1; i <= 5; i++) {
            scheme.bindItem(Character.forDigit(i, 10),
                    config.getLang().getGroup().itemStack(config.getLang().getGroupIcons().get(i)),
                    e -> {
                        config.getLang().getGroup().getSound().play(user);
                        // todo
                    });
        }
    }
}
