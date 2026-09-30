package fi._1up.coolbelt;

import com.periut.accessoryapi.api.AccessoryRegister;
import fi._1up.coolbelt.api.SlotAtlas;
import fi._1up.coolbelt.api.ToolSlot;
import fi._1up.coolbelt.impl.texture.VirtualTextureRegistry;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Coolbelt implements ModInitializer {
    public static final Logger LOGGER = LogManager.getFormatterLogger("Coolbelt");

    @Override
    public void onInitialize() {
        String texturePath = VirtualTextureRegistry.register("/assets/coolbelt/textures/slot/slots.png", SlotAtlas::generate);

        for (ToolSlot slot : ToolSlot.SLOTS) {
            if(!slot.enabled()) continue;

            int[] coordinates = SlotAtlas.getAtlasCoordinates(slot.baseItem());

            AccessoryRegister.add(slot.key(), texturePath, coordinates[0], coordinates[1], slot.h(), slot.v());
            LOGGER.info("Slot '%s' registered with texture path: %s", slot.key(), texturePath);
        }
    }
}