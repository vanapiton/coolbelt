package fi._1up.coolbelt.config;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

@SuppressWarnings("CanBeFinal")
public class CoolbeltConfigHud {
    @ConfigEntry(
            nameKey = "config.coolbelt.hud.generate_slot_atlas.name",
            name = "§lGenerate slot texture atlas on-the-fly§r §8(Requires texture pack reload)§r",
            descriptionKey = "config.coolbelt.generate_slot_atlas.desc",
            description = "Best suited for 16x16 texture packs. If false, loads a fallback texture instead."
    )
    public Boolean generateSlotAtlas = true;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.outline_color.name",
            name = "Outline color §8(Requires texture pack reload)§r",
            descriptionKey = "config.coolbelt.outline_color.desc",
            description = "Color of the tool outlines, as a decimal integer RGB color (e.g., 0x5F5F5F becomes 6250335).",
            minValue = 0x000000,
            maxValue = 0xFFFFFF
    )
    public Integer outlineColor = 0x5F5F5F;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.sauvola_k.name",
            name = "Sauvola thresholding K value §8(Requires texture pack reload)§r",
            descriptionKey = "config.coolbelt.sauvola_k.desc",
            description = "Controls the outline generation sensitivity. Lower values are more sensitive."
    )
    public Double sauvolaK = 0.4;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.show_durabilities.name",
            name = "§lShow durability HUD§r",
            descriptionKey = "config.coolbelt.hud.show_durabilies.desc",
            description = "Sets if durability indicators are shown next to the hotbar."
    )
    public Boolean showDurabilities = true;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.always_show_tools.name",
            name = "§lAlways show tool durabilities§r",
            descriptionKey = "config.coolbelt.hud.always_show_tools.desc",
            description = "Sets if the durability indicator always shows all tools."
    )
    public Boolean alwaysShowTools = false;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.tools_offset_x.name",
            name = "Tools horizontal offset measured in slots",
            minValue = Integer.MIN_VALUE
    )
    public Integer toolsOffsetX = 6;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.tools_offset_y.name",
            name = "Tools vertical offset measured in slots",
            minValue = Integer.MIN_VALUE
    )
    public Integer toolsOffsetY = 0;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.tools_step_x.name",
            name = "Tools per-tool horizontal step measured in slots",
            minValue = Integer.MIN_VALUE
    )
    public Integer toolStepX = 1;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.tools_step_y.name",
            name = "Tools per-tool vertical step measured in slots",
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
            name = "Armors horizontal offset measured in slots",
            minValue = Integer.MIN_VALUE
    )
    public Integer armorsOffsetX = -6;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.armors_offset_y.name",
            name = "Armors vertical offset measured in slots",
            minValue = Integer.MIN_VALUE
    )
    public Integer armorsOffsetY = 0;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.armors_step_x.name",
            name = "Armors per-piece horizontal step measured in slots",
            minValue = Integer.MIN_VALUE
    )
    public Integer armorStepX = -1;

    @ConfigEntry(
            nameKey = "config.coolbelt.hud.armors_step_y.name",
            name = "Armors per-piece vertical step measured in slots",
            minValue = Integer.MIN_VALUE
    )
    public Integer armorStepY = 0;
}
