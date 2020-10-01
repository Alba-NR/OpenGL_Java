package graphics.shapes.meshes;

import static org.lwjgl.opengl.GL30.*;

/**
 * Represents a mesh that is created from a model loaded by {@link graphics.core.io.ModelLoader}.
 * Set to public only so that {@link graphics.core.io.ModelLoader} can create instances.
 * Not recommended to manually create instances of this class.
 */
public class MeshFromOBJ extends Mesh {
    private float[] vPositions;
    private int[] vIndeces;
    private float[] vNormals;
    private float[] texCoords;

    public MeshFromOBJ(float[] vPositions, int[] vIndeces, float[] vNormals, float[] texCoords, boolean useFaceCulling){
        super(GL_CCW, useFaceCulling);
        this.vPositions = vPositions;
        this.vIndeces = vIndeces;
        this.vNormals = vNormals;
        this.texCoords = texCoords;
        initialize();
    }

    @Override
    float[] initializeVertexPositions() {
        return vPositions;
    }

    @Override
    int[] initializeVertexIndices() {
        return vIndeces;
    }

    @Override
    float[] initializeVertexNormals() {
        return vNormals;
    }

    @Override
    float[] initializeTextureCoordinates() {
        return texCoords;
    }
}
