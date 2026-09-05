package fi._1up.coolbelt.mixin.item;

import fi._1up.coolbelt.api.ToolAccessory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SwordItem.class)
public class SwordItemMixin implements ToolAccessory {
    @Override
    public String[] getAccessoryTypes(ItemStack itemStack) { return new String[] { "sword" }; }
}
