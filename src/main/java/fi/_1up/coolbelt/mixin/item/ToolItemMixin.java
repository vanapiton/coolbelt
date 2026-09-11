package fi._1up.coolbelt.mixin.item;

import fi._1up.coolbelt.api.ToolAccessory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import org.spongepowered.asm.mixin.Mixin;

import static fi._1up.coolbelt.config.CoolbeltConfig.CONFIG;

@Mixin(ToolItem.class)
public class ToolItemMixin implements ToolAccessory {
    @Override
    public String[] getAccessoryTypes(ItemStack itemStack) {
        if(CONFIG.allowSlottingModdedTools) return new String[]{"pickaxe", "axe", "shovel"};
        return new String[] {};
    }
}
