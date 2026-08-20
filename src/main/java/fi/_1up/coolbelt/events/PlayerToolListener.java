package fi._1up.coolbelt.events;

import com.periut.accessoryapi.api.Accessory;
import com.periut.accessoryapi.api.helper.AccessoryAccess;
import fi._1up.coolbelt.Coolbelt;
import fi._1up.coolbelt.api.ToolbeltInventory;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.event.entity.player.IsPlayerUsingEffectiveToolEvent;
import net.modificationstation.stationapi.api.event.entity.player.PlayerStrengthOnBlockEvent;

public class PlayerToolListener {
    private final static float STANDARD_STRENGTH = 1;

    @EventListener
    public void onPlayerStrengthOnBlockEvent(PlayerStrengthOnBlockEvent event) {
        final var previousProvider = event.resultProvider;

        event.resultProvider = () -> {
            float bestStrength = previousProvider != null ? previousProvider.getAsFloat() : STANDARD_STRENGTH;
            ItemStack bestStack = null;
            Block block = event.blockState.getBlock();

            for (ItemStack stack : AccessoryAccess.getAccessories(event.player)) {
                if (stack == null) continue;

                float strength = stack.getMiningSpeedMultiplier(block);
                if (strength > bestStrength) {
                    bestStrength = strength;
                    bestStack = stack;
                }
            }

            if (bestStack != null && bestStack.getItem() instanceof Accessory accessory) {
                ((ToolbeltInventory) event.player.inventory).coolbelt$setSelectedAccessory(accessory);
            }

            return bestStrength;
        };
    }

    @EventListener
    public void onIsPlayerUsingEffectiveToolEvent(IsPlayerUsingEffectiveToolEvent event) {
        final var previousProvider = event.resultProvider;

        event.resultProvider = () -> {
            if(previousProvider != null && previousProvider.getAsBoolean()) return true;

            Block block = event.blockState.getBlock();

            ItemStack[] accessories = AccessoryAccess.getAccessories(event.player);
            for(ItemStack stack : accessories) {
                if(stack == null) continue;
                if(stack.isSuitableFor(block)) {
                    return true;
                }
            }

            return false;
        };
    }
}
