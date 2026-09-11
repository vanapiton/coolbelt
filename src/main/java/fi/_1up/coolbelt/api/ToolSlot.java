package fi._1up.coolbelt.api;

import java.util.List;

import static fi._1up.coolbelt.config.CoolbeltConfig.CONFIG;

public record ToolSlot(String key, int texX, int texY, int h, int v, boolean enabled) {
    public static final List<ToolSlot> SLOTS = List.of(
            new ToolSlot("sword",    0, 0, 0, 0, CONFIG.isSlotEnabled.sword),
            new ToolSlot("pickaxe", 16, 0, 0, 1, CONFIG.isSlotEnabled.pickaxe),
            new ToolSlot("axe",     32, 0, 0, 2, CONFIG.isSlotEnabled.axe),
            new ToolSlot("shovel",  48, 0, 0, 3, CONFIG.isSlotEnabled.shovel)
    );
}
