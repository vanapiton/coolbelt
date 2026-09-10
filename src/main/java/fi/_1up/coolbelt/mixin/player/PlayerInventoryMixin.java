package fi._1up.coolbelt.mixin.player;

import com.periut.accessoryapi.api.Accessory;
import com.periut.accessoryapi.api.helper.AccessoryAccess;
import fi._1up.coolbelt.api.AttackDamageRegistry;
import fi._1up.coolbelt.api.MiningSpeedRegistry;
import fi._1up.coolbelt.api.ToolbeltInventory;
import fi._1up.coolbelt.config.SlotAlgorithm;
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

import java.util.function.ToDoubleFunction;

import static fi._1up.coolbelt.api.AttackDamageRegistry.UNDAMAGEABLE;
import static fi._1up.coolbelt.api.AttackDamageRegistry.STANDARD_ATTACK_DAMAGE;
import static fi._1up.coolbelt.api.MiningSpeedRegistry.STANDARD_MINING_SPEED;
import static fi._1up.coolbelt.api.MiningSpeedRegistry.UNMINABLE;
import static fi._1up.coolbelt.config.CoolbeltConfig.config;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements ToolbeltInventory {
    @Shadow public PlayerEntity player;
    @Shadow public int selectedSlot = 0;
    @Shadow public ItemStack[] main;

    @Unique private ItemStack coolbelt$selectedAccessory = null;
    @Unique private static final int HOTBAR_SIZE = 9;

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void inventoryTick(CallbackInfo ci) {
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
    private int findBestItemIndex(ItemStack[] items, int length, ToDoubleFunction<ItemStack> valueExtractor, double baseline) {
        double bestValue = baseline;
        int bestIndex = -1;

        for (int i = 0; i < length; i++) {
            ItemStack stack = items[i];
            if (stack != null) {
                double value = valueExtractor.applyAsDouble(stack);
                if (value > bestValue) {
                    bestValue = value;
                    bestIndex = i;
                }
            }
        }
        return bestIndex;
    }

    @Unique
    private boolean isTool(ItemStack stack) {
        if (stack == null) return false;
        Item item = stack.getItem();
        return item instanceof ToolItem || item instanceof SwordItem;
    }

    @Unique
    private record ToolSelectionResult(
            double value,
            int selectedSlot,
            ItemStack selectedAccessory
    ) {}

    @Unique
    private ToolSelectionResult processToolSelection(ToDoubleFunction<ItemStack> valueExtractor, double baseline, double minValue) {
        ItemStack handStack = getStack(selectedSlot);
        double handValue = valueExtractor.applyAsDouble(handStack);

        int bestHotbarSlot = selectedSlot;
        if (config.searchHotbar || config.slotAlgorithm == SlotAlgorithm.ALWAYS_PREFER_HOTBAR_TOOL) {
            int bestSlot = findBestItemIndex(main, HOTBAR_SIZE, valueExtractor, handValue);
            if (bestSlot >= 0) bestHotbarSlot = bestSlot;
        }

        ItemStack bestHotbarStack = getStack(bestHotbarSlot);
        double bestHotbarValue = (bestHotbarSlot == selectedSlot) ? handValue : valueExtractor.applyAsDouble(bestHotbarStack);

        switch (config.slotAlgorithm) {
            case ALWAYS_PREFER_HAND_TOOL:
                if (isTool(handStack) || handValue > baseline) {
                    return new ToolSelectionResult(minValue, selectedSlot, null);
                }
                break;
            case ALWAYS_PREFER_HOTBAR_TOOL:
                if (bestHotbarStack == null) break;
                if (isTool(bestHotbarStack) || bestHotbarValue > baseline) {
                    return new ToolSelectionResult(bestHotbarValue, bestHotbarSlot, null);
                }
                break;
            case ALWAYS_PREFER_BELT_TOOL:
                bestHotbarValue = minValue;
                break;
            case ALWAYS_PREFER_FASTEST_TOOL:
            default:
                break;
        }

        ItemStack[] accessories = AccessoryAccess.getAccessories(player);
        int bestAccessoryIndex = findBestItemIndex(accessories, accessories.length, valueExtractor, bestHotbarValue);

        if (bestAccessoryIndex >= 0) {
            ItemStack bestStack = accessories[bestAccessoryIndex];
            if (bestStack.getItem() instanceof Accessory) {
                return new ToolSelectionResult(valueExtractor.applyAsDouble(bestStack), selectedSlot, bestStack);
            }
        }

        if (isTool(bestHotbarStack) || bestHotbarValue > baseline) {
            return new ToolSelectionResult(bestHotbarValue, bestHotbarSlot, null);
        }

        return new ToolSelectionResult(minValue, selectedSlot, null);
    }

    @Inject(method = "getAttackDamage", at = @At("HEAD"), cancellable = true)
    private void getAttackDamage(Entity target, CallbackInfoReturnable<Integer> cir) {
        ToolSelectionResult result = processToolSelection(
            stack -> AttackDamageRegistry.getDamage(stack, target),
            STANDARD_ATTACK_DAMAGE,
            UNDAMAGEABLE
        );
        if (result.value() >= 0) {
            this.selectedSlot = result.selectedSlot();
            coolbelt$setSelectedAccessory(result.selectedAccessory());
            cir.setReturnValue((int) result.value());
        }
    }

    @Unique
    private boolean shouldSkipZeroHardnessBlock(Block block) {
        if (config.useToolForZeroHardness) return false;
        return block.getHardness() == 0 && block.material.isHandHarvestable();
    }

    @Inject(method = "getStrengthOnBlock", at = @At("HEAD"), cancellable = true)
    private void getStrengthOnBlock(Block block, CallbackInfoReturnable<Float> cir) {
        if (shouldSkipZeroHardnessBlock(block)) return;

        ToolSelectionResult result = processToolSelection(
            stack -> MiningSpeedRegistry.getSpeed(stack, block),
            STANDARD_MINING_SPEED,
            UNMINABLE
        );
        if (result.value() >= 0) {
            this.selectedSlot = result.selectedSlot();
            coolbelt$setSelectedAccessory(result.selectedAccessory());
            cir.setReturnValue((float) result.value());
        }
    }

    @Inject(method = "isUsingEffectiveTool", at = @At("HEAD"), cancellable = true)
    private void isUsingEffectiveTool(Block block, CallbackInfoReturnable<Boolean> cir) {
        if (config.searchHotbar || config.slotAlgorithm == SlotAlgorithm.ALWAYS_PREFER_HOTBAR_TOOL) {
            for (int slot = 0; slot < HOTBAR_SIZE; slot++) {
                ItemStack stack = main[slot];
                if (stack != null && stack.isSuitableFor(block)) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }

        ItemStack[] accessories = AccessoryAccess.getAccessories(this.player);
        for (ItemStack stack : accessories) {
            if (stack != null && stack.isSuitableFor(block)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }

    @Override
    public ItemStack coolbelt$getSelectedAccessory() {
        return coolbelt$selectedAccessory;
    }

    @Override
    public void coolbelt$setSelectedAccessory(ItemStack accessory) {
        coolbelt$selectedAccessory = accessory;
    }
}
