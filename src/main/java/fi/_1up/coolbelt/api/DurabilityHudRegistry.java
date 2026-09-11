package fi._1up.coolbelt.api;

import com.periut.accessoryapi.api.helper.AccessoryAccess;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static fi._1up.coolbelt.config.CoolbeltConfig.CONFIG;
import static fi._1up.coolbelt.Coolbelt.LOGGER;

public class DurabilityHudRegistry {
    private static final List<DurabilityHudGroup> REGISTRY = new ArrayList<>();

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

    public static void register(Function<ClientPlayerEntity, List<ItemStack>> supplier, int x, int y, int stepX, int stepY) {
        REGISTRY.add(new DurabilityHudGroup(supplier, x, y, stepX, stepY));
        LOGGER.info("Durability HUD group registered at (%+d, %+d).", x, y);
    }

    public static List<DurabilityHudGroup> getGroups() {
        return REGISTRY;
    }
}
