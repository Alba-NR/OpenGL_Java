package graphics.shapes.meshes;

import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class ModelLoader {

    private static Map<String, MeshFromOBJ> mapFilenameToInstance = new HashMap<>();

    public static Mesh loadModel(String filePath){
        MeshFromOBJ returnValue = mapFilenameToInstance.getOrDefault(filePath, null);

        if( returnValue == null) {
            // create assimp scene obj
            AIScene scene = Assimp.aiImportFile(filePath,
                    Assimp.aiProcess_Triangulate |
                            Assimp.aiProcess_JoinIdenticalVertices
            );
            if (scene == null) System.err.println("Couldn't load model at" + filePath); // todo: raise exception instead

            // get 1st mesh
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(0)); // get 1st mesh
            int vertexCount = mesh.mNumVertices();
            int faceCount = mesh.mNumFaces();

            AIVector3D.Buffer vertices = mesh.mVertices(); // store vertices in buffer
            AIVector3D.Buffer normals = mesh.mNormals();
            AIFace.Buffer faces = mesh.mFaces();

            float[] vPositions = new float[vertexCount * 3];
            float[] vNormals = new float[vertexCount * 3];
            int[] vIndeces = new int[vertexCount * 3];
            float[] texCoords = new float[vertexCount * 2];

            // add all vertex positions, indices, normals & texture coords in AImesh into appropriate arrays
            for (int i = 0; i < vertexCount; i++) {
                // vertices
                AIVector3D vertex = vertices.get(i);
                vPositions[i * 3] = vertex.x();
                vPositions[i * 3 + 1] = vertex.y();
                vPositions[i * 3 + 2] = vertex.z();

                // normals
                AIVector3D normal = normals.get(i);
                vNormals[i * 3] = normal.x();
                vNormals[i * 3 + 1] = normal.y();
                vNormals[i * 3 + 2] = normal.z();

                // indices
                if (i < faceCount) {
                    IntBuffer faceIndeces = faces.get(i).mIndices();
                    vIndeces[i * 3] = faceIndeces.get(0);
                    vIndeces[i * 3 + 1] = faceIndeces.get(1);
                    vIndeces[i * 3 + 2] = faceIndeces.get(2);
                }

                // texture coords
                float texX = 0.0f;
                float texY = 0.0f;
                if (mesh.mNumUVComponents().get(0) != 0) { // there are tex coords
                    AIVector3D texCoord = mesh.mTextureCoords(0).get(i);
                    texX = texCoord.x();
                    texY = texCoord.y();
                }
                texCoords[i * 2] = texX;
                texCoords[i * 2 + 1] = texY;
            }

            // return Mesh obj
            returnValue = new MeshFromOBJ(vPositions, vIndeces, vNormals, texCoords);

            // place into map
            mapFilenameToInstance.put(filePath, returnValue);
        }

        return returnValue;
    }
}
