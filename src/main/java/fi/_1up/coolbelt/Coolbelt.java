package fi._1up.coolbelt;

import com.periut.accessoryapi.api.AccessoryRegister;
import fi._1up.coolbelt.api.ToolSlot;
import net.fabricmc.api.ModInitializer;

public class Coolbelt implements ModInitializer {
    private static final String SLOT_TEX_PATH = "/assets/coolbelt/textures/slot/tools.png";

    @Override
    public void onInitialize() {
        for (ToolSlot slot : ToolSlot.SLOTS) {
            if(!slot.enabled()) continue;
            AccessoryRegister.add(slot.key(), SLOT_TEX_PATH, slot.texX(), slot.texY(), slot.h(), slot.v());
        }
    }
}