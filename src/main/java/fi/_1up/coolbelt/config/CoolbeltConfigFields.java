package fi._1up.coolbelt.config;

import net.glasslauncher.mods.gcapi3.api.ConfigCategory;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

@SuppressWarnings("CanBeFinal")
public class CoolbeltConfigFields {
    @ConfigCategory(
            nameKey = "config.coolbelt.is_slot_enabled.name",
            name = "Toggle toolbelt slots §8(Requires restart)§r",
            descriptionKey = "config.coolbelt.is_slot_enabled.desc",
            description = "Determines which toolbelt slots are added; §8If none, §owhy do you have this mod?§r",
            multiplayerSynced = true
    )
    public CoolbeltConfigSlotToggles isSlotEnabled = new CoolbeltConfigSlotToggles();

    @ConfigCategory(
            nameKey = "config.coolbelt.durability_hud.name",
            name = "HUD options",
            descriptionKey = "config.coolbelt.durability_hud.desc",
            description = "Origin is on the middle hotbar slot."
    )
    public CoolbeltConfigHud hud = new CoolbeltConfigHud();

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
            nameKey = "config.coolbelt.search_hotbar.name",
            name = "Search hotbar",
            descriptionKey = "config.coolbelt.search_hotbar.desc",
            description = "Searches for tools in the hotbar as opposed to just the held item."
    )
    public Boolean searchHotbar = true;

    @ConfigEntry(
            nameKey = "config.coolbelt.slot_algorithm.name",
            name = "Slot selection algorithm",
            descriptionKey = "config.coolbelt.slot_algorithm.desc",
            description = "Sets which slot is preferred when multiple slots have an appropriate tool."
    )
    public SlotAlgorithm slotAlgorithm = SlotAlgorithm.ALWAYS_PREFER_HAND_TOOL;
}
