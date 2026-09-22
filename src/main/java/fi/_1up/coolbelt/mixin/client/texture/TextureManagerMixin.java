package fi._1up.coolbelt.mixin.client.texture;

import fi._1up.coolbelt.api.VirtualTextureRegistry;
import net.minecraft.client.texture.TextureManager;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static fi._1up.coolbelt.api.VirtualTextureRegistry.VIRTUAL_ASSET_PATH;

@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {
    @SuppressWarnings("rawtypes")
    @Shadow private HashMap textures;

    @Shadow public abstract void load(BufferedImage image, int id);

    @SuppressWarnings("unchecked")
    @Inject(method = "getTextureId", at = @At("HEAD"), cancellable = true)
    private void getTextureId(String path, CallbackInfoReturnable<Integer> cir) {
        if (path == null) return;

        String normalizedPath = path.startsWith("/") ? path : "/" + path;

        if (normalizedPath.startsWith(VIRTUAL_ASSET_PATH)) {

            if (this.textures.containsKey(path)) {
                cir.setReturnValue((Integer) this.textures.get(path));
                return;
            }

            BufferedImage generatedImage = VirtualTextureRegistry.generateImage(normalizedPath);

            if (generatedImage != null) {
                int glTextureId = GL11.glGenTextures();

                this.load(generatedImage, glTextureId);

                this.textures.put(path, glTextureId);
                this.textures.put(normalizedPath, glTextureId);

                cir.setReturnValue(glTextureId);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @Inject(method = "reload", at = @At("HEAD"))
    private void reload(CallbackInfo ci) {
        if (this.textures == null) return;

        Iterator iterator = this.textures.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();

            if (key != null && key.startsWith(VirtualTextureRegistry.VIRTUAL_ASSET_PATH)) {
                Integer glTextureId = (Integer) entry.getValue();
                if (glTextureId != null && glTextureId > 0) {
                    GL11.glDeleteTextures(glTextureId);
                }
                iterator.remove();
            }
        }
    }
}