package fi._1up.coolbelt.api;

import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Function;

/// Layout configuration holding item providers and scaled screen offsets for a durability HUD group.
/// @param stackSupplier [Function] mapping a [ClientPlayerEntity] to a [List] of [ItemStack] instances to display.
/// @param xSlots Horizontal offset measured in slots.
/// @param ySlots Vertical offset measured in slots.
/// @param stepXSlots Horizontal step between items measured in slots.
/// @param stepYSlots Vertical step between items measured in slots.
@ApiStatus.Experimental
public record DurabilityHudGroup(
        Function<ClientPlayerEntity, List<ItemStack>> stackSupplier,
        int xSlots,
        int ySlots,
        int stepXSlots,
        int stepYSlots
) {
    /// Pixel step multiplier per slot unit.
    private static final int SLOT_STEP = 20;

    /// Calculates the horizontal screen offset in pixels.
    /// @return Horizontal offset in pixels.
    public int x() { return xSlots * SLOT_STEP; }

    /// Calculates the vertical screen offset in pixels.
    /// @return Vertical offset in pixels.
    public int y() { return ySlots * SLOT_STEP; }

    /// Calculates the horizontal pixel step between items.
    /// @return Horizontal step in pixels.
    public int stepX() { return stepXSlots * SLOT_STEP; }

    /// Calculates the vertical pixel step between items.
    /// @return Vertical step in pixels.
    public int stepY() { return stepYSlots * SLOT_STEP; }
}
