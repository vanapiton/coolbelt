package fi._1up.coolbelt.config;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

@SuppressWarnings("CanBeFinal")
public class CoolbeltConfigHud {
    @ConfigEntry(
            nameKey = "config.coolbelt.hud.show.name",
            name = "§lShow durability HUD§r",
            descriptionKey = "config.coolbelt.hud.show.desc",
            description = "Sets if durability indicators are shown next to the hotbar."
    )
    public Boolean showDurabilityHUD = true;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.always_show_tools.name",
            name = "§lAlways show tool durabilities§r",
            descriptionKey = "config.coolbelt.hud.always_show_tools.desc",
            description = "Sets if the durability indicator always shows all tools."
    )
    public Boolean alwaysShowTools = false;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.tools_offset_x.name",
            name = "Tools position offset X in slots",
            minValue = Integer.MIN_VALUE
    )
    public Integer toolsOffsetX = 6;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.tools_offset_y.name",
            name = "Tools position offset Y in slots",
            minValue = Integer.MIN_VALUE
    )
    public Integer toolsOffsetY = 0;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.tools_step_x.name",
            name = "Tools per-tool step X in slots",
            minValue = Integer.MIN_VALUE
    )
    public Integer toolStepX = 1;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.tools_step_y.name",
            name = "Tools per-tool step Y in slots",
            minValue = Integer.MIN_VALUE
    )
    public Integer toolStepY = 0;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.always_show_armor.name",
            name = "§lAlways show armor durabilities§r",
            descriptionKey = "config.coolbelt.hud.always_show_armor.desc",
            description = "Sets if the durability indicator always shows armor. §8§oScope creep? Surely not!§r"
    )
    public Boolean alwaysShowArmors = false;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.armors_offset_x.name",
            name = "Armors position offset X in slots",
            minValue = Integer.MIN_VALUE
    )
    public Integer armorsOffsetX = -6;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.armors_offset_y.name",
            name = "Armors position offset Y in slots",
            minValue = Integer.MIN_VALUE
    )
    public Integer armorsOffsetY = 0;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.armors_step_x.name",
            name = "Armors per-piece step X in slots",
            minValue = Integer.MIN_VALUE
    )
    public Integer armorStepX = -1;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.armors_step_y.name",
            name = "Armors per-piece step Y in slots",
            minValue = Integer.MIN_VALUE
    )
    public Integer armorStepY = 0;
}
