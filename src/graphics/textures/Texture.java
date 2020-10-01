package graphics.textures;

import graphics.core.io.ImageData;
import graphics.core.io.ImageLoader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 *  Represents a texture loaded from an image file.
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
        ImageData imgData = ImageLoader.loadImage(filename, isRGBA);
        width = imgData.getWidth();
        height = imgData.getHeight();

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
        if(type == TextureType.DIFFUSE) // tex in sRGB space -- so sRGB values transformed to linear before any calcs
            glTexImage2D(GL_TEXTURE_2D, 0, (isRGBA ? GL_SRGB_ALPHA : GL_SRGB), width, height, 0, (isRGBA ? GL_RGBA : GL_RGB), GL_UNSIGNED_BYTE, imgData.getByteBuffer());
        else // tex in linear space
            glTexImage2D(GL_TEXTURE_2D, 0, (isRGBA ? GL_RGBA : GL_RGB), width, height, 0, (isRGBA ? GL_RGBA : GL_RGB), GL_UNSIGNED_BYTE, imgData.getByteBuffer());
        glGenerateMipmap(GL_TEXTURE_2D);    // generate mipmap

        // set the texture wrapping & filtering parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glBindTexture(GL_TEXTURE_2D, 0); // unbind texture
    }

    /**
     * Set the wrapping method to GL_REPEAT
     */
    public void setTexWrapToRepeat(){
        glBindTexture(GL_TEXTURE_2D, id);  // bind texture
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glBindTexture(GL_TEXTURE_2D, 0); // unbind texture
    }

    /**
     * Set the wrapping method to GL_CLAMP_TO_EDGE
     */
    public void setTexWrapToClampToEdge(){
        glBindTexture(GL_TEXTURE_2D, id);  // bind texture
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glBindTexture(GL_TEXTURE_2D, 0); // unbind texture
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
