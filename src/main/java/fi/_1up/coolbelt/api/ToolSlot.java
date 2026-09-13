package fi._1up.coolbelt.api;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

import static fi._1up.coolbelt.config.CoolbeltConfig.CONFIG;

/// Representation of a tool slot configuration containing texture coordinates and grid positions.
/// @param key Identifier string for the tool slot.
/// @param texX Horizontal texture coordinate offset.
/// @param texY Vertical texture coordinate offset.
/// @param h Horizontal grid index.
/// @param v Vertical grid index.
/// @param enabled State indicating if the slot is enabled in the configuration.
@ApiStatus.Experimental
public record ToolSlot(String key, int texX, int texY, int h, int v, boolean enabled) {
    /// Immutable [List] of default [ToolSlot] configurations.
    public static final List<ToolSlot> SLOTS = List.of(
            new ToolSlot("sword",    0, 0, 0, 0, CONFIG.isSlotEnabled.sword),
            new ToolSlot("pickaxe", 16, 0, 0, 1, CONFIG.isSlotEnabled.pickaxe),
            new ToolSlot("axe",     32, 0, 0, 2, CONFIG.isSlotEnabled.axe),
            new ToolSlot("shovel",  48, 0, 0, 3, CONFIG.isSlotEnabled.shovel)
    );
}
