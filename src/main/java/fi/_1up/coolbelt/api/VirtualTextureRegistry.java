package fi._1up.coolbelt.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/// Registry managing virtual textures served from [VIRTUAL_ASSET_PATH].
@ApiStatus.Experimental
public final class VirtualTextureRegistry {
    /// The path that virtual assets are served from.
    public static final String VIRTUAL_ASSET_PATH = "/assets/coolbelt/virtual/";

    private static final Map<String, Supplier<BufferedImage>> REGISTRY = new ConcurrentHashMap<>();

    private VirtualTextureRegistry() {}

    /// Registers an arbitrary image generation factory function under a slot key.
    /// @param fileName Virtual file name to register under.
    /// @param factory Supplier function returning the generated [BufferedImage].
    /// @return The virtual path string.
    public static String register(@NotNull String fileName, @NotNull Supplier<BufferedImage> factory) {
        String resourcePath = VIRTUAL_ASSET_PATH + fileName;
        REGISTRY.put(resourcePath, factory);
        return resourcePath;
    }

    /// Generates the image associated with a registered virtual path by executing its function.
    /// @param virtualPath The registered virtual path.
    /// @return The generated [BufferedImage], or null if generation fails or path is unregistered.
    @Nullable
    public static BufferedImage generateImage(@NotNull String virtualPath) {
        Supplier<BufferedImage> factory = REGISTRY.get(virtualPath);
        if (factory == null) return null;

        return factory.get();
    }
}
