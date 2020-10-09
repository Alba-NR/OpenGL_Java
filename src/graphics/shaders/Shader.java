package graphics.shaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a single shader to be used later on in a ShaderProgram.
 */
public class Shader {
    private int id;
    private int type;
    private String filename;

    /**
     * Constructor initialises fields & calls load() to read the shader code from
     * the given filename and builds the shader
     * @param type the type of shader to build: GL_VERTEX_SHADER or GL_FRAGMENT_SHADER
     * @param filename  name of the text file with the GLSL shaderID
     */
    public Shader(int type, String filename) {
        this.type = type;
        this.filename = filename;
        load();
    }

    /**
     * Reads the shader code from the given filename in the constructor & builds the shader
     */
    private void load(){
        // read the shader's source code from given file
        String shaderSource;
        try {
            shaderSource = String.join("\n", Files.readAllLines(Paths.get(filename)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load shader file: " + filename);
        }

        // create & compile shader
        id = glCreateShader(type);          // create shader obj
        glShaderSource(id, shaderSource);   // attach shader code
        glCompileShader(id);                // compile shader code

        // check if compilation failed
        int compilationStatus = glGetShaderi(id, GL_COMPILE_STATUS);
        if (compilationStatus == 0) {
            String errorLog = glGetShaderInfoLog(id);
            System.out.println("errorLog: " + errorLog);
            glDeleteShader(id);
            throw new RuntimeException("Shader compilation failed: consult the log above");
        }
    }

    public int getHandle() {
        return id;
    }
}
