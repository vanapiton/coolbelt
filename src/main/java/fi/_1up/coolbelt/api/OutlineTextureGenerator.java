package fi._1up.coolbelt.api;

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

    private static final int OUTLINE_ARGB = 0xFF5F5F5F;
    private static final double DARKNESS_THRESHOLD = 100;

    private static final int[][] NEIGHBORS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    private OutlineTextureGenerator() {}

    /// Generates an outline image for a specific item.
    /// @param item Item instance to generate the outline for.
    /// @return The generated [BufferedImage], or null if generation fails.
    public static BufferedImage generateImage(@NotNull Item item) {
        int textureId = item.getTextureId(0);

        int atlasX = textureId % ATLAS_GRID_SIZE;
        int atlasY = textureId / ATLAS_GRID_SIZE;
        return generateImage(atlasX, atlasY);
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

        int tileSize = itemAtlas.getWidth() / ATLAS_GRID_SIZE;

        int startX = atlasX * tileSize;
        int startY = atlasY * tileSize;

        int[] pixels = new int[tileSize * tileSize];
        itemAtlas.getRGB(startX, startY, tileSize, tileSize, pixels, 0, tileSize);

        BufferedImage outline = new BufferedImage(itemAtlas.getWidth(), itemAtlas.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < tileSize; y++) {
            for (int x = 0; x < tileSize; x++) {
                int argb = pixels[y * tileSize + x];

                if (isTransparent(argb)) continue;

                if (isOutlinePixel(pixels, x, y, argb, tileSize)) {
                    outline.setRGB(x, y, OUTLINE_ARGB);
                }
            }
        }

        return outline;
    }

    private static boolean isOutlinePixel(int[] pixels, int x, int y, int currentArgb, int tileSize) {
        double currentLuminance = calculateLuminance(currentArgb);

        for (int[] neighbor : NEIGHBORS) {
            int nx = x + neighbor[0];
            int ny = y + neighbor[1];

            if (nx < 0 || nx >= tileSize || ny < 0 || ny >= tileSize) return true;

            int neighborArgb = pixels[ny * tileSize + nx];
            if (isTransparent(neighborArgb)) return true;

            double neighborLuminance = calculateLuminance(neighborArgb);
            if ((neighborLuminance - currentLuminance) >= DARKNESS_THRESHOLD) return true;
        }
        return false;
    }

    private static boolean isTransparent(int argb) {
        return ((argb >> 24) & 0xFF) <= 0;
    }

    private static double calculateLuminance(int argb) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return (0.2126 * r) + (0.7152 * g) + (0.0722 * b);
    }
}
