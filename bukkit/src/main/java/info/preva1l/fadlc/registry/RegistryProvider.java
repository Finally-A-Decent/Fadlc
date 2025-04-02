package info.preva1l.fadlc.registry;

import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.models.claim.settings.GroupSetting;
import info.preva1l.fadlc.models.claim.settings.ProfileFlag;
import info.preva1l.fadlc.models.user.settings.impl.ClaimLeaveEnterNotificationSetting;
import info.preva1l.fadlc.models.user.settings.impl.MessageLocationSetting;
import info.preva1l.fadlc.models.user.settings.impl.ViewBordersSetting;

import java.util.stream.Stream;

public interface RegistryProvider {
    default void loadRegistries() {
        loadProfileFlagsRegistry();
        loadGroupSettingsRegistry();
        loadUserSettingsRegistry();
    }

    private void loadUserSettingsRegistry() {
        UserSettingsRegistry.register(ViewBordersSetting.class, "view_borders");
        UserSettingsRegistry.register(MessageLocationSetting.class, "message_location");
        UserSettingsRegistry.register(ClaimLeaveEnterNotificationSetting.class, "claim_leave_enter_notification");
    }

    private void loadProfileFlagsRegistry() {
        Lang.ProfileFlags conf = Lang.i().getProfileFlags();
        Stream.of(
                new ProfileFlag("explosion_damage",
                        conf.getExplosionDamage().getIcon(),
                        conf.getExplosionDamage().getName(),
                        conf.getExplosionDamage().getDescription(),
                        conf.getExplosionDamage().isEnabledByDefault()
                ),
                new ProfileFlag("entity_griefing",
                        conf.getEntityGriefing().getIcon(),
                        conf.getEntityGriefing().getName(),
                        conf.getEntityGriefing().getDescription(),
                        conf.getEntityGriefing().isEnabledByDefault()
                ),
                new ProfileFlag("pvp",
                        conf.getPvp().getIcon(),
                        conf.getPvp().getName(),
                        conf.getPvp().getDescription(),
                        conf.getPvp().isEnabledByDefault()
                ),
                new ProfileFlag("passive_mob_spawn",
                        conf.getPassiveMobSpawn().getIcon(),
                        conf.getPassiveMobSpawn().getName(),
                        conf.getPassiveMobSpawn().getDescription(),
                        conf.getPassiveMobSpawn().isEnabledByDefault()
                ),
                new ProfileFlag("hostile_mob_spawn",
                        conf.getHostileMobSpawn().getIcon(),
                        conf.getHostileMobSpawn().getName(),
                        conf.getHostileMobSpawn().getDescription(),
                        conf.getHostileMobSpawn().isEnabledByDefault()
                )
        ).forEach(ProfileFlagsRegistry::register);
    }

    private void loadGroupSettingsRegistry() {
        Lang.GroupSettings conf = Lang.i().getGroupSettings();
        Stream.of(
                new GroupSetting(
                        "break_blocks",
                        conf.getBreakBlocks().getName(),
                        conf.getBreakBlocks().getDescription()
                ),
                new GroupSetting(
                        "place_blocks",
                        conf.getPlaceBlocks().getName(),
                        conf.getPlaceBlocks().getDescription()
                ),
                new GroupSetting(
                        "use_doors",
                        conf.getUseDoors().getName(),
                        conf.getUseDoors().getDescription()
                ),
                new GroupSetting(
                        "use_buttons",
                        conf.getUseButtons().getName(),
                        conf.getUseButtons().getDescription()
                ),
                new GroupSetting(
                        "enter",
                        conf.getPlaceBlocks().getName(),
                        conf.getPlaceBlocks().getDescription()
                )
        ).forEach(GroupSettingsRegistry::register);
    }
}
