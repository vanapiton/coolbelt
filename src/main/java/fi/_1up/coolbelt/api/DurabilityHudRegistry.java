package fi._1up.coolbelt.api;

import com.periut.accessoryapi.api.helper.AccessoryAccess;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static fi._1up.coolbelt.config.CoolbeltConfig.config;

public class DurabilityHudRegistry {
    private static final List<DurabilityHudGroup> REGISTRY = new ArrayList<>();

    static {
        // Tools
        register(
                player -> {
                    if (!config.hud.alwaysShowTools) {
                        return Collections.singletonList(((ToolbeltInventory) player.inventory).coolbelt$getSelectedAccessory());
                    }

                    List<ItemStack> stacks = new ArrayList<>();

                    for (ToolSlot slot : ToolSlot.SLOTS) {
                        if (!slot.enabled()) continue;

                        ItemStack[] accessories = AccessoryAccess.getAccessories(player, slot.key());
                        if (accessories.length == 0) {
                            // Keep empty spots for empty slots
                            stacks.add(null);
                            continue;
                        }
                        Collections.addAll(stacks, accessories);
                    }

                    return stacks;
                },
                config.hud.toolsOffsetX,
                config.hud.toolsOffsetY,
                config.hud.toolStepX,
                config.hud.toolStepY
        );

        // Armor
        register(
                player -> {
                    if (!config.hud.alwaysShowArmors) return Collections.emptyList();
                    return Arrays.stream(player.inventory.armor).toList();
                },
                config.hud.armorsOffsetX,
                config.hud.armorsOffsetY,
                config.hud.armorStepX,
                config.hud.armorStepY
        );
    }

    public static void register(Function<ClientPlayerEntity, List<ItemStack>> supplier, int x, int y, int stepX, int stepY) {
        REGISTRY.add(new DurabilityHudGroup(supplier, x, y, stepX, stepY));
    }

    public static List<DurabilityHudGroup> getGroups() {
        return REGISTRY;
    }
}
