package graphics.meshes;

import graphics.textures.Texture;

import java.util.List;

public class MeshFromOBJ extends Mesh {
    private float[] vPositions;
    private int[] vIndeces;
    private float[] vNormals;
    private float[] texCoords;

    public MeshFromOBJ(float[] vPositions, int[] vIndeces, float[] vNormals, float[] texCoords){
        super();
        this.vPositions = vPositions;
        this.vIndeces = vIndeces;
        this.vNormals = vNormals;
        this.texCoords = texCoords;
        initialize();
    }

    public MeshFromOBJ(float[] vPositions, int[] vIndeces, float[] vNormals, float[] texCoords, List<Texture> texturesList){
        super(texturesList);
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
