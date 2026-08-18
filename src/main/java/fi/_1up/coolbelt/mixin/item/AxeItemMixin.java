package fi._1up.coolbelt.mixin.item;

import com.periut.accessoryapi.api.Accessory;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AxeItem.class)
public class AxeItemMixin implements Accessory {
    @Override
    public String[] getAccessoryTypes(ItemStack itemStack) {
        return new String[] { "axe" };
    }
}
