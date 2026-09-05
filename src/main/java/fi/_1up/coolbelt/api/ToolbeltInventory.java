package fi._1up.coolbelt.api;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

@SuppressWarnings("unused")
public interface ToolbeltInventory extends Inventory {
    ItemStack coolbelt$getSelectedAccessory();
    void coolbelt$setSelectedAccessory(ItemStack accessory);
}
