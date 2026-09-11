package fi._1up.coolbelt.compat.stationapi;

import fi._1up.coolbelt.api.MiningSpeedRegistry;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.event.entity.player.IsPlayerUsingEffectiveToolEvent;
import net.modificationstation.stationapi.api.event.entity.player.PlayerStrengthOnBlockEvent;
import net.modificationstation.stationapi.api.event.mod.InitEvent;
import net.modificationstation.stationapi.api.template.item.TemplateToolItem;
import net.modificationstation.stationapi.impl.item.ToolEffectivenessImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static fi._1up.coolbelt.Coolbelt.LOGGER;
import static fi._1up.coolbelt.api.MiningSpeedRegistry.*;

@SuppressWarnings("unused")
public class StationAPICompat {

    public static Optional<Float> getMiningSpeed(@Nullable ItemStack stack, @NotNull Block block) {
        if(stack == null) return Optional.empty();

        // Required for some modded tools
        if(stack.getItem() instanceof TemplateToolItem templateToolItem) {
            if(templateToolItem.isSuitableFor(block)) {
                return Optional.of(ToolEffectivenessImpl.getMiningSpeedMultiplier(stack));
            }
        }

        // TODO: Make this handle non-default BlockState
        if(ToolEffectivenessImpl.isSuitableFor(stack, block.getDefaultState())) {
            return Optional.of(ToolEffectivenessImpl.getMiningSpeedMultiplier(stack));
        }

        return Optional.of(UNMINABLE);
    }

    @EventListener
    public void onInitEvent(InitEvent event) {
        LOGGER.info("StationAPI compatibility is enabled.");
        MiningSpeedRegistry.register(StationAPICompat::getMiningSpeed);
    }

    @EventListener
    public void onPlayerStrengthOnBlockEvent(PlayerStrengthOnBlockEvent event) {
        final var previousProvider = event.resultProvider;

        event.resultProvider = () -> {
            float baseStrength = previousProvider != null ? previousProvider.getAsFloat() : STANDARD_MINING_SPEED;
            return Math.max(baseStrength, event.player.inventory.getStrengthOnBlock(event.blockState.getBlock()));
        };
    }

    @EventListener
    public void onIsPlayerUsingEffectiveToolEvent(IsPlayerUsingEffectiveToolEvent event) {
        final var previousProvider = event.resultProvider;

        event.resultProvider = () -> {
            if(previousProvider != null && previousProvider.getAsBoolean()) return true;
            return event.player.inventory.isUsingEffectiveTool(event.blockState.getBlock());
        };
    }
}
