package graphics.core;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryUtil.NULL;

public class WindowManager {

    private static long window;                // window handle
    final private static int SCR_WIDTH = 1200;  // screen size settings
    final private static int SCR_HEIGHT = 900;
    private static Vector3f bgColour = new Vector3f(0.2f, 0.2f, 0.2f);

    /**
     * Creates an OpenGL window.
     */
    public static void createWindow(){
        // ---GLFW window context ---
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

        glfwWindowHint(GLFW_SAMPLES, 4); // multi-sample buffer for MSAA

        // --- GLFW window creation ---
        window = glfwCreateWindow(SCR_WIDTH, SCR_HEIGHT, "OpenGL_Java", NULL, NULL);
        if(window == NULL){
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }
        // make the OpenGL context current
        glfwMakeContextCurrent(window);
        createCapabilities();  // necessary here
        glViewport(0, 0, SCR_WIDTH, SCR_HEIGHT);   // set OpenGL window (OpenGL will render in this viewport)

        // when working w/my 2nd monitor todo
        //GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());
        //glfwSetWindowPos(window, (vid.width()+SCR_WIDTH/2), (vid.height()-(int)(SCR_HEIGHT*1.5)));

    }

    /**
     * Swap back & front buffers.
     */
    public static void updateWindow(){
        glfwSwapBuffers(window);    // swap back & front buffers
    }

    /**
     * Free the window callbacks & destroy the window.
     */
    public static void closeWindow(){
        // free the window callbacks & destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
    }

    /**
     * Returns true if window should close.
     * @return true if GLFW is instructed to close the window.
     */
    public static boolean windowShouldClose(){
        return glfwWindowShouldClose(window);
    }

    /**
     * Clear screen to background colour, by clearing colour & depth buffers too...
     */
    public static void clearScreen(){
        glClearColor(bgColour.x, bgColour.y, bgColour.z, 1.0f); // specify colour to clear to
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);     // clear screen's color buffer & depth buffer
    }

    /**
     * Clear screen to background colour. (calls glClearColor)
     */
    public static void clearColour(){
        glClearColor(bgColour.x, bgColour.y, bgColour.z, 1.0f); // specify colour to clear to
    }
    /**
     * Clear screen to given colour. (calls glClearColor)
     */
    public static void clearColour(Vector3f colour){
        glClearColor(colour.x, colour.y, colour.z, 1.0f);   // specify colour to clear to
    }
    /**
     * Clear screen to given colour. (calls glClearColor)
     */
    public static void clearColour(float r, float g, float b){
        glClearColor(r, g, b, 1.0f);   // specify colour to clear to
    }

    /**
     * Clear colour & depth buffers.
     */
    public static void clearColourDepthBuffers(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    /**
     * Clear colour buffer.
     */
    public static void clearColourBuffer(){
        glClear(GL_COLOR_BUFFER_BIT);
    }
    /**
     * Clear depth buffer
     */
    public static void clearDepthBuffer(){
        glClear(GL_DEPTH_BUFFER_BIT);
    }


    /**
     * Get GLFW state of given key.
     * @param key GLFW keyboard key to get state for
     * @return state of given key
     */
    public static int getKeyState(int key){
        return glfwGetKey(window, key);
    }

    /**
     * Make window visible by calling glfwShowWindow()
     */
    public static void makeWindowVisible(){
        glfwShowWindow(window);
    }

    public static long getWindowHandle() {
        return window;
    }
    public static int getScrHeight() {
        return SCR_HEIGHT;
    }
    public static int getScrWidth() {
        return SCR_WIDTH;
    }
}
