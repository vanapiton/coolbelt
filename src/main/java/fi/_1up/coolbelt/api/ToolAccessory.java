package fi._1up.coolbelt.api;

import com.periut.accessoryapi.api.Accessory;
import com.periut.accessoryapi.api.helper.AccessoryAccess;
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
        if(stack.isDamageable() && stack.getDamage() >= stack.getMaxDamage()) {
            AccessoryAccess.removeAccessory(player, stack.getItem());
            ((ToolbeltInventory)player.inventory).coolbelt$setSelectedAccessory(null);
            return null;
        }
        return stack;
    }
}
