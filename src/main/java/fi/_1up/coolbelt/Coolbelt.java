package fi._1up.coolbelt;

import com.periut.accessoryapi.api.AccessoryRegister;
import fi._1up.coolbelt.api.SlotAtlas;
import fi._1up.coolbelt.api.ToolSlot;
import fi._1up.coolbelt.api.VirtualTextureRegistry;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static fi._1up.coolbelt.config.CoolbeltConfig.CONFIG;

public class Coolbelt implements ModInitializer {
    public static final Logger LOGGER = LogManager.getFormatterLogger("Coolbelt");

    @Override
    public void onInitialize() {
        String texturePath;
        if(CONFIG.hud.generateOutlines) {
            texturePath = VirtualTextureRegistry.register("slots.png", SlotAtlas::generate);
        } else {
            texturePath = "/assets/coolbelt/textures/slot/slots.png";
        }

        for (ToolSlot slot : ToolSlot.SLOTS) {
            if(!slot.enabled()) continue;

            int[] coordinates = SlotAtlas.getAtlasCoordinates(slot.baseItem());

            AccessoryRegister.add(slot.key(), texturePath, coordinates[0], coordinates[1], slot.h(), slot.v());
            LOGGER.info("Slot '%s' registered with texture path: %s", slot.key(), texturePath);
        }
    }
}