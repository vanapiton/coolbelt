package fi._1up.coolbelt.impl.mixin.client.option;

import fi._1up.coolbelt.config.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Shadow public KeyBinding[] allKeys;

    @Inject(method = "<init>(Lnet/minecraft/client/Minecraft;Ljava/io/File;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;load()V"))
    private void init(Minecraft minecraft, File file, CallbackInfo ci) {
        allKeys = ArrayUtils.addAll(allKeys, KeyBindings.binds);
    }
}
