package fi._1up.coolbelt;

import com.periut.accessoryapi.api.AccessoryRegister;
import fi._1up.coolbelt.api.OutlineTextureGenerator;
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
        for (ToolSlot slot : ToolSlot.SLOTS) {
            if(!slot.enabled()) continue;

            String texturePath;
            int texX = 0;
            int texY = 0;

            if(CONFIG.hud.generateOutlines) {
                texturePath = VirtualTextureRegistry.register(
                        slot.key() + "_slot.png",
                        ()-> OutlineTextureGenerator.generateImage(slot.baseItem())
                );
            } else {
                texturePath = "/assets/coolbelt/textures/slot/tools.png";
                texX = 16 * slot.v();
            }

            AccessoryRegister.add(slot.key(), texturePath, texX, texY, slot.h(), slot.v());
            LOGGER.info("Slot '%s' registered with texture path: %s", slot.key(), texturePath);
        }
    }
}