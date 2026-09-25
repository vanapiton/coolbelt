package fi._1up.coolbelt.impl;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.pack.TexturePack;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import static fi._1up.coolbelt.Coolbelt.LOGGER;

public class TexturePackHelper {

    @Nullable
    public static TexturePack getSelected() {
        @SuppressWarnings("deprecation")
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();

        if (mc.texturePacks == null || mc.texturePacks.selected == null) {
            LOGGER.error("Failed to get texture pack.");
            return null;
        }

        return mc.texturePacks.selected;
    }

    @Nullable
    public static BufferedImage getImage(String path) {
        TexturePack texturePack = getSelected();
        if(texturePack == null) return null;
        BufferedImage image = null;

        try (InputStream stream = texturePack.getResource(path)) {
            if (stream == null) {
                LOGGER.error("Failed to get texture pack resource stream.");
                return null;
            }

            image = ImageIO.read(stream);
            if (image == null) {
                LOGGER.error("Failed to read image from texture pack.");
                return null;
            }
        } catch (Exception err) {
            LOGGER.error("Failed to get image from texture pack: %s", err.getMessage());
        }

        return image;
    }
}
