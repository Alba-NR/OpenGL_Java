package graphics.shaders;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a shader program to be used in an OpenGL application.
 * (consists of vertex & fragment shaders, and optionally a geometry shader)
 */
public class ShaderProgram {
    private Shader vertexShader;
    private Shader fragmentShader;
    private Shader geomShader;
    private int id ;

    /**
     * Initialise fields to given values.
     * Calls createProgram to create a new shader program for OpenGL.
     * @param vertexShader {@link Shader} vertex shader to include
     * @param fragmentShader {@link Shader} fragment shader to include
     */
    public ShaderProgram(Shader vertexShader, Shader fragmentShader) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        this.geomShader = null;
        createProgram();
    }

    /**
     * Initialise fields to given values.
     * Calls createProgram to create a new shader program for OpenGL.
     * @param vertexShader {@link Shader} vertex shader to include
     * @param fragmentShader {@link Shader} fragment shader to include
     * @param geomShader {@link Shader} geometry shader to include
     */
    public ShaderProgram(Shader vertexShader, Shader fragmentShader, Shader geomShader) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        this.geomShader = geomShader;
        createProgram();
    }

    /**
     * Creates a new shader program & inks shaders together into this created program.
     */
    private void createProgram(){

        id = glCreateProgram();                         // create shader program
        glAttachShader(id, vertexShader.getHandle());   // attach compiled shaders to program
        glAttachShader(id, fragmentShader.getHandle());
        if(geomShader != null) glAttachShader(id, geomShader.getHandle());
        glLinkProgram(id);                              // link attached shaders in one program

        // check if linking failed
        int linkingStatus = glGetProgrami(id, GL_LINK_STATUS);
        if (linkingStatus == 0) {
            String errorLog = glGetProgramInfoLog(id);
            System.out.println("errorLog: " + errorLog);
            glDeleteShader(id);
            throw new RuntimeException("Shader linking failed: consult the log above");
        }
    }

    /**
     * Use this shader program in an OpenGL application.
     */
    public void use(){
        glUseProgram(id);
    }
    /**
     * Delete this shader program in an OpenGL application.
     */
    public void delete(){
        glDeleteProgram(id);
    }

    /**
     * Sets value of the specified integer uniform variable in program to the
     * new, given value
     * @param target target of integer uniform variable to change value of
     * @param value new integer value
     */
    public void uploadInt(String target, int value){
        glUniform1i(glGetUniformLocation(id, target), value);
    }
    /**
     * Sets value of the specified float uniform variable in program to the
     * new, given value
     * @param target target of float uniform variable to change value of
     * @param value new float value
     */
    public void uploadFloat(String target, float value){
        glUniform1f(glGetUniformLocation(id, target), value);
    }

    /**
     * Upload a 3-component vector (v0, v1, v2) to 'target' shader uniform variable
     * @param v0 1st component of vector
     * @param v1 2nd component of vector
     * @param v2 3rd component of vector
     * @param target name of uniform variable to which to upload vector
     */
    public void uploadVec3f(String target, float v0, float v1, float v2){
        glUniform3f(glGetUniformLocation(id, target), v0, v1, v2);   // set vector as uniform value
    }
    /**
     * Upload a 3-component vector 'vector' to 'target' shader uniform variable
     * @param vector vector to upload
     * @param target name of uniform variable to which to upload vector
     */
    public void uploadVec3f(String target, Vector3f vector){
        glUniform3f(glGetUniformLocation(id, target), vector.x, vector.y, vector.z);   // set vector as uniform value
    }

    /**
     * Upload a 3x3 matrix 'm' to 'target' shader uniform variable
     * @param m {@link Matrix3f} to upload (i.e. to set 'target' to)
     * @param target name of uniform variable to which to upload matrix
     */
    public void uploadMatrix3f(String target, Matrix3f m) {
        int targetLocation = glGetUniformLocation(id, target);   // get location of target uniform
        FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
        m.get(buffer);  // store matrix in column-major order into buffer
        glUniformMatrix3fv(targetLocation, false, buffer);   // set matrix as uniform value
    }
    /**
     * Upload a 4x4 matrix 'm' to 'target' shader uniform variable
     * @param m {@link Matrix4f} to upload (i.e. to set 'target' to)
     * @param target name of uniform variable to which to upload matrix
     */
    public void uploadMatrix4f(String target, Matrix4f m) {
        int targetLocation = glGetUniformLocation(id, target);   // get location of target uniform
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        m.get(buffer);  // store matrix in column-major order into buffer
        glUniformMatrix4fv(targetLocation, false, buffer);   // set matrix as uniform value
    }

    /**
     * Upload a float array to 'target' shader uniform variable
     * @param arr float array to upload
     * @param target name of uniform variable to which to upload array
     */
    public void uploadFloatArray(String target, float[] arr){
        glUniform1fv(glGetUniformLocation(id, target), arr);    // set array as uniform value
    }
    /**
     * Upload a int array to 'target' shader uniform variable
     * @param arr int array to upload
     * @param target name of uniform variable to which to upload array
     */
    public void uploadIntArray(String target, int[] arr){
        glUniform1iv(glGetUniformLocation(id, target), arr);    // set array as uniform value
    }

    /**
     * Tell shader where it can find attributes (inputs...)
     * @param location location of shader variable to which to bind given data
     * @param arrayBufferHandle	handle to data (vertex positions, normals or texture coordinates)
     * @param attribSize "dimensionality" of data/attrib (vertex & normals = 3, texture coords = 2)
     */
    public void bindDataToShader(int location, int arrayBufferHandle, int attribSize) {
        glBindBuffer(GL_ARRAY_BUFFER, arrayBufferHandle); // bind given buffer

        // specify how openGL should interpret the data
        glVertexAttribPointer(location, attribSize, GL_FLOAT, false, 0, 0);
        // enable attrib variable
        glEnableVertexAttribArray(location);
    }

    public int getHandle(){
        return id;
    }
}
