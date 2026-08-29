package fi._1up.coolbelt.compat.stationapi;

import fi._1up.coolbelt.api.ToolbeltInventory;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.entity.player.IsPlayerUsingEffectiveToolEvent;
import net.modificationstation.stationapi.api.event.entity.player.PlayerStrengthOnBlockEvent;

@SuppressWarnings("unused")
public class StationAPICompat {
    @EventListener
    public void onPlayerStrengthOnBlockEvent(PlayerStrengthOnBlockEvent event) {
        final var previousProvider = event.resultProvider;

        event.resultProvider = () -> {
            float baseStrength = previousProvider != null ? previousProvider.getAsFloat() : ToolbeltInventory.STANDARD_MINING_SPEED;
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
