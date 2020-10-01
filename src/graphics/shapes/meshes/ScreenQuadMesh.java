package graphics.shapes.meshes;

import static org.lwjgl.opengl.GL11.GL_CCW;

public class ScreenQuadMesh extends Mesh {
    private static ScreenQuadMesh instance = null;

    private ScreenQuadMesh() {
        super(GL_CCW, true);
        initialize();
    }

    public static ScreenQuadMesh getInstance(){
        if(instance == null){
            instance = new ScreenQuadMesh();
        }
        return instance;
    }

    @Override
    float[] initializeVertexPositions() {
        // vertex attributes for a quad that fills the entire screen in Normalized Device Coordinates.
        return new float[]{
                -1.0f, 1.0f,
                -1.0f, -1.0f,
                1.0f, -1.0f,

                -1.0f, 1.0f,
                1.0f, -1.0f,
                1.0f,  1.0f,
        };
    }

    @Override
    int[] initializeVertexIndices() {
        return new int[]{
                0,1,2,3,4,5
        };
    }

    @Override
    float[] initializeVertexNormals() {
        return null;
    }

    @Override
    float[] initializeTextureCoordinates() {
        return new float[]{
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,

                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };
    }
}
