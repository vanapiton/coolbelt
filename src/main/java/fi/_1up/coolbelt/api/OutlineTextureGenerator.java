package fi._1up.coolbelt.api;

import fi._1up.coolbelt.impl.SauvolaThreshold;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import static fi._1up.coolbelt.Coolbelt.LOGGER;

/// Generates outline textures from items in the item atlas. Supports 16x16 texture packs.
@ApiStatus.Experimental
public class OutlineTextureGenerator {
    private static final int ATLAS_GRID_SIZE = 16;
    private static final int BASE_TILE_SIZE = 16;

    private static final int OUTLINE_RGB = 0x5F5F5F;
    private static final double ALPHA_THRESHOLD = 128;

    private static final double SAUVOLA_K = 0.4;
    private static final double SAUVOLA_R = 128;

    private OutlineTextureGenerator() {}

    /// Generates an outline image for a specific item.
    /// @param item Item instance to generate the outline for.
    /// @return The generated [BufferedImage], or null if generation fails.
    public static BufferedImage generateImage(@NotNull Item item) {
        int textureId = item.getTextureId(0);
        return generateImage(textureId % ATLAS_GRID_SIZE, textureId / ATLAS_GRID_SIZE);
    }

    @Nullable
    private static BufferedImage getItemAtlas() {
        @SuppressWarnings("deprecation")
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();

        if (mc.texturePacks == null || mc.texturePacks.selected == null) {
            LOGGER.error("Failed to get texture pack.");
            return null;
        }

        BufferedImage itemAtlas = null;

        try (InputStream stream = mc.texturePacks.selected.getResource("/gui/items.png")) {
            if (stream == null) {
                LOGGER.error("Failed to get texture pack /gui/items.png input stream.");
                return null;
            }

            itemAtlas = ImageIO.read(stream);
            if (itemAtlas == null) {
                LOGGER.error("Failed to get item atlas.");
                return null;
            }
        } catch (Exception err) {
            LOGGER.error("Failed to get item atlas: %s", err.getMessage());
        }

        return itemAtlas;
    }

    /// Generates an outline image for a specific tile coordinate within the item atlas.
    /// @param atlasX Atlas column index.
    /// @param atlasY Atlas row index.
    /// @return The generated [BufferedImage], or null if generation fails.
    @Nullable
    public static BufferedImage generateImage(int atlasX, int atlasY) {
        LOGGER.info("Trying to generate outline from (%d, %d).", atlasX, atlasY);

        BufferedImage itemAtlas = getItemAtlas();
        if(itemAtlas == null) return null;

        int atlasWidth = itemAtlas.getWidth();
        int atlasHeight = itemAtlas.getHeight();

        int tileSize = atlasWidth / ATLAS_GRID_SIZE;
        int windowRadius = Math.max(1, tileSize / BASE_TILE_SIZE);

        BufferedImage outline = itemAtlas.getSubimage(atlasX * tileSize, atlasY * tileSize, tileSize, tileSize);
        SauvolaThreshold.mutate(outline, windowRadius, SAUVOLA_K, SAUVOLA_R);

        int[] pixels = outline.getRGB(0, 0, tileSize, tileSize, null, 0, tileSize);

        for (int y = 0; y < tileSize; y++) {
            for (int x = 0; x < tileSize; x++) {
                int argb = pixels[y * tileSize + x];

                if (isTransparent(argb)) continue;

                boolean isThresholded = (argb & 0x00FFFFFF) == 0;

                if (isThresholded || isEdge(pixels, x, y, tileSize)) {
                    outline.setRGB(x, y, argb & 0xFF000000 | OUTLINE_RGB);
                }
                else {
                    outline.setRGB(x, y, 0);
                }
            }
        }

        BufferedImage outputAtlas = new BufferedImage(atlasWidth, atlasHeight, BufferedImage.TYPE_INT_ARGB);
        int[] processedTilePixels = outline.getRGB(0, 0, tileSize, tileSize, null, 0, tileSize);
        outputAtlas.setRGB(0, 0, tileSize, tileSize, processedTilePixels, 0, tileSize);

        return outputAtlas;
    }

    private static boolean isEdge(int[] pixels, int x, int y, int tileSize) {
        if (x == 0 || x == tileSize - 1 || y == 0 || y == tileSize - 1) return true;

        return isTransparent(pixels[y * tileSize + (x - 1)])
                || isTransparent(pixels[y * tileSize + (x + 1)])
                || isTransparent(pixels[(y - 1) * tileSize + x])
                || isTransparent(pixels[(y + 1) * tileSize + x]);
    }

    private static boolean isTransparent(int argb) {
        return ((argb >> 24) & 0xFF) < ALPHA_THRESHOLD;
    }
}
