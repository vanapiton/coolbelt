package fi._1up.coolbelt.api;

import com.periut.accessoryapi.api.helper.AccessoryAccess;
import fi._1up.coolbelt.compat.ModVersionChecker;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import static fi._1up.coolbelt.config.CoolbeltConfig.CONFIG;
import static fi._1up.coolbelt.Coolbelt.LOGGER;

/// Registry managing durability HUD display groups and their positioning parameters.
@ApiStatus.Experimental
public final class DurabilityHudRegistry {
    /// Internal [List] storing registered [DurabilityHudGroup] instances.
    private static final List<DurabilityHudGroup> REGISTRY = new CopyOnWriteArrayList<>();

    static {
        // Tools
        register(
                player -> {
                    if (!CONFIG.hud.alwaysShowTools) {
                        return Collections.singletonList(((ToolbeltInventory) player.inventory).coolbelt$getSelectedAccessory());
                    }

                    List<ItemStack> stacks = new ArrayList<>();

                    for (ToolSlot slot : ToolSlot.SLOTS) {
                        if (!slot.enabled()) continue;

                        ItemStack accessory;
                        if(ModVersionChecker.isAtLeast("accessoryapi", "0.9.0")) {
                            int slotIndex = AccessoryAccess.getAccessoryInventory(player).getSlotFor(slot.key(), 0);
                            accessory = AccessoryAccess.getAccessory(player, slotIndex);
                        } else {
                            // Hacky solution for Accessory API versions prior to 0.9.0
                            // Shows duplicates of tools with multiple valid slots, not ideal
                            ItemStack[] accessories = AccessoryAccess.getAccessories(player, slot.key());
                            accessory = accessories.length > 0 ? accessories[0] : null;
                        }

                        stacks.add(accessory);
                    }

                    return stacks;
                },
                CONFIG.hud.toolsOffsetX,
                CONFIG.hud.toolsOffsetY,
                CONFIG.hud.toolStepX,
                CONFIG.hud.toolStepY
        );

        // Armor
        register(
                player -> {
                    if (!CONFIG.hud.alwaysShowArmors) return Collections.emptyList();
                    return Arrays.stream(player.inventory.armor).toList();
                },
                CONFIG.hud.armorsOffsetX,
                CONFIG.hud.armorsOffsetY,
                CONFIG.hud.armorStepX,
                CONFIG.hud.armorStepY
        );

        // Hacky ignore overlay
        register(
                player -> {
                    ToolbeltInventory inv = ((ToolbeltInventory) player.inventory);
                    if(!inv.coolbelt$isBeltIgnored()) return Collections.emptyList();

                    ItemStack overlayStack = new ItemStack(Block.COBWEB);

                    if (!CONFIG.hud.alwaysShowTools) {
                        return Collections.singletonList(overlayStack);
                    }

                    List<ItemStack> stacks = new ArrayList<>();

                    for (ToolSlot slot : ToolSlot.SLOTS) {
                        if (!slot.enabled()) continue;

                        Collections.addAll(stacks, overlayStack);
                    }

                    return stacks;
                },
                CONFIG.hud.toolsOffsetX,
                CONFIG.hud.toolsOffsetY,
                CONFIG.hud.toolStepX,
                CONFIG.hud.toolStepY
        );
    }

    /// Private constructor to prevent instantiation.
    private DurabilityHudRegistry() {}

    /// Registers a new [DurabilityHudGroup] with layout properties.
    /// @param supplier [Function] mapping a [ClientPlayerEntity] to a [List] of [ItemStack] instances to display.
    /// @param xSlots Horizontal offset measured in slots.
    /// @param ySlots Vertical offset measured in slots.
    /// @param stepXSlots Horizontal step between items measured in slots.
    /// @param stepYSlots Vertical step between items measured in slots.
    public static void register(Function<ClientPlayerEntity, List<ItemStack>> supplier, int xSlots, int ySlots, int stepXSlots, int stepYSlots) {
        REGISTRY.add(new DurabilityHudGroup(supplier, xSlots, ySlots, stepXSlots, stepYSlots));
        LOGGER.info("Durability HUD group registered at (%+d, %+d).", xSlots, ySlots);
    }

    /// Retrieves all registered [DurabilityHudGroup] instances.
    /// @return [List] containing all registered [DurabilityHudGroup] instances.
    public static List<DurabilityHudGroup> getGroups() {
        return REGISTRY;
    }
}
