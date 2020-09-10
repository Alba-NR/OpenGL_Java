package graphics.meshes;

import graphics.materials.Material;

/**
 * Represents a square.
 */
public class SquareMesh extends Mesh {

    public SquareMesh(){
        super();
        initialize();
    }

    public SquareMesh(Material material){
        super(material);
        initialize();
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
