package fi._1up.coolbelt.mixin.item;

import fi._1up.coolbelt.api.ToolAccessory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import org.spongepowered.asm.mixin.Mixin;

import static fi._1up.coolbelt.config.CoolbeltConfig.CONFIG;

@Mixin(ShovelItem.class)
public class ShovelItemMixin implements ToolAccessory {
    @Override
    public String[] getAccessoryTypes(ItemStack itemStack) {
        if(CONFIG.isSlotEnabled.shovel) return new String[] { "shovel" };
        return new String[] {};
    }
}
