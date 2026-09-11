package fi._1up.coolbelt;

import com.periut.accessoryapi.api.AccessoryRegister;
import fi._1up.coolbelt.api.ToolSlot;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Coolbelt implements ModInitializer {
    public static final Logger LOGGER = LogManager.getFormatterLogger("Coolbelt");

    private static final String SLOT_TEX_PATH = "/assets/coolbelt/textures/slot/tools.png";

    @Override
    public void onInitialize() {
        for (ToolSlot slot : ToolSlot.SLOTS) {
            if(!slot.enabled()) continue;
            LOGGER.info("Accessory slot '%s' registered.", slot.key());
            AccessoryRegister.add(slot.key(), SLOT_TEX_PATH, slot.texX(), slot.texY(), slot.h(), slot.v());
        }
    }
}