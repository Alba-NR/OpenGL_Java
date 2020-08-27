
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;

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
    private Camera camera = new Camera();
    private float cameraSpeed;
    //note: buffers, shaderProg, texture & camera as fields atm to be able to use them in dif methods

    // screen size settings
    final private int SCR_WIDTH = 800;
    final private int SCR_HEIGHT = 600;

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
        window = glfwCreateWindow(SCR_WIDTH, SCR_HEIGHT, "learning", NULL, NULL);
        if(window == NULL){
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }
        // make the OpenGL context current
        glfwMakeContextCurrent(window);
        createCapabilities();  // necessary here
        glViewport(0, 0, SCR_WIDTH, SCR_HEIGHT);   // set OpenGL window (OpenGL will render in this viewport)

        glEnable(GL_DEPTH_TEST); // enable depth testing

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
            // arrows used to move camera (in processArrowsInput() method)
        });

        // make window visible
        glfwShowWindow(window);

        // --- set up shaders ---

        // create vertex shader
        Shader vertexShader = new Shader(GL_VERTEX_SHADER, "./resources/vertex_shader.glsl");
        // create fragment shader
        Shader fragmentShader = new Shader(GL_FRAGMENT_SHADER, "./resources/fragment_shader.glsl");

        // create shader program
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);


        // --- set up vertex data & buffers ---

        float[] vertices = {
                -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
                0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
                -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

                -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
        };

        vao = glGenVertexArrays();              // create vertex array (VAO- vertex array obj)
        vbo = glGenBuffers();                   // create an int buffer & return int ID (create VBO- vertex buffer obj)
        glBindVertexArray(vao);                 // bind vertex array (VAO)
        glBindBuffer(GL_ARRAY_BUFFER, vbo);     // bind buffer (VBO)
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW); // copy vertex data into currently bound buffer

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
        // stride is 5*4 for the floats (1 float -> 4 bytes) (x,y,z)(s,t)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5*4, 0);
        glEnableVertexAttribArray(0);
        // texel attrib
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5*4, 3*4);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);    // unbind VBO
        glBindVertexArray(0);                       // unbind VAO
    }

    /**
     * Rendering loop
     */
    public void renderLoop(){
        shaderProgram.use();    // set shader program to use

        // textures
        Texture texture1 = new Texture("./resources/container.jpg", false); // create texture objects
        Texture texture2 = new Texture("./resources/awesomeface.png", true);
        shaderProgram.uploadInt("texture1", 0); // set texture unit to which each shader sampler belongs to
        shaderProgram.uploadInt("texture2", 1);

        // create & upload projection matrix
        Matrix4f projection = new Matrix4f();
        projection.setPerspective((float) Math.PI / 4, (float) SCR_WIDTH / SCR_HEIGHT, 0.1f, 100.0f);
        shaderProgram.uploadMatrix4f(projection, "proj_m");

        // multiple cubes
        Vector3f[] cubePositions = {
                new Vector3f(0.0f,  0.0f,  0.0f),
                new Vector3f(2.0f,  5.0f, -15.0f),
                new Vector3f(-1.5f, -2.2f, -2.5f),
                new Vector3f(-3.8f, -2.0f, -12.3f),
                new Vector3f( 2.4f, -0.4f, -3.5f),
                new Vector3f(-1.7f,  3.0f, -7.5f),
                new Vector3f( 1.3f, -2.0f, -2.5f),
                new Vector3f( 1.5f,  2.0f, -2.5f),
                new Vector3f( 1.5f,  0.2f, -1.5f),
                new Vector3f(-1.3f,  1.0f, -1.5f)
        };

        float deltaTime;	        // Time between current frame and last frame
        float lastFrameT = 0.0f;    // Time of last frame

        // repeat while GLFW isn't instructed to close
        while(!glfwWindowShouldClose(window)){
            // --- per-frame time logic ---
            float currentFrameT = (float) glfwGetTime();
            deltaTime = currentFrameT - lastFrameT;
            lastFrameT = currentFrameT;
            cameraSpeed = 5.0f * deltaTime;

            // --- process keyboard arrows input --
            processArrowsInput();

            // --- clear screen ---
            glClearColor(0.2f, 0.2f, 0.2f, 1.0f); // specify colour to clear to
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear screen's color buffer & depth buffer

            // --- render commands ---

            // textures
            glActiveTexture(GL_TEXTURE0);       // bind 1st texture to texture unit 0
            glBindTexture(GL_TEXTURE_2D, texture1.getHandle());
            glActiveTexture(GL_TEXTURE1);       // bind 2nd texture to texture unit 1
            glBindTexture(GL_TEXTURE_2D, texture2.getHandle());

            // draw/render
            glBindVertexArray(vao);     // bind vertex attrib buffer

             // calc view matrix
            Matrix4f view = camera.calcLookAt();
            shaderProgram.uploadMatrix4f(view, "view_m");

            for(int i = 0; i < cubePositions.length; i++){
                Matrix4f model = new Matrix4f();  // calc model matrix
                model.translate(cubePositions[i]);
                model.rotate((float) Math.toRadians(20.0f * i), (new Vector3f(1.0f, 0.3f, 0.5f)).normalize());
                shaderProgram.uploadMatrix4f(model, "model_m");

                glDrawArrays(GL_TRIANGLES, 0, 36);  // draw it (as triangles)
            }
            glBindVertexArray(0);       // remove the binding

            // --- check events & swap buffers ---
            glfwSwapBuffers(window);    // swap back & front buffers
            glfwPollEvents();           // checks if any events are triggered, updates window state, & calls corresponding funcs
        }
    }

    /**
     * Called in render loop to contnually process input from keyboard arrows in each frame.
     */
    private void processArrowsInput(){
        // camera movement using arrows
        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS) {
            // UP -> cameraPos += cameraFront * cameraSpeed
            camera.setCameraPos(camera.getCameraPos().add(camera.getCameraFront().mul(cameraSpeed)));
        }if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS) {
            // DOWN -> cameraPos -= cameraFront * cameraSpeed
            camera.setCameraPos(camera.getCameraPos().sub(camera.getCameraFront().mul(cameraSpeed)));
        }if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS) {
            // LEFT -> cameraPos -= normalize(cross(cameraFront, cameraUp)) * cameraSpeed
            camera.setCameraPos(camera.getCameraPos().sub(camera.getCameraFront().cross(camera.getCameraUp()).normalize().mul(cameraSpeed)));
        }if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS) {
            // RIGHT -> cameraPos += normalize(cross(cameraFront, cameraUp)) * cameraSpeed
            camera.setCameraPos(camera.getCameraPos().add(camera.getCameraFront().cross(camera.getCameraUp()).normalize().mul(cameraSpeed)));
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
