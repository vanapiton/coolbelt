package fi._1up.coolbelt.api;

import com.periut.accessoryapi.api.Accessory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface ToolbeltInventory extends Inventory {
    int EMPTY_SLOT = -1;
    ItemStack coolbelt$getSelectedAccessory();
    void coolbelt$setSelectedAccessory(Accessory accessory);
}
