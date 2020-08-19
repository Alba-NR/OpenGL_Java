
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Main {

    private long window;        // window handle
    private ShaderProgram shaderProgram;  // shader prog to use
    private int vao;            // VAO obj  -- to manage vertex attributes (configs, assoc VBOs...)
    private int vbo;            // VBO obj -- to manage vertex data in the GPU's mem
    private int ebo;            // EBO onj -- for indexed drawing (stores indices of vertices that OpenGL will draw)
    //note: buffers as fields atm to be able to use them in dif methods

    /**
     * Initialise GLFW & window for rendering
     */
    public void init() throws IOException {

        // --- init & config GLFW ---
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

        // --- GLFW window creation ---
        window = glfwCreateWindow(800, 800, "learning", NULL, NULL);
        if(window == NULL){
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }
        // make the OpenGL context current
        glfwMakeContextCurrent(window);
        createCapabilities();  // necessary here
        glViewport(0, 0, 800, 800);   // set OpenGL window (OpenGL will render in this viewport)

        // --- callback functions registered after window is created & before render loop is init ---

        // whenever window is resized, call given funct -- adjusts viewport
        glfwSetFramebufferSizeCallback(window, (long window, int width, int height) -> glViewport(0, 0, width, height));
        // whenever key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) // close window when esc key is released
                glfwSetWindowShouldClose(window, true);
            if (key == GLFW_KEY_W) { // view in wireframe mode whilst W is pressed
                if ( action == GLFW_PRESS ) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                else if ( action == GLFW_RELEASE ) glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            }
        });

        // make window visible
        glfwShowWindow(window);

        // --- set up shaders ---

        // create vertex shader
        Shader vertexShader = new Shader(GL_VERTEX_SHADER, "./resources/triangle_vertex_shader.glsl");
        // create fragment shader
        Shader fragmentShader = new Shader(GL_FRAGMENT_SHADER, "./resources/triangle_fragment_shader.glsl");

        // create shader program
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);


        // --- set up vertex data & buffers ---
        /*
        float[] vertices = {
                0.5f,  0.5f, 0.0f,      // top right
                0.5f, -0.5f, 0.0f,      // bottom right
                -0.5f, -0.5f, 0.0f,     // bottom left
                -0.5f,  0.5f, 0.0f     // top left
        };
        int[] indices = {
                0, 1, 3,   // first triangle
                1, 2, 3    // second triangle
        };
         */
        float[] vertices = {
                // positions         // colours
                0.5f, -0.5f, 0.0f,  1.0f, 0.0f, 0.0f,    // bottom right
                -0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f,    // bottom left
                0.0f,  0.5f, 0.0f,  0.0f, 0.0f, 1.0f     // top
        };
        vao = glGenVertexArrays();              // create vertex array (VAO- vertex array obj)
        vbo = glGenBuffers();                   // create an int buffer & return int ID (create VBO- vertex buffer obj)
        //ebo = glGenBuffers();                   // create EBO buffer (EBO- element buffer obj)
        glBindVertexArray(vao);                 // bind vertex array (VAO)
        glBindBuffer(GL_ARRAY_BUFFER, vbo);     // bind buffer (VBO)
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW); // copy vertex data into currently bound buffer
        //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        //glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // --- link vertex attributes ---
        /*
        specify how openGL should interpret the vertex data
        arguments to glVertexAttribPointer():
              - pass in data to vertex attrib at location 0
              - size of vertex attrib
              - type of the data
              - specifies if we want the data to be normalized
              - the stride
              - offset of where the position data begins in the buffer.
         */

        // position attrib (at location 0)
        // stride is 6*4 for the floats (4 bytes) (x,y,z)(r,g,b)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6*4, 0);
        glEnableVertexAttribArray(0);
        // colour attrib (at location 1)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6*4, 3*4); // 3*4 for skipping the 1st 3 floats (x,y,z)
        glEnableVertexAttribArray(1);


        glBindBuffer(GL_ARRAY_BUFFER, 0);    // unbind VBO
        glBindVertexArray(0);                       // unbind VAO
    }

    /**
     * Rendering loop
     */
    public void renderLoop(){
        // repeat while GLFW isn't instructed to close
        while(!glfwWindowShouldClose(window)){

            // clear screen
            glClearColor(0.2f, 0.2f, 0.2f, 1.0f); // specify colour to clear to
            glClear(GL_COLOR_BUFFER_BIT); // clear screen's color buffer (entire color buffer is filled w/the colour)

            // render commands
            shaderProgram.use();

            glBindVertexArray(vao);                                  // bind element buffer
            glDrawArrays(GL_TRIANGLES, 0, 3);           // draw it (as triangles)
            //glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);    // draw it as triangles (if using EBO)
            glBindVertexArray(0);                                    // remove the binding

            // check events & swap buffers
            glfwSwapBuffers(window);    // swap back & front buffers
            glfwPollEvents();           // checks if any events are triggered, updates window state, & calls corresponding funcs
        }
    }

    /**
     * Terminate GLFW & window
     */
    public void terminate(){

        // free the window callbacks & destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // de-allocate all resources
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        shaderProgram.delete();

        // clean/delete all other GLFW's resources
        glfwTerminate();
    }

    public static void main(String[] args) throws IOException {
        Main app = new Main();
        app.init();         // initialise application
        app.renderLoop();   // rendering loop
        app.terminate();    // terminate application
    }
}
