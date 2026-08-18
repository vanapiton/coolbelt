package fi._1up.coolbelt.events;

import com.periut.accessoryapi.api.Accessory;
import com.periut.accessoryapi.api.helper.AccessoryAccess;
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
            ItemStack bestAccessory = null;
            Block block = event.blockState.getBlock();

            for (ItemStack accessory : AccessoryAccess.getAccessories(event.player)) {
                if (accessory == null) continue;

                float strength = accessory.getMiningSpeedMultiplier(block);
                if (strength > bestStrength) {
                    bestStrength = strength;
                    bestAccessory = accessory;
                }
            }

            if (bestAccessory != null && bestAccessory.getItem() instanceof Accessory accessory) {
                ((ToolbeltInventory) event.player.inventory).coolbelt$setSelectedAccessory(accessory);
                return bestStrength;
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
            for(ItemStack accessory : accessories) {
                if(accessory == null) continue;
                if(accessory.isSuitableFor(block)) {
                    return true;
                }
            }

            return false;
        };
    }
}
