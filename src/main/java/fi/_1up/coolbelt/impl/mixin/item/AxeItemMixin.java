package fi._1up.coolbelt.impl.mixin.item;

import fi._1up.coolbelt.api.ToolAccessory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.AxeItem;
import org.spongepowered.asm.mixin.Mixin;

import static fi._1up.coolbelt.config.CoolbeltConfig.CONFIG;

@Mixin(AxeItem.class)
public class AxeItemMixin implements ToolAccessory {
    @Override
    public String[] getAccessoryTypes(ItemStack itemStack) {
        if(CONFIG.isSlotEnabled.axe) return new String[] { "axe" };
        return new String[] {};
    }
}
