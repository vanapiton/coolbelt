package fi._1up.coolbelt.compat.stationapi;

import fi._1up.coolbelt.api.ToolbeltInventory;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.event.entity.player.IsPlayerUsingEffectiveToolEvent;
import net.modificationstation.stationapi.api.event.entity.player.PlayerStrengthOnBlockEvent;
import net.modificationstation.stationapi.impl.item.ToolEffectivenessImpl;

@SuppressWarnings("unused")
public class StationAPICompat {

    public static float getMiningSpeedMultiplier(ItemStack stack) {
        return ToolEffectivenessImpl.getMiningSpeedMultiplier(stack);
    }

    public static boolean isSuitableFor(ItemStack stack, Block block) {
        // TODO: Make this handle non-default BlockState
        return ToolEffectivenessImpl.isSuitableFor(stack, block.getDefaultState());
    }

    @EventListener
    public void onPlayerStrengthOnBlockEvent(PlayerStrengthOnBlockEvent event) {
        final var previousProvider = event.resultProvider;

        event.resultProvider = () -> {
            float baseStrength = previousProvider != null ? previousProvider.getAsFloat() : ToolbeltInventory.STANDARD_MINING_SPEED;
            return Math.max(baseStrength, event.player.inventory.getStrengthOnBlock(event.blockState.getBlock()));
        };
    }

    @EventListener
    public void onIsPlayerUsingEffectiveToolEvent(IsPlayerUsingEffectiveToolEvent event) {
        final var previousProvider = event.resultProvider;

        event.resultProvider = () -> {
            if(previousProvider != null && previousProvider.getAsBoolean()) return true;
            return event.player.inventory.isUsingEffectiveTool(event.blockState.getBlock());
        };
    }
}
