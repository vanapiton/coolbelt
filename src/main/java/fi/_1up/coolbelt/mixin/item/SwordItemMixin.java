package fi._1up.coolbelt.mixin.item;

import fi._1up.coolbelt.api.ToolAccessory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;

import static fi._1up.coolbelt.config.CoolbeltConfig.CONFIG;

@Mixin(SwordItem.class)
public class SwordItemMixin implements ToolAccessory {
    @Override
    public String[] getAccessoryTypes(ItemStack itemStack) {
        if(CONFIG.isSlotEnabled.sword) return new String[] { "sword" };
        return new String[] {};
    }
}
