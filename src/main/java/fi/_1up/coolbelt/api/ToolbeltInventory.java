package fi._1up.coolbelt.api;

import com.periut.accessoryapi.api.Accessory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface ToolbeltInventory extends Inventory {
    int STANDARD_ATTACK_DAMAGE = 1;
    float STANDARD_MINING_SPEED = 1;
    ItemStack coolbelt$getSelectedAccessory();
    void coolbelt$setSelectedAccessory(ItemStack accessory);
}
