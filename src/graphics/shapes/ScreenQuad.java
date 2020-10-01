package graphics.shapes;

import graphics.shapes.meshes.ScreenQuadMesh;
import graphics.textures.Texture;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.*;

/**
 * Represents a quadrilateral/square that fills in the entire screen.
 * Used when rendering to a texture, to see the texture.
 */
public class ScreenQuad {
    private final ScreenQuadMesh mesh;
    private int texHandle;

    public ScreenQuad(Texture texture){
        mesh = ScreenQuadMesh.getInstance();
        texHandle = texture.getHandle();
    }

    public ScreenQuad(int texHandle){
        mesh = ScreenQuadMesh.getInstance();
        this.texHandle = texHandle;
    }

    /**
     * Bind the texture to tex unit 0
     */
    public void bindTexture(){
        glActiveTexture(GL_TEXTURE0);                // activate proper texture unit before binding
        glBindTexture(GL_TEXTURE_2D, texHandle);     // bind texture to appropriate texture unit
    }

    public ScreenQuadMesh getMesh() {
        return mesh;
    }

    public int getTextureHandle() {
        return texHandle;
    }
}
