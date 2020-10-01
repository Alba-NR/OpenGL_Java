package graphics.shapes.meshes;

import static org.lwjgl.opengl.GL15.*;

/**
 * Defines a cubic mesh used for a cubemap. Only contains vertex data.
 */
public class CubeMapCubeMesh extends Mesh{

    private static CubeMapCubeMesh instance = null;

    private CubeMapCubeMesh() {
        super(GL_CCW, true);
        initialize();
    }

    public static CubeMapCubeMesh getInstance(){
        if(instance == null){
            instance = new CubeMapCubeMesh();
        }
        return instance;
    }

    /*
    @Override
    float[] initializeVertexPositions() {
        return new float[]{
                // vertices             index
                -0.5f, -0.5f, -0.5f,    // 0
                0.5f, -0.5f, -0.5f,     // 1
                0.5f, 0.5f, -0.5f,      // 2
                -0.5f, 0.5f, -0.5f,     // 3
                -0.5f, -0.5f, 0.5f,     // 4
                0.5f, 0.5f, 0.5f,       // 5
                0.5f, -0.5f, 0.5f,      // 6
                -0.5f, 0.5f, 0.5f       // 7
        };
    }

    @Override
    int[] initializeVertexIndices() {
        return new int[]{
                0, 1, 2, 2, 3, 0,   // back face
                4, 5, 6, 5, 4, 7,   // front face
                7, 0, 3, 0, 7, 4,   // left face
                5, 2, 1, 1, 6, 5,   // right face
                0, 6, 1, 6, 0, 4,   // bottom face
                3, 2, 5, 5, 7, 3    // top face
        };
    }

     */

    @Override
    float[] initializeVertexPositions() {
        return new float[] {
                //back face
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,

                // front face
                -0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,

                // left face
                -0.5f,  0.5f,  0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f,  0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,

                // right face
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,

                // bottom face
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f,  0.5f,

                // top face
                -0.5f,  0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f
        };
    }

    @Override
    int[] initializeVertexIndices() {
        return new int[] {
                0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11,
                12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
                24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35
        };
    }

    @Override
    float[] initializeVertexNormals() {
        return null;
    }

    @Override
    float[] initializeTextureCoordinates() {
        return null;
    }
}
