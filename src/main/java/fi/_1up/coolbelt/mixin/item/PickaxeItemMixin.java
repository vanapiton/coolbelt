package fi._1up.coolbelt.mixin.item;

import fi._1up.coolbelt.api.ToolAccessory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PickaxeItem.class)
public class PickaxeItemMixin implements ToolAccessory {
    @Override
    public String[] getAccessoryTypes(ItemStack itemStack) {
        return new String[] { "pickaxe" };
    }
}
