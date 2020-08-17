
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL.*;

public class Main {

    // window handle
    private long window;

    /**
     * Initialise GLFW & window for rendering
     */
    public void init() {

        // initialize GLFW
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // instantiate the GLFW window (using OpenGL 3.3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // create new GLFW window object (size 1000x1000)
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
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true);
        });

        // make window visible
        glfwShowWindow(window);
    }

    /**
     * Rendering loop
     */
    public void renderLoop(){
        // repeat while GLFW isn't instructed to close
        while(!glfwWindowShouldClose(window)){
            // clear screen
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f); // specify colour to clear to
            glClear(GL_COLOR_BUFFER_BIT); // clear screen's color buffer (entire color buffer is filled w/the colour)

            // input
            //processInput(window);

            // render commands

            // check events & swap buffers
            glfwSwapBuffers(window);    // swap back & front buffers
            glfwPollEvents();           // checks if any events are triggered, updates window state, & calls corresponding funcs
        }
    }

    private void processInput(long window){
        // check if user has pressed escape key (if so => set WindowShouldClose property to true)
        if(glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) glfwSetWindowShouldClose(window, true);
    }

    /**
     * Terminate GLFW & window
     */
    public void terminate(){

        // free the window callbacks & destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // clean/delete all of GLFW's resources
        glfwTerminate();
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.init();         // initialise application
        app.renderLoop();   // rendering loop
        app.terminate();    // terminate application
    }
}
