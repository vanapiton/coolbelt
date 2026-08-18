package fi._1up.coolbelt.mixin.item;

import com.periut.accessoryapi.api.Accessory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PickaxeItem.class)
public class PickaxeItemMixin implements Accessory {
    @Override
    public String[] getAccessoryTypes(ItemStack itemStack) {
        return new String[] { "pickaxe" };
    }
}
