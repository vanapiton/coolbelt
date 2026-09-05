package fi._1up.coolbelt.api;

import com.periut.accessoryapi.api.Accessory;
import com.periut.accessoryapi.api.helper.AccessoryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface ToolAccessory extends Accessory {
    default ItemStack tickWhileWorn(PlayerEntity player, ItemStack stack) {
        if(stack.isDamageable() && stack.getDamage() >= stack.getMaxDamage()) {
            AccessoryAccess.removeAccessory(player, stack.getItem());
            ((ToolbeltInventory)player.inventory).coolbelt$setSelectedAccessory(null);
            return null;
        }
        return stack;
    }
}
