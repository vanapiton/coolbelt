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
    private static final int TILE_SIZE = 16;
    private static final int TEXTURE_SIZE = TILE_SIZE * 16;

    private static final int OUTLINE_ARGB = 0xFF5F5F5F;
    private static final double DARKNESS_THRESHOLD = 100.0;

    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    private OutlineTextureGenerator() {}

    /// Generates an outline image for a specific item.
    /// @param item Item instance to generate the outline for.
    /// @return The generated [BufferedImage], or null if generation fails.
    public static BufferedImage generateImage(@NotNull Item item) {
        int textureId = item.getTextureId(0);
        int atlasX = textureId % TILE_SIZE;
        int atlasY = textureId / TILE_SIZE;
        return generateImage(atlasX, atlasY);
    }

    /// Generates an outline image for a specific tile coordinate within the item atlas.
    /// @param atlasX Atlas column index.
    /// @param atlasY Atlas row index.
    /// @return The generated [BufferedImage], or null if generation fails.
    @Nullable
    public static BufferedImage generateImage(int atlasX, int atlasY) {
        LOGGER.info("Trying to generate outline from (%d, %d).", atlasX, atlasY);

        @SuppressWarnings("deprecation")
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();

        if (mc.texturePacks == null || mc.texturePacks.selected == null) {
            LOGGER.error("Failed to get texture pack.");
            return null;
        }

        try (InputStream stream = mc.texturePacks.selected.getResource("/gui/items.png")) {
            if(stream == null) {
                LOGGER.error("Failed to get texture pack /gui/items.png input stream.");
                return null;
            }

            BufferedImage itemAtlas = ImageIO.read(stream);
            if(itemAtlas == null) {
                LOGGER.error("Failed to get item atlas.");
                return null;
            }

            int startX = atlasX * TILE_SIZE;
            int startY = atlasY * TILE_SIZE;

            int[] pixels = new int[TILE_SIZE * TILE_SIZE];
            itemAtlas.getRGB(startX, startY, TILE_SIZE, TILE_SIZE, pixels, 0, TILE_SIZE);

            BufferedImage outline = new BufferedImage(TEXTURE_SIZE, TEXTURE_SIZE, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < TILE_SIZE; y++) {
                for (int x = 0; x < TILE_SIZE; x++) {
                    int argb = pixels[y * TILE_SIZE + x];

                    if (isTransparent(argb)) continue;

                    if (isOutlinePixel(pixels, x, y, argb)) {
                        outline.setRGB(x, y, OUTLINE_ARGB);
                    }
                }
            }

            return outline;
        } catch (IllegalArgumentException err) {
            LOGGER.error("ImageIO received null input during texture pack reload: %s", err.getMessage());
        } catch (Exception err) {
            LOGGER.error("Failed to generate outline from (%s, %s): %s", atlasX, atlasY, err.getMessage());
        }
        return null;
    }

    private static boolean isOutlinePixel(int[] pixels, int x, int y, int currentArgb) {
        double currentLuminance = calculateLuminance(currentArgb);

        for (int[] dir : DIRECTIONS) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            if (nx < 0 || nx >= TILE_SIZE || ny < 0 || ny >= TILE_SIZE) return true;

            int neighborArgb = pixels[ny * TILE_SIZE + nx];
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
