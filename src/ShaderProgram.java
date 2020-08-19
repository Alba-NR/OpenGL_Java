import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDeleteShader;

/**
 * Represents a shader program to be used in an OpenGL application.
 */
public class ShaderProgram {
    private Shader vertexShader;
    private Shader fragmentShader;
    private int id ;

    /**
     * Initialise fields to given values.
     * Calls createProgram to create a new shader program for OpenGL.
     * @param vertexShader {@link Shader} vertex shader to include
     * @param fragmentShader {@link Shader} fragment shader to include
     */
    ShaderProgram(Shader vertexShader, Shader fragmentShader) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        createProgram();
    }

    /**
     * Creates a new shader program & inks shaders together into this created program.
     */
    private void createProgram(){

        id = glCreateProgram();                         // create shader program
        glAttachShader(id, vertexShader.getHandle());   // attach compiled shaders to program
        glAttachShader(id, fragmentShader.getHandle());
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
     * @param name name of integer uniform variable to change value of
     * @param value new integer value
     */
    void uniformSetInt(String name, int value){
        glUniform1i(glGetUniformLocation(id, name), (value));
    }
    /**
     * Sets value of the specified float uniform variable in program to the
     * new, given value
     * @param name name of float uniform variable to change value of
     * @param value new float value
     */
    void uniformSetFloat(String name, float value){
        glUniform1f(glGetUniformLocation(id, name), value);
    }
}
