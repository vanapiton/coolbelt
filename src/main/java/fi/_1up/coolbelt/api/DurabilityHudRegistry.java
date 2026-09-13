package fi._1up.coolbelt.api;

import com.periut.accessoryapi.api.helper.AccessoryAccess;
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

                        int slotIndex = AccessoryAccess.getAccessoryInventory(player).getSlotFor(slot.key(), 0);
                        ItemStack accessory = AccessoryAccess.getAccessory(player, slotIndex);
                        Collections.addAll(stacks, accessory);
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
