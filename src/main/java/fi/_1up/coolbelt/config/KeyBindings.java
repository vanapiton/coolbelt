package fi._1up.coolbelt.config;

import net.minecraft.client.option.KeyBinding;
import org.lwjgl.input.Keyboard;

public abstract class KeyBindings {
    public static final KeyBinding IGNORE = new KeyBinding("key.coolbelt.ignore", Keyboard.KEY_0);

    public static final KeyBinding[] binds = new KeyBinding[]{
            IGNORE
    };
}