package fi._1up.coolbelt;

import com.periut.accessoryapi.api.AccessoryRegister;
import net.fabricmc.api.ModInitializer;
import java.util.List;

public class Coolbelt implements ModInitializer {
    private static final String SLOT_TEX_PATH = "/assets/coolbelt/textures/slot/tools.png";
    private static final int SLOT_TEX_SIZE = 16;

    private record ToolSlot(String key, int texX, int texY, int h, int v) {
        public int texX() { return texX * SLOT_TEX_SIZE; }
        public int texY() { return texY * SLOT_TEX_SIZE; }
    }

    private static final List<ToolSlot> SLOTS = List.of(
            new ToolSlot("sword",   0, 0, 0, 0),
            new ToolSlot("pickaxe", 1, 0, 0, 1),
            new ToolSlot("axe",     2, 0, 0, 2),
            new ToolSlot("shovel",  3, 0, 0, 3)
    );

    @Override
    public void onInitialize() {
        for (ToolSlot slot : SLOTS) {
            AccessoryRegister.add(slot.key(), SLOT_TEX_PATH, slot.texX(), slot.texY(), slot.h(), slot.v());
        }
    }
}