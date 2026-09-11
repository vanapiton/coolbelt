package fi._1up.coolbelt.mixin.item;

import fi._1up.coolbelt.api.ToolAccessory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import org.spongepowered.asm.mixin.Mixin;

import static fi._1up.coolbelt.config.CoolbeltConfig.CONFIG;

@Mixin(PickaxeItem.class)
public class PickaxeItemMixin implements ToolAccessory {
    @Override
    public String[] getAccessoryTypes(ItemStack itemStack) {
        if(CONFIG.isSlotEnabled.pickaxe) return new String[] { "pickaxe" };
        return new String[] {};
    }
}
