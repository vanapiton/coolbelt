package fi._1up.coolbelt.mixin.item;

import fi._1up.coolbelt.api.ToolAccessory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.AxeItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AxeItem.class)
public class AxeItemMixin implements ToolAccessory {
    @Override
    public String[] getAccessoryTypes(ItemStack itemStack) {
        return new String[] { "axe" };
    }
}
