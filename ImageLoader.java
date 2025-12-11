package mygame;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader {
    private static final Map<String, BufferedImage> imageCache = new HashMap<>();
    private static final String RESOURCES_PATH = "resources/";

    /**
     * Load an image from the resources folder and cache it
     */
    public static BufferedImage loadImage(String filename) {
        // Check if already cached
        if (imageCache.containsKey(filename)) {
            return imageCache.get(filename);
        }

        try {
            File imageFile = new File(RESOURCES_PATH + filename);
            BufferedImage image = ImageIO.read(imageFile);
            imageCache.put(filename, image);
            return image;
        } catch (Exception e) {
            System.err.println("Failed to load image: " + filename);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Clear the image cache (useful when restarting game)
     */
    public static void clearCache() {
        imageCache.clear();
    }
}
