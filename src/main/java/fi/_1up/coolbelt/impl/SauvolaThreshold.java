package fi._1up.coolbelt.impl;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

/// Implements the Sauvola algorithm for image binarization.
public final class SauvolaThreshold {

    private SauvolaThreshold() {}

    /// Modifies a BufferedImage in-place based on Sauvola thresholding on sRGB luma, preserves and ignores alpha.
    /// @param image Target BufferedImage to modify in-place.
    /// @param radius Radius of the local neighborhood window.
    /// @param k Sauvola standard deviation weight.
    /// @param r Dynamic range of standard deviation.
    @Contract(mutates = "param1")
    public static void mutate(@NotNull BufferedImage image, int radius, double k, double r) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] src = image.getRGB(0, 0, width, height, null, 0, width);
        int[] dst = new int[src.length];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = y * width + x;
                boolean isEdge = x == 0 || x == width - 1 || y == 0 || y == height - 1;
                boolean underThreshold = sample(src, x, y, width, height, radius, k, r);

                int argb = src[i];
                argb |= 0x00FFFFFF;
                if(isEdge || underThreshold) argb &= 0xFF000000;

                dst[i] = argb;
            }
        }

        image.setRGB(0, 0, width, height, dst, 0, width);
    }

    private static boolean sample(int[] pixels, int x, int y, int width, int height, int radius, double k, double r) {
        double sum = 0;
        double sumSq = 0;
        int count = 0;

        int minY = Math.max(0, y - radius);
        int maxY = Math.min(height - 1, y + radius);
        int minX = Math.max(0, x - radius);
        int maxX = Math.min(width - 1, x + radius);

        for (int wy = minY; wy <= maxY; wy++) {
            for (int wx = minX; wx <= maxX; wx++) {
                int argb = pixels[wy * width + wx];
                double luma = getLuma(argb);

                sum += luma;
                sumSq += luma * luma;
                count++;
            }
        }

        if (count == 0) return false;

        double mean = sum / count;
        double variance = (sumSq / count) - (mean * mean);
        double deviation = Math.sqrt(Math.max(0, variance));

        double threshold = mean * (1 + k * ((deviation / r) - 1));
        int currentPixel = pixels[y * width + x];
        return getLuma(currentPixel) < threshold;
    }

    private static double getLuma(int argb) {
        int red = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int blue = argb & 0xFF;
        return (0.2126 * red) + (0.7152 * green) + (0.0722 * blue);
    }
}