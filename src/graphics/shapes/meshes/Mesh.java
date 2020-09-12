package graphics.shapes.meshes;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Abstract class encapsulating a 3D mesh object.
 * Must have 3D position (vertex positions), UV texture coordinates and normals.
 *
 */
public abstract class Mesh {

    // shape/rendering properties
    private int vaoHandle;
    private int vertexVBOHandle;
    private int normalHandle;
    private int texHandle;
    private int num_of_triangles;
    private int eboHandle;
    private int GLFrontFaceWinding;
    private boolean useFaceCulling;

    // abstract methods -- subclasses should implement them
    abstract float[]  initializeVertexPositions();
    abstract int[]  initializeVertexIndices();
    abstract float[]  initializeVertexNormals();
    abstract float[]  initializeTextureCoordinates();

    public Mesh(int GLFrontFaceWinding, boolean useFaceCulling){
        this.GLFrontFaceWinding = GLFrontFaceWinding;
        this.useFaceCulling = useFaceCulling;
    }

     /**
     * Initialise mesh. Must be called before using mesh.
     */
    public void initialize() {

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

        // --- load vertex positions ---
        vaoHandle = glGenVertexArrays(); // create VAO obj
        glBindVertexArray(vaoHandle); // bind vertex array (VAO)

        vertexVBOHandle = glGenBuffers();                   // create an int buffer & return int ID (create VBO- vertex buffer obj)
        glBindBuffer(GL_ARRAY_BUFFER, vertexVBOHandle);     // bind buffer (VBO)
        glBufferData(GL_ARRAY_BUFFER, vertPositions, GL_STATIC_DRAW); // copy vertex data into currently bound buffer

        // --- load vertex normals ---
        normalHandle = glGenBuffers(); // Get an OGL name for a buffer object
        glBindBuffer(GL_ARRAY_BUFFER, normalHandle); // Bring that buffer object into existence on GPU
        glBufferData(GL_ARRAY_BUFFER, vertNormals, GL_STATIC_DRAW); // Load the GPU buffer object with data

        // --- load vertex indexes ---

        eboHandle = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboHandle);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // --- load texture coordinates ---
        texHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, texHandle);
        glBufferData(GL_ARRAY_BUFFER, textureCoordinates, GL_STATIC_DRAW);
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
        glDeleteVertexArrays(normalHandle);
        glDeleteVertexArrays(texHandle);
        glDeleteBuffers(vertexVBOHandle);
        glDeleteBuffers(eboHandle);
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
