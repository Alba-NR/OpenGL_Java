package graphics.core.io;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageLoader {

    private static boolean flipVertically = true;

    /**
     * Loads the specified image and returns its image data:
     *      - BufferedImage & ByteBuffer representations of the image data
     *      - width & height of the image
     * @param filename {@link String} filename/filepath of the image file to load
     * @param isRGBA true if should interpret image data as RGBA instead of RGB
     * @return {@link ImageData} object containin the image's data.
     */
    public static ImageData loadImage(String filename, boolean isRGBA){
        // load image from file
        BufferedImage img;
        try {
            img = ImageIO.read(new File(filename));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load texture image file: " + filename);
        }

        // get texture width & height
        int width = img.getWidth();
        int height = img.getHeight();

        if(flipVertically) {
            // flip image vertically (OpenGL 0.0 texture y-coord at top-left corner; img has it at bottom-left)
            AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
            tx.translate(0, -img.getHeight());
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            img = op.filter(img, null);
        } // must not flip vertically for cubemap texture images

        // convert BufferedImage to ByteBuffer for OpenGL functions
        int[] pixels = new int[width*height];
        img.getRGB(0, 0, width, height, pixels, 0, width);

        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * (isRGBA ? 4 : 3)); //4 for RGBA, 3 for RGB

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int pixel = pixels[y * width + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8 ) & 0xFF));     // Green component
                buffer.put((byte) ((pixel >> 0 ) & 0xFF));     // Blue component
                if(isRGBA) buffer.put((byte) ((pixel >> 24) & 0xFF));     // Alpha component (if using RGBA)
            }
        }

        buffer.flip(); // important!

        // create ImageData obj to return the img's info
        ImageData imgData = new ImageData(img, buffer);

        return imgData;
    }

    public static void setFlipVertically(boolean flipVertically) {
        ImageLoader.flipVertically = flipVertically;
    }
}
