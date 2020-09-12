package engine;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

public class WindowManager {

    private static long window;                // window handle
    final private static int SCR_WIDTH = 1200;  // screen size settings
    final private static int SCR_HEIGHT = 900;

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
