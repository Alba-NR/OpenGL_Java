package graphics.shapes.meshes;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Abstract class encapsulating a 3D mesh object.
 * Should have 3D position (vertex positions), UV texture coordinates and normals.
 *
 * Note: subclasses representing specific shapes/meshes should implement the singleton
 *      pattern, to avoid unnecessary creation & duplication of buffers.
 */
public abstract class Mesh {

    // shape/rendering properties
    private int vaoHandle;
    private int vertexVBOHandle;
    private int normalHandle = -1;
    private int texHandle = -1;
    private int num_of_triangles;
    private int eboHandle;
    private int GLFrontFaceWinding;
    private boolean useFaceCulling;

    // abstract methods -- subclasses should implement them
    abstract float[]  initializeVertexPositions();
    abstract int[]  initializeVertexIndices();
    abstract float[]  initializeVertexNormals();
    abstract float[]  initializeTextureCoordinates();

    Mesh(int GLFrontFaceWinding, boolean useFaceCulling){
        this.GLFrontFaceWinding = GLFrontFaceWinding;
        this.useFaceCulling = useFaceCulling;
    }

     /**
     * Initialise mesh. Must be called before using mesh.
     */
     void initialize() {

        float[] vertPositions = initializeVertexPositions();
        int[] indices = initializeVertexIndices();
        float[] vertNormals = initializeVertexNormals();
        float[] textureCoordinates = initializeTextureCoordinates();
        num_of_triangles = indices.length;

        loadDataOntoGPU(vertPositions, indices, vertNormals, textureCoordinates);
    }

    /**
     * Move data from Java arrays to the corresponding OpenGL buffers.
     * @param vertPositions array of vertex positions
     * @param indices array of indeces for each vertex
     * @param vertNormals array of normal vectors
     * @param textureCoordinates array specifying tex coordinates
     */
    private void loadDataOntoGPU(float[] vertPositions, int[] indices, float[] vertNormals, float[] textureCoordinates) {
        vaoHandle = glGenVertexArrays();    // create VAO obj
        glBindVertexArray(vaoHandle);       // bind vertex array (VAO)

        // --- load vertex positions ---
        vertexVBOHandle = glGenBuffers();                   // create an int buffer & return int ID (create VBO- vertex buffer obj)
        glBindBuffer(GL_ARRAY_BUFFER, vertexVBOHandle);     // bind buffer (VBO)
        glBufferData(GL_ARRAY_BUFFER, vertPositions, GL_STATIC_DRAW); // copy vertex data into currently bound buffer

        // --- load vertex indexes ---
        eboHandle = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboHandle);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // --- load vertex normals ---
        if (vertNormals != null) {
            normalHandle = glGenBuffers(); // Get an OGL name for a buffer object
            glBindBuffer(GL_ARRAY_BUFFER, normalHandle); // Bring that buffer object into existence on GPU
            glBufferData(GL_ARRAY_BUFFER, vertNormals, GL_STATIC_DRAW); // Load the GPU buffer object with data
        }

        // --- load texture coordinates ---
        if (textureCoordinates != null) {
            texHandle = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, texHandle);
            glBufferData(GL_ARRAY_BUFFER, textureCoordinates, GL_STATIC_DRAW);
        }
    }

    /**
     * Draw the mesh using the currently active shader program.
     */
    public void render(){
        if(!useFaceCulling) glDisable(GL_CULL_FACE);    // disable face culling
        else glFrontFace(GLFrontFaceWinding);           // set front facing faces winding (for back face culling)

        // draw mesh
        glBindVertexArray(vaoHandle);
        glDrawElements(GL_TRIANGLES, num_of_triangles, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        if(!useFaceCulling) glEnable(GL_CULL_FACE);     // enable face culling again (bc default is enabled)
    }

    public void deallocateResources(){
        glDeleteVertexArrays(vaoHandle);
        glDeleteBuffers(vertexVBOHandle);
        glDeleteBuffers(eboHandle);
        if (normalHandle != -1) glDeleteVertexArrays(normalHandle);
        if (texHandle != -1) glDeleteVertexArrays(texHandle);
    }

    public int getVAOHandle(){
        return vaoHandle;
    }
    public int getNormalHandle() {
        return normalHandle;
    }
    public int getTexHandle() {
        return texHandle;
    }
    public int getVertexVBOHandle() {
        return vertexVBOHandle;
    }
    public int getNumOfTriangles() {
        return num_of_triangles;
    }
    public int getEboHandle() {
        return eboHandle;
    }

    public void setGLFrontFaceWinding(int GLFrontFaceWinding) {
        this.GLFrontFaceWinding = GLFrontFaceWinding;
    }
    public void setUseFaceCulling(boolean useFaceCulling) {
        this.useFaceCulling = useFaceCulling;
    }
}
