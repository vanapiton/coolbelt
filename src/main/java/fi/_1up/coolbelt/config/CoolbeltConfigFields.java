package fi._1up.coolbelt.config;

import net.glasslauncher.mods.gcapi3.api.ConfigCategory;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

@SuppressWarnings("CanBeFinal")
public class CoolbeltConfigFields {
    @ConfigEntry(
        nameKey = "config.coolbelt.use_tool_for_zero_hardness.name",
        name = "Use toolbelt for zero-hardness blocks",
        descriptionKey = "config.coolbelt.use_tool_for_zero_hardness.desc",
        description = "Looks cool when breaking grass with a sword, but wears it out..."
    )
    public Boolean useToolForZeroHardness = false;

    @ConfigEntry(
        nameKey = "config.coolbelt.use_sword_for_mining.name",
        name = "Use sword for mining",
        descriptionKey = "config.coolbelt.use_sword_for_mining.desc",
        description = "Uses the sword slot if it's the fastest, since in beta swords mine all blocks slightly faster."
    )
    public Boolean useSwordForMining = true;

    @ConfigEntry(
        nameKey = "config.coolbelt.search_whole_hotbar.name",
        name = "Search whole hotbar",
        descriptionKey = "config.coolbelt.search_whole_hotbar.desc",
        description = "Searches for tools in the hotbar as opposed to just the held item."
    )
    public Boolean searchWholeHotbar = true;

    @ConfigEntry(
        nameKey = "config.coolbelt.hotbar_algorithm.name",
        name = "Hotbar algorithm",
        descriptionKey = "config.coolbelt.hotbar_algorithm.desc",
        description = "Sets which slot is preferred when both the hotbar and toolbelt have an appropriate tool."
    )
    public HotbarAlgorithm hotbarAlgorithm = HotbarAlgorithm.ALWAYS_PREFER_HAND_TOOL;

    @ConfigCategory(
        nameKey = "config.coolbelt.is_slot_enabled.name",
        name = "Toggle toolbelt slots §8(Requires restart)§r",
        descriptionKey = "config.coolbelt.is_slot_enabled.desc",
        description = "Determines which toolbelt slots are added; If none, §owhy do you have this mod?§r",
        multiplayerSynced = true
    )
    public CoolbeltConfigSlotToggles isSlotEnabled = new CoolbeltConfigSlotToggles();
}
