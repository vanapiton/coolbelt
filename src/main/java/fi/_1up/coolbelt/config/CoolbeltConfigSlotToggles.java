package fi._1up.coolbelt.config;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class CoolbeltConfigSlotToggles {
    @ConfigEntry(
            nameKey = "config.coolbelt.is_slot_enabled.sword.name",
            name = "Enable sword slot §8(Requires restart)§r",
            multiplayerSynced = true,
            requiresRestart = true
    )
    public Boolean sword = true;

    @ConfigEntry(
            nameKey = "config.coolbelt.is_slot_enabled.pickaxe.name",
            name = "Enable pickaxe slot §8(Requires restart)§r",
            multiplayerSynced = true,
            requiresRestart = true
    )
    public Boolean pickaxe = true;

    @ConfigEntry(
            nameKey = "config.coolbelt.is_slot_enabled.axe.name",
            name = "Enable axe slot §8(Requires restart)§r",
            multiplayerSynced = true,
            requiresRestart = true
    )
    public Boolean axe = true;

    @ConfigEntry(
            nameKey = "config.coolbelt.is_slot_enabled.shovel.name",
            name = "Enable shovel slot §8(Requires restart)§r",
            multiplayerSynced = true,
            requiresRestart = true
    )
    public Boolean shovel = true;
}
