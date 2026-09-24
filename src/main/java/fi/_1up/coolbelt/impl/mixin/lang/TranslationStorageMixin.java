package fi._1up.coolbelt.impl.mixin.lang;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.language.TranslationStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.Properties;

import static fi._1up.coolbelt.Coolbelt.LOGGER;

@Mixin(TranslationStorage.class)
public class TranslationStorageMixin {
    @Shadow private Properties translations;
    @Unique private static final String TRANSLATIONS_PATH = "/assets/coolbelt/stationapi/lang/en_US.lang";

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Properties;load(Ljava/io/InputStream;)V", ordinal = 1, shift = At.Shift.AFTER))
    public void init(CallbackInfo ci) {
        if (!FabricLoader.getInstance().isModLoaded("stationapi")) {
            try {
                translations.load(TranslationStorage.class.getResourceAsStream(TRANSLATIONS_PATH));
            } catch (IOException err) {
                LOGGER.error("Failed to load translations: %s", err.getMessage());
            }
        }
    }
}
