package fi._1up.coolbelt.api;

import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Function;

public record DurabilityHudGroup(
        Function<ClientPlayerEntity, List<ItemStack>> stackSupplier,
        int xSlots,
        int ySlots,
        int stepXSlots,
        int stepYSlots
) {
    private static final int SLOT_STEP = 20;
    public int x() { return xSlots * SLOT_STEP; }
    public int y() { return ySlots * SLOT_STEP; }
    public int stepX() { return stepXSlots * SLOT_STEP; }
    public int stepY() { return stepYSlots * SLOT_STEP; }
}
