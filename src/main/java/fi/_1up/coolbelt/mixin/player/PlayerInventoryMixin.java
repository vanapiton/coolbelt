package fi._1up.coolbelt.mixin.player;

import com.periut.accessoryapi.api.Accessory;
import com.periut.accessoryapi.api.helper.AccessoryAccess;
import fi._1up.coolbelt.api.ToolbeltInventory;
import fi._1up.coolbelt.compat.stationapi.StationAPICompat;
import fi._1up.coolbelt.config.HotbarAlgorithm;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

import static fi._1up.coolbelt.config.CoolbeltConfig.config;

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

        for (ItemStack accessoryStack : AccessoryAccess.getAccessories(player)) {
            if (accessoryStack == null) continue;

            T value = valueExtractor.apply(accessoryStack);
            if (value.compareTo(bestValue) > 0) {
                bestValue = value;
                bestStack = accessoryStack;
            }
        }
        return bestStack;
    }

    @Unique
    private <T extends Comparable<T>> int findBestHotbarSlot(Function<ItemStack, T> valueExtractor, T baseline) {
        T bestValue = baseline;
        int bestSlot = selectedSlot;

        for (int slot = 0; slot < 9; slot++) {
            ItemStack hotbarStack = getStack(slot);
            if (hotbarStack == null) continue;

            T value = valueExtractor.apply(hotbarStack);
            if (value.compareTo(bestValue) > 0) {
                bestValue = value;
                bestSlot = slot;
            }
        }

        return bestSlot;
    }

    @Unique
    private boolean isTool(ItemStack stack) {
        if (stack == null) return false;
        Item item = stack.getItem();
        return item instanceof ToolItem || item instanceof SwordItem;
    }

    @Unique
    private <T extends Comparable<T>> void processToolSelection(
            Function<ItemStack, T> valueExtractor,
            T baseline,
            T minValue,
            CallbackInfoReturnable<T> cir
    ) {
        ItemStack handStack = getStack(selectedSlot);
        T handValue = valueExtractor.apply(handStack);

        int bestHotbarSlot = selectedSlot;
        if (config.searchWholeHotbar || config.hotbarAlgorithm == HotbarAlgorithm.ALWAYS_PREFER_HOTBAR_TOOL) {
            bestHotbarSlot = findBestHotbarSlot(valueExtractor, handValue);
        }
        ItemStack bestHotbarStack = getStack(bestHotbarSlot);
        T bestHotbarValue = valueExtractor.apply(bestHotbarStack);

        switch (config.hotbarAlgorithm) {
            case ALWAYS_PREFER_HAND_TOOL:
                if (isTool(handStack) || handValue.compareTo(baseline) > 0) return;
                break;
            case ALWAYS_PREFER_HOTBAR_TOOL:
                if (bestHotbarStack == null) break;
                if (isTool(bestHotbarStack) || bestHotbarValue.compareTo(baseline) > 0) {
                    this.selectedSlot = bestHotbarSlot;
                    cir.setReturnValue(bestHotbarValue);
                    return;
                }
                break;
            case ALWAYS_PREFER_BELT_TOOL:
                bestHotbarValue = minValue;
                break;
            case ALWAYS_PREFER_FASTEST_TOOL:
            default:
                break;
        }

        ItemStack bestStack = findBestAccessory(valueExtractor, bestHotbarValue);

        if (bestStack != null && bestStack.getItem() instanceof Accessory) {
            coolbelt$setSelectedAccessory(bestStack);
            cir.setReturnValue(valueExtractor.apply(bestStack));
            return;
        }

        if (isTool(bestHotbarStack) || bestHotbarValue.compareTo(baseline) > 0) {
            this.selectedSlot = bestHotbarSlot;
            cir.setReturnValue(bestHotbarValue);
        }
    }

    @Inject(method = "getAttackDamage", at = @At("HEAD"), cancellable = true)
    private void getAttackDamage(Entity target, CallbackInfoReturnable<Integer> cir) {
        processToolSelection(
                stack -> stack != null ? stack.getAttackDamage(target) : STANDARD_ATTACK_DAMAGE,
                STANDARD_ATTACK_DAMAGE,
                Integer.MIN_VALUE,
                cir
        );
    }

    @Unique
    private float calculateEffectiveStrength(ItemStack stack, Block block) {
        if (stack == null) {
            return block.material.isHandHarvestable() ? STANDARD_MINING_SPEED : Float.NEGATIVE_INFINITY;
        }

        if(!config.useSwordForMining && stack.getItem() instanceof Accessory accessory) {
            String[] types = accessory.getAccessoryTypes(stack);
            for (String type : types) {
                if (type.equals("sword")) return Float.NEGATIVE_INFINITY;
            }
        }

        if(StationAPICompat.IS_STAPI_LOADED) {
            boolean isSuitable = StationAPICompat.isSuitableFor(stack, block);
            return isSuitable ? StationAPICompat.getMiningSpeedMultiplier(stack) : Float.NEGATIVE_INFINITY;
        }

        boolean isSuitable = block.material.isHandHarvestable() || stack.isSuitableFor(block);
        return isSuitable ? stack.getMiningSpeedMultiplier(block) : Float.NEGATIVE_INFINITY;
    }

    @Unique
    private boolean shouldSkipZeroHardnessBlock(Block block) {
        if (config.useToolForZeroHardness) return false;
        return block.getHardness() == 0 && block.material.isHandHarvestable();
    }

    @Inject(method = "getStrengthOnBlock", at = @At("HEAD"), cancellable = true)
    private void getStrengthOnBlock(Block block, CallbackInfoReturnable<Float> cir) {
        if (shouldSkipZeroHardnessBlock(block)) return;

        processToolSelection(
                stack -> calculateEffectiveStrength(stack, block),
                STANDARD_MINING_SPEED,
                Float.NEGATIVE_INFINITY,
                cir
        );
    }

    @Inject(method = "isUsingEffectiveTool", at = @At("HEAD"), cancellable = true)
    private void isUsingEffectiveTool(Block block, CallbackInfoReturnable<Boolean> cir) {
        ItemStack[] accessories = AccessoryAccess.getAccessories(player);

        if (config.searchWholeHotbar || config.hotbarAlgorithm == HotbarAlgorithm.ALWAYS_PREFER_HOTBAR_TOOL) {
            for (int slot = 0; slot < 9; slot++) {
                ItemStack stack = getStack(slot);
                if (stack != null && stack.isSuitableFor(block)) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }

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
