package fi._1up.coolbelt.impl.compat.stationapi;

import fi._1up.coolbelt.api.ItemEvalRegistry;
import fi._1up.coolbelt.api.MiningSpeedRegistry;
import fi._1up.coolbelt.api.ToolbeltInventory;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.event.entity.player.IsPlayerUsingEffectiveToolEvent;
import net.modificationstation.stationapi.api.event.entity.player.PlayerStrengthOnBlockEvent;
import net.modificationstation.stationapi.api.event.mod.InitEvent;
import net.modificationstation.stationapi.api.item.tool.StationTool;
import net.modificationstation.stationapi.impl.item.ToolEffectivenessImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static fi._1up.coolbelt.Coolbelt.LOGGER;
import static fi._1up.coolbelt.api.MiningSpeedRegistry.STANDARD_MINING_SPEED;
import static fi._1up.coolbelt.api.MiningSpeedRegistry.UNMINABLE;
import static fi._1up.coolbelt.impl.ToolSelector.*;

@SuppressWarnings("unused")
public class StationAPICompat {
    public static final boolean IS_STAPI_LOADED = FabricLoader.getInstance().isModLoaded("stationapi");

    public static Block toBlock(Object object) {
        if(object instanceof BlockState state) return state.getBlock();
        if(object instanceof Block block) return block;
        return null;
    }

    public static Optional<Float> getMiningSpeed(@Nullable ItemStack stack, @NotNull Object object) {
        if (stack == null) return Optional.empty();
        if (!(object instanceof BlockState state)) return Optional.empty();

        if(stack.getItem() instanceof StationTool stationTool) {
            LOGGER.info(state.isIn(stationTool.getEffectiveBlocks(stack)));
        }

        // Required for some modded tools
        if (stack.getItem() instanceof ToolItem toolItem) {
            if (toolItem.isSuitableFor(state.getBlock())) {
                return Optional.of(ToolEffectivenessImpl.getMiningSpeedMultiplier(stack));
            }
        }

        return Optional.of(UNMINABLE);
    }

    @EventListener
    public void onInitEvent(InitEvent event) {
        LOGGER.info("StationAPI compatibility is enabled.");
        MiningSpeedRegistry.registerInternal(ItemEvalRegistry.PRIORITY_DEFAULT, StationAPICompat::getMiningSpeed);
    }

    @EventListener
    public void onPlayerStrengthOnBlockEvent(PlayerStrengthOnBlockEvent event) {
        final var previousProvider = event.resultProvider;

        event.resultProvider = () -> {
            float baseStrength = previousProvider != null ? previousProvider.getAsFloat() : STANDARD_MINING_SPEED;

            PlayerInventory inv = event.player.inventory;
            BlockState state = event.blockState;

            if (((ToolbeltInventory)inv).coolbelt$isBeltIgnored()) return baseStrength;
            if (shouldSkipZeroHardnessBlock(state.getBlock())) return baseStrength;

            ToolSelectionResult result = processToolSelection(
                stack -> MiningSpeedRegistry.getSpeed(stack, state),
                STANDARD_MINING_SPEED,
                UNMINABLE,
                inv
            );

            if (result.value() >= 0) {
                inv.selectedSlot = result.selectedSlot();
                ((ToolbeltInventory)inv).coolbelt$setSelectedAccessory(result.selectedAccessory());
                return (float) Math.max(baseStrength, result.value());
            }

            return baseStrength;
        };
    }

    @EventListener
    public void onIsPlayerUsingEffectiveToolEvent(IsPlayerUsingEffectiveToolEvent event) {
        final var previousProvider = event.resultProvider;

        event.resultProvider = () -> {
            if (previousProvider != null && previousProvider.getAsBoolean()) return true;
            return event.player.inventory.isUsingEffectiveTool(event.blockState.getBlock());
        };
    }
}
