package graphics.textures;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 *  Represents a texture.
 */
public class Texture {
    private String filename;
    private int width;
    private int height;
    private int id;
    private TextureType type;

    public Texture(String filename, boolean isRGBA, TextureType type){
        this.filename = filename;
        this.type = type;
        loadTexture(isRGBA);
    }

    /**
     * Load texture image from specified file & create OpenGL texture object
     */
    private void loadTexture(boolean isRGBA){

        // load image from file
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(filename));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load texture image file: " + filename);
        }

        // get texture width & height
        width = img.getWidth();
        height = img.getHeight();

        // flip image vertically (OpenGL 0.0 texture y-coord at top-left corner; img has it at bottom-left)
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -img.getHeight());
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        img = op.filter(img, null);

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

        // create OpenGL texture obj (get it's id)
        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);  // bind texture

        // generate texture
        /*
         *  generate texture using glTexImage2D. Args:
         *      - texture target: GL_TEXTURE_2D => will generate texture on the currently bound texture
         *                                         obj at the same target (target is GL_TEXTURE_2D)
         *      - mipmap level (0 is base level)
         *      - format to store the texture in
         *      - width & height of the resulting texture
         *      - always 0
         *      - format and datatype of the source image
         *      - actual image data
         */
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, (isRGBA ? GL_RGBA : GL_RGB), GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);    // generate mipmap
    }

    public int getHandle(){
        return id;
    }

    public TextureType getType() {
        return type;
    }

    public void setType(TextureType type) {
        this.type = type;
    }
}
