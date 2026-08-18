package fi._1up.coolbelt.mixin.item;

import com.periut.accessoryapi.api.Accessory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShovelItem.class)
public class ShovelItemMixin implements Accessory {
    @Override
    public String[] getAccessoryTypes(ItemStack itemStack) {
        return new String[] { "shovel" };
    }
}
