package graphics.shapes.meshes;

import static org.lwjgl.opengl.GL30.*;

/**
 * Defines a square mesh. Edges are unit length.
 */
public class SquareMesh extends Mesh {

    private static SquareMesh instance = null;

    private SquareMesh() {
        super(GL_CW, false);
        initialize();
    }

    public static SquareMesh getInstance(){
        if(instance == null){
            instance = new SquareMesh();
        }
        return instance;
    }

    @Override
    float[] initializeVertexPositions() {
        return new float[]{
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.5f,  0.5f, 0.0f,
                0.5f,  0.5f, 0.0f,
                -0.5f,  0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f
        };
    }

    @Override
    int[] initializeVertexIndices() {
        return new int[] {
                0,  1,  2,  3,  4,  5
        };
    }

    @Override
    float[] initializeVertexNormals() {
        return new float[]{
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f
        };
    }

    @Override
    float[] initializeTextureCoordinates() {
        return new float[]{
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 0.0f
        };
    }
}
