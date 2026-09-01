package fi._1up.coolbelt;

import com.periut.accessoryapi.api.AccessoryRegister;
import net.fabricmc.api.ModInitializer;
import java.util.List;

import static fi._1up.coolbelt.config.CoolbeltConfig.config;

public class Coolbelt implements ModInitializer {
    private static final String SLOT_TEX_PATH = "/assets/coolbelt/textures/slot/tools.png";
    private static final int SLOT_TEX_SIZE = 16;

    @SuppressWarnings("SameParameterValue")
    private record ToolSlot(String key, int texX, int texY, int h, int v, boolean enabled) {
        public int texX() { return texX * SLOT_TEX_SIZE; }
        public int texY() { return texY * SLOT_TEX_SIZE; }
    }

    private static final List<ToolSlot> SLOTS = List.of(
            new ToolSlot("sword",   0, 0, 0, 0, config.isSlotEnabled.sword),
            new ToolSlot("pickaxe", 1, 0, 0, 1, config.isSlotEnabled.pickaxe),
            new ToolSlot("axe",     2, 0, 0, 2, config.isSlotEnabled.axe),
            new ToolSlot("shovel",  3, 0, 0, 3, config.isSlotEnabled.shovel)
    );

    @Override
    public void onInitialize() {
        for (ToolSlot slot : SLOTS) {
            if(!slot.enabled) continue;
            AccessoryRegister.add(slot.key(), SLOT_TEX_PATH, slot.texX(), slot.texY(), slot.h(), slot.v());
        }
    }
}