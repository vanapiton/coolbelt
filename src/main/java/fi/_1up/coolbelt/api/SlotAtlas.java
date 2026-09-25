package fi._1up.coolbelt.api;

import fi._1up.coolbelt.impl.TexturePackHelper;
import fi._1up.coolbelt.impl.SauvolaThreshold;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static fi._1up.coolbelt.Coolbelt.LOGGER;

/// Generates slot placeholder outline textures from the item atlas. Supports texture packs.
@ApiStatus.Experimental
public final class SlotAtlas {
    private static final int ATLAS_GRID_SIZE = 16;
    private static final int BASE_TILE_SIZE = 16;

    private static final double ALPHA_THRESHOLD = 128;

    private static final double SAUVOLA_R = 128;

    private SlotAtlas() {}

    /// Get the coordinates of the item on the slot atlas.
    /// @return An int[] containing the x and y coordinates.
    public static int[] getAtlasCoordinates(@NotNull Item item) {
        int textureId = item.getTextureId(0);

        int x = textureId % ATLAS_GRID_SIZE * BASE_TILE_SIZE;
        int y = textureId / ATLAS_GRID_SIZE * BASE_TILE_SIZE;

        return new int[] {x, y};
    }

    /// Generates the slot atlas.
    /// @return The generated [BufferedImage], or null if generation fails.
    @Nullable
    public static BufferedImage generate() {
        BufferedImage slotAtlas = generate(0x5F5F5F, 0.4);

        if(slotAtlas != null && FabricLoader.getInstance().isDevelopmentEnvironment()) {
            saveToRunDirectory(slotAtlas);
        }

        return slotAtlas;
    }

    /// Generates the slot atlas.
    /// @param rgb The outline color
    /// @param k The Sauvola thresholding K-value.
    /// @return The generated [BufferedImage], or null if generation fails.
    @Nullable
    public static BufferedImage generate(int rgb, double k) {
        LOGGER.info("Trying to generate slot atlas.");

        BufferedImage outlineAtlas = TexturePackHelper.getImage("/gui/items.png");
        if(outlineAtlas == null) return null;

        int atlasWidth = outlineAtlas.getWidth();
        int atlasHeight = outlineAtlas.getHeight();

        int tileSize = atlasWidth / ATLAS_GRID_SIZE;
        int windowRadius = Math.max(1, tileSize / BASE_TILE_SIZE);

        SauvolaThreshold.mutate(outlineAtlas, windowRadius, k, SAUVOLA_R);

        int[] pixels = outlineAtlas.getRGB(0, 0, atlasWidth, atlasHeight, null, 0, atlasWidth);

        for (int y = 0; y < atlasHeight; y++) {
            for (int x = 0; x < atlasWidth; x++) {
                int argb = pixels[y * atlasWidth + x];

                if (isTransparent(argb)) continue;

                boolean isThresholded = (argb & 0x00FFFFFF) == 0;

                if (isThresholded || isEdge(pixels, x, y, atlasWidth)) {
                    outlineAtlas.setRGB(x, y, argb & 0xFF000000 | rgb);
                }
                else {
                    outlineAtlas.setRGB(x, y, 0);
                }
            }
        }

        return outlineAtlas;
    }

    private static boolean isEdge(int[] pixels, int x, int y, int width) {
        if (x == 0 || x == width - 1 || y == 0 || y == width - 1) return true;

        return isTransparent(pixels[y * width + (x - 1)])
                || isTransparent(pixels[y * width + (x + 1)])
                || isTransparent(pixels[(y - 1) * width + x])
                || isTransparent(pixels[(y + 1) * width + x]);
    }

    private static boolean isTransparent(int argb) {
        return ((argb >> 24) & 0xFF) < ALPHA_THRESHOLD;
    }

    private static void saveToRunDirectory(BufferedImage image) {
        if (image == null) {
            LOGGER.error("Failed to save slot atlas since it is null!");
            return;
        }

        File runDir = Minecraft.getRunDirectory();
        File outputFile = new File(runDir, "slots.png");

        try {
            boolean written = ImageIO.write(image, "png", outputFile);
            if (written) {
                LOGGER.info("Slot atlas successfully saved to: %s", outputFile.getAbsolutePath());
            } else {
                LOGGER.error("Failed to save slot atlas: No writer found for PNG format!");
            }
        } catch (IOException err) {
            LOGGER.error("Failed to save slot atlas: %s", err.getMessage());
        }
    }
}
