package fi._1up.coolbelt.impl.texture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static fi._1up.coolbelt.Coolbelt.LOGGER;

/// Registry managing virtual textures served from [VIRTUAL_ASSET_PATH].
public final class VirtualTextureRegistry {
    /// The path that virtual assets are served from.
    public static final String VIRTUAL_ASSET_PATH = "/assets/coolbelt/virtual/";
    private static final Map<String, VirtualTexture> REGISTRY = new ConcurrentHashMap<>();

    private VirtualTextureRegistry() {}

    /// Registers an arbitrary image generation factory function under a relative path.
    ///
    /// @param relativePath Relative path to register under.
    /// @param generator    Supplier function returning the generated [BufferedImage].
    /// @return The virtual path string.
    public static String register(@NotNull String relativePath, @NotNull Supplier<BufferedImage> generator) {
        return register(relativePath, generator, relativePath);
    }

    /// Registers an arbitrary image generation factory function under a relative path.
    ///
    /// @param relativePath Relative path to register under.
    /// @param generator    Supplier function returning the generated [BufferedImage].
    /// @param fallbackPath Path to use in case the supplier returns null.
    /// @return The virtual path string.
    public static String register(@NotNull String relativePath, @NotNull Supplier<BufferedImage> generator, String fallbackPath) {
        String normalizedPath = relativePath.replaceFirst("^/", "");
        String texturePath = VIRTUAL_ASSET_PATH + normalizedPath;
        REGISTRY.put(texturePath, new VirtualTexture(generator, fallbackPath));
        return texturePath;
    }

    /// Generates the image associated with a registered virtual path by executing its function.
    ///
    /// @param virtualPath The registered virtual path.
    /// @return The generated [BufferedImage], or null if generation fails or path is unregistered.
    @Nullable
    public static BufferedImage generateImage(@NotNull String virtualPath) {
        VirtualTexture virtualTexture = REGISTRY.get(virtualPath);
        if (virtualTexture == null) return null;

        BufferedImage image = virtualTexture.generator.get();
        String fallbackPath = virtualTexture.fallbackPath;

        if (image == null && fallbackPath != null && !fallbackPath.isEmpty()) {
            try (InputStream stream = VirtualTextureRegistry.class.getResourceAsStream(fallbackPath)) {
                if (stream != null) {
                    image = ImageIO.read(stream);
                }
            } catch (IOException e) {
                LOGGER.error("Failed to read fallback image at: %s", fallbackPath);
            }
        }

        return image;
    }

    private record VirtualTexture(@NotNull Supplier<BufferedImage> generator, String fallbackPath) {}
}
