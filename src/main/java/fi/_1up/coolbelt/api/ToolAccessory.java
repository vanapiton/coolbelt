package fi._1up.coolbelt.api;

import com.periut.accessoryapi.api.Accessory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

/// Interface for tool accessories with durability checking functionality.
@ApiStatus.Experimental
public interface ToolAccessory extends Accessory {
    /// Handles per-tick behavior for worn items and removes broken accessory items.
    /// @param player [PlayerEntity] wearing the accessory.
    /// @param stack [ItemStack] representing the accessory.
    /// @return Original [ItemStack], or null if destroyed through damage.
    default ItemStack tickWhileWorn(PlayerEntity player, ItemStack stack) {
        if(DurabilityChecker.isAtOrBelow(stack, 0)) {
            ((ToolbeltInventory)player.inventory).coolbelt$setSelectedAccessory(null);
            return null;
        }
        return stack;
    }
}
