package games.enchanted.eg_bedrock_books.common.util;

public class ColourUtil {
    private static int clampInt(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    /**
     * Converts rgb to an int in rgb decimal format
     */
    public static int RGB_to_RGBint(int r, int g, int b) {
        int red = clampInt(r, 0, 255);
        int green = clampInt(g, 0, 255);
        int blue = clampInt(b, 0, 255);
        return (red << 16) | (green << 8) | blue;
    }

    /**
     * Converts an int in rgb decimal format to an array of r, g, b
     */
    public static int[] RGBint_to_RGB(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return new int[]{r, g, b};
    }

    private static final int WHITE_THRESHOLD = 14;

    public static int makeRGBHighContrastAgainstBlack(int rgb) {
        int[] rgbSplit = RGBint_to_RGB(rgb);
        if(rgbSplit[0] < WHITE_THRESHOLD && rgbSplit[1] < WHITE_THRESHOLD && rgbSplit[2] < WHITE_THRESHOLD) {
            return RGB_to_RGBint(255, 255, 255);
        }
        for (int i = 0; i < rgbSplit.length; i++) {
            rgbSplit[i] = (int) ((((rgbSplit[i] / 255d) / 3) + 0.6665) * 255);
        }

        return RGB_to_RGBint(rgbSplit[0], rgbSplit[1], rgbSplit[2]);
    }
}
