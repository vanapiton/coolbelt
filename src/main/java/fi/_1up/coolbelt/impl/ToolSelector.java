package fi._1up.coolbelt.impl;

import com.periut.accessoryapi.api.Accessory;
import com.periut.accessoryapi.api.helper.AccessoryAccess;
import fi._1up.coolbelt.config.SlotAlgorithm;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;

import java.util.function.ToDoubleFunction;

import static fi._1up.coolbelt.config.RootConfig.CONFIG;

public class ToolSelector {
    public static final int HOTBAR_SIZE = 9;

    public record ToolSelectionResult(
        double value,
        int selectedSlot,
        ItemStack selectedAccessory
    ) {}

    public static boolean shouldSkipZeroHardnessBlock(Block block) {
        if (CONFIG.useToolForZeroHardness) return false;
        return block.getHardness() == 0 && block.material.isHandHarvestable();
    }

    private static boolean isTool(ItemStack stack) {
        if (stack == null) return false;
        Item item = stack.getItem();
        return item instanceof ToolItem || item instanceof SwordItem || item instanceof ShearsItem;
    }

    private static int findBestItemIndex(ItemStack[] items, int length, ToDoubleFunction<ItemStack> valueExtractor, double baseline) {
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

    public static ToolSelectionResult processToolSelection(ToDoubleFunction<ItemStack> valueExtractor, double baseline, double minValue, PlayerInventory inv) {
        ItemStack handStack = inv.getStack(inv.selectedSlot);
        double handValue = valueExtractor.applyAsDouble(handStack);

        int bestHotbarSlot = inv.selectedSlot;
        if (CONFIG.searchHotbar || CONFIG.slotAlgorithm == SlotAlgorithm.ALWAYS_PREFER_HOTBAR_TOOL) {
            int bestSlot = findBestItemIndex(inv.main, HOTBAR_SIZE, valueExtractor, handValue);
            if (bestSlot >= 0) bestHotbarSlot = bestSlot;
        }

        ItemStack bestHotbarStack = inv.getStack(bestHotbarSlot);
        double bestHotbarValue = (bestHotbarSlot == inv.selectedSlot) ? handValue : valueExtractor.applyAsDouble(bestHotbarStack);

        switch (CONFIG.slotAlgorithm) {
            case ALWAYS_PREFER_HAND_TOOL:
                if (isTool(handStack) || handValue > baseline) {
                    return new ToolSelectionResult(minValue, inv.selectedSlot, null);
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

        ItemStack[] accessories = AccessoryAccess.getAccessories(inv.player);
        int bestAccessoryIndex = findBestItemIndex(accessories, accessories.length, valueExtractor, bestHotbarValue);

        if (bestAccessoryIndex >= 0) {
            ItemStack bestStack = accessories[bestAccessoryIndex];
            if (bestStack.getItem() instanceof Accessory) {
                return new ToolSelectionResult(valueExtractor.applyAsDouble(bestStack), inv.selectedSlot, bestStack);
            }
        }

        if (isTool(bestHotbarStack) || bestHotbarValue > baseline) {
            return new ToolSelectionResult(bestHotbarValue, bestHotbarSlot, null);
        }

        return new ToolSelectionResult(minValue, inv.selectedSlot, null);
    }
}
