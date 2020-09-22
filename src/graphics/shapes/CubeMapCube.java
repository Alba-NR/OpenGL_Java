package graphics.shapes;

import graphics.shapes.meshes.CubeMapCubeMesh;
import graphics.textures.CubeMapTexture;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.*;

/**
 * Represents a cube used for a cubemap.
 */
public class CubeMapCube {
    private final CubeMapCubeMesh mesh;
    private CubeMapTexture cubeMapTexture;

    public CubeMapCube(CubeMapTexture cubeMapTexture){
        mesh = CubeMapCubeMesh.getInstance();
        this.cubeMapTexture = cubeMapTexture;
    }

    /**
     * Bind the cube's cubemap texture to the appropriate texture units.
     */
    public void bindTexture(){
        glActiveTexture(GL_TEXTURE0);                                       // activate proper texture unit before binding
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMapTexture.getHandle());     // bind texture to appropriate texture unit
    }

    public CubeMapCubeMesh getMesh() {
        return mesh;
    }

    public CubeMapTexture getCubeMapTexture() {
        return cubeMapTexture;
    }
}
