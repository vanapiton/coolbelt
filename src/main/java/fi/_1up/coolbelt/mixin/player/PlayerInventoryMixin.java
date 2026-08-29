package fi._1up.coolbelt.mixin.player;

import com.periut.accessoryapi.api.Accessory;
import com.periut.accessoryapi.api.helper.AccessoryAccess;
import fi._1up.coolbelt.api.ToolbeltInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements ToolbeltInventory {
    @Unique
    private ItemStack coolbelt$selectedAccessory = null;

    @Shadow
    public PlayerEntity player;
    @Shadow
    public int selectedSlot = 0;

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if(!player.handSwinging) {
            coolbelt$setSelectedAccessory(null);
        }
    }

    @Inject(method = "scrollInHotbar", at = @At("HEAD"))
    @Environment(EnvType.CLIENT)
    private void scrollInHotbar(int dir, CallbackInfo ci) {
        coolbelt$setSelectedAccessory(null);
    }

    @Inject(method = "getSelectedItem", at = @At("HEAD"), cancellable = true)
    private void getSelectedItem(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = coolbelt$getSelectedAccessory();
        if (stack != null) cir.setReturnValue(stack);
    }

    @Unique
    private <T extends Comparable<T>> ItemStack findBestAccessory(Function<ItemStack, T> valueExtractor, T baseline) {
        T bestValue = baseline;
        ItemStack bestStack = null;

        for (ItemStack stack : AccessoryAccess.getAccessories(player)) {
            if (stack == null) continue;

            T value = valueExtractor.apply(stack);
            if (value.compareTo(bestValue) > 0) {
                bestValue = value;
                bestStack = stack;
            }
        }
        return bestStack;
    }

    @Inject(method = "getAttackDamage", at = @At("HEAD"), cancellable = true)
    private void getAttackDamage(Entity target, CallbackInfoReturnable<Integer> cir) {
        ItemStack selectedItem = getStack(selectedSlot);
        int baseDamage = selectedItem != null ? selectedItem.getAttackDamage(target) : STANDARD_ATTACK_DAMAGE;

        ItemStack bestStack = findBestAccessory(stack -> stack.getAttackDamage(target), baseDamage);

        if (bestStack != null && bestStack.getItem() instanceof Accessory) {
            coolbelt$setSelectedAccessory(bestStack);
            cir.setReturnValue(bestStack.getAttackDamage(target));
        }
    }

    @Unique
    private float calculateEffectiveStrength(ItemStack stack, Block block) {
        if (stack == null) {
            return block.material.isHandHarvestable() ? STANDARD_MINING_SPEED : Float.NEGATIVE_INFINITY;
        }

        boolean isSuitable = block.material.isHandHarvestable() || stack.isSuitableFor(block);
        return isSuitable ? stack.getMiningSpeedMultiplier(block) : Float.NEGATIVE_INFINITY;
    }

    @Inject(method = "getStrengthOnBlock", at = @At("HEAD"), cancellable = true)
    private void getStrengthOnBlock(Block block, CallbackInfoReturnable<Float> cir) {
        ItemStack selectedItem = getStack(selectedSlot);
        float baseStrength = calculateEffectiveStrength(selectedItem, block);

        ItemStack bestStack = findBestAccessory(stack -> calculateEffectiveStrength(stack, block), baseStrength);

        if (bestStack != null && bestStack.getItem() instanceof Accessory) {
            coolbelt$setSelectedAccessory(bestStack);
            cir.setReturnValue(bestStack.getMiningSpeedMultiplier(block));
        }
    }

    @Inject(method = "isUsingEffectiveTool", at = @At("HEAD"), cancellable = true)
    private void isUsingEffectiveTool(Block block, CallbackInfoReturnable<Boolean> cir) {
        ItemStack[] accessories = AccessoryAccess.getAccessories(player);

        for(ItemStack stack : accessories) {
            if(stack == null) continue;
            if(stack.isSuitableFor(block)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }

    @Override
    public ItemStack coolbelt$getSelectedAccessory() {
        if(coolbelt$selectedAccessory == null) return null;

        ItemStack stack = coolbelt$selectedAccessory;

        if(stack.isDamageable() && stack.getDamage() >= stack.getMaxDamage()) {
            AccessoryAccess.removeAccessory(player, stack.getItem());
            coolbelt$setSelectedAccessory(null);
            return null;
        }

        return stack;
    }

    @Override
    public void coolbelt$setSelectedAccessory(ItemStack accessory) {
        coolbelt$selectedAccessory = accessory;
    }
}
