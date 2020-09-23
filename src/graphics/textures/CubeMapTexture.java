package graphics.textures;

import graphics.core.io.ImageData;
import graphics.core.io.ImageLoader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

/**
 *  Represents a cubemap.
 */
public class CubeMapTexture {
    private String[] filenames;
    private int id;

    /**
     * @param filenames must be in the following order:
     *                  right, left, top, bottom, back, front.
     */
    public CubeMapTexture(String[] filenames){
        this.filenames = filenames;
        loadTextures();
    }

    /**
     * Load cubemap texture from images for all 6 files & create OpenGL texture object
     */
    private void loadTextures(){
        // create OpenGL texture obj (get it's id)
        id = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);  // bind texture

        ImageLoader.setFlipVertically(false);    // must not flip imgs vertically

        for(int n = 0; n < filenames.length; n++) {
            // load image from file
            ImageData imageData = ImageLoader.loadImage(filenames[n], false);

            // generate texture
            /*
             *  generate texture using glTexImage2D.
             *  1st arg: texture target -- GL_TEXTURE_CUBE_MAP_POSITIVE_X + n
             *          => will generate texture on the currently bound texture obj at the same target
             *          (target is GL_TEXTURE_CUBE_MAP_POSITIVE_X + n -- each int value of the enum  + n represents a dif cube face)
             */
            glTexImage2D(
                    GL_TEXTURE_CUBE_MAP_POSITIVE_X + n,
                    0, GL_RGB, imageData.getWidth(), imageData.getHeight(),
                    0, GL_RGB, GL_UNSIGNED_BYTE, imageData.getByteBuffer()
            );
        }

        ImageLoader.setFlipVertically(true);   // set back to default

        // set the texture wrapping & filtering parameters
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        glBindTexture(GL_TEXTURE_CUBE_MAP, 0); // unbind texture
    }

    public int getHandle(){
        return id;
    }
}
