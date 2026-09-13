package fi._1up.coolbelt.api;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

/// Inventory extension managing selected accessory items for toolbelts.
@SuppressWarnings("unused")
@ApiStatus.Experimental
public interface ToolbeltInventory extends Inventory {
    /// Retrieves the currently selected accessory [ItemStack].
    /// @return Currently selected accessory [ItemStack].
    ItemStack coolbelt$getSelectedAccessory();

    /// Sets the currently selected accessory [ItemStack].
    /// @param accessory Accessory [ItemStack] to select.
    void coolbelt$setSelectedAccessory(ItemStack accessory);
}
