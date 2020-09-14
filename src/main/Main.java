package main;

import graphics.camera.Camera;
import graphics.camera.CameraMovement;
import graphics.core.WindowManager;
import graphics.lights.DirLight;
import graphics.lights.FlashLight;
import graphics.lights.PointLight;
import graphics.materials.Material;
import graphics.renderEngine.EntityRenderer;
import graphics.renderEngine.PointLightRenderer;
import graphics.renderEngine.RenderContext;
import graphics.renderEngine.Renderer;
import graphics.scene.DrawableEntity;
import graphics.scene.Entity;
import graphics.scene.Scene;
import graphics.shapes.Cube;
import graphics.shaders.Shader;
import graphics.shaders.ShaderProgram;
import graphics.shapes.Shape;
import graphics.textures.Texture;
import graphics.textures.TextureType;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;


public class Main {

    private ShaderProgram cubeShaderProgram;  // shader prog to use for cubes
    private ShaderProgram lightShaderProgram;  // shader prog to use for light cube
    private Scene scene;        // scene to render

    final private int SCR_WIDTH = WindowManager.getScrWidth();  // screen size settings
    final private int SCR_HEIGHT = WindowManager.getScrHeight();

    private Camera camera = new Camera();   // camera & mouse
    private double lastX = SCR_WIDTH / 2.0f, lastY = SCR_HEIGHT / 2.0f;
    private boolean firstMouse = true;


    /**
     * Initialise GLFW & window for rendering
     */
    public void init() throws IOException {

        // --- init & config GLFW ---
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // --- GLFW window creation (& init GLFW context)---
        WindowManager.createWindow();
        long win = WindowManager.getWindowHandle();

        glEnable(GL_DEPTH_TEST);    // enable depth testing
        glEnable(GL_CULL_FACE);     // enable culling
        glCullFace(GL_BACK);        // cull back faces
        glFrontFace(GL_CCW);        // initially set front faces as those w/counter clockwise winding

        // --- callback functions registered after window is created & before render loop is init ---

        // whenever window is resized, call given funct -- adjusts viewport
        glfwSetFramebufferSizeCallback(win, (long window, int width, int height) -> glViewport(0, 0, width, height));
        // whenever key is pressed, repeated or released.
        glfwSetKeyCallback(win, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) // close window when esc key is released
                glfwSetWindowShouldClose(window, true);
            if (key == GLFW_KEY_E) { // view in wireframe mode whilst E is pressed
                if ( action == GLFW_PRESS ) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                else if ( action == GLFW_RELEASE ) glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            }
            // AWSD used to move camera (in processArrowsInput() method)
        });
        glfwSetInputMode(win, GLFW_CURSOR, GLFW_CURSOR_DISABLED); // use mouse
        glfwSetCursorPosCallback(win, (long window, double xpos, double ypos) -> {
            if (firstMouse) {
                lastX = xpos;
                lastY = ypos;
                firstMouse = false;
            }
            double xoffset = (xpos - lastX);
            double yoffset = (lastY - ypos); // reversed since y-coord range from bottom to top
            lastX = xpos;
            lastY = ypos;

            camera.processMouseMovement(xoffset, yoffset, true);
        });
        glfwSetScrollCallback(win, (long window, double xoffset, double yoffset) -> { // 'zoom' illusion when scroll w/mouse
            camera.processMouseScroll(yoffset);
        });

        // make window visible
        WindowManager.makeWindowVisible();

        // --- set up shaders ---

        // create cube vertex shader
        Shader cubeVertexShader = new Shader(GL_VERTEX_SHADER, "./resources/shaders/phong_vertex_shader.glsl");
        // create cube fragment shader
        //Shader cubeFragmentShader = new Shader(GL_FRAGMENT_SHADER, "./resources/shaders/phong_withColour_fragment_shader.glsl");
        Shader cubeFragmentShader = new Shader(GL_FRAGMENT_SHADER, "./resources/shaders/phong_withTexture_fragment_shader.glsl");
        // create cube shader program
        cubeShaderProgram = new ShaderProgram(cubeVertexShader, cubeFragmentShader);

        // create light cube vertex shader
        Shader lightVertexShader = new Shader(GL_VERTEX_SHADER, "./resources/shaders/lightSource_vertex_shader.glsl");
        // create light cube fragment shader
        Shader lightFragmentShader = new Shader(GL_FRAGMENT_SHADER, "./resources/shaders/lightSource_fragment_shader.glsl");
        // create light cube shader program
        lightShaderProgram = new ShaderProgram(lightVertexShader, lightFragmentShader);
    }

    /**
     * Rendering loop
     */
    public void renderLoop(){

        // --- create renderers ---
        Renderer entityRenderer = new EntityRenderer(cubeShaderProgram);
        Renderer lightSourceRenderer = new PointLightRenderer(lightShaderProgram);

        // --------- SET UP SCENE ---------

        // --- set-up lights ---

        // directional light
        DirLight dirLight = new DirLight(new Vector3f(1.0f, 1.0f, 1.0f), 1.0f, new Vector3f(-0.2f, -1.0f, -0.3f));

        // flashlight spotlight
        FlashLight flashLight = new FlashLight(
                camera.getCameraPos(),
                new Vector3f(0.5f, 0.5f, 1.0f),
                2.5f,
                camera.getCameraFront(),
                1.0f,
                0.045f,
                0.00075f,
                (float) Math.cos(Math.toRadians(5)),
                (float) Math.cos(Math.toRadians(7))
        );

        // point lights
        List<PointLight> pointLightsList = new ArrayList<>();
        Vector3f[] pointLightPositions = {
                new Vector3f( -1.0f,  2.0f,  2.0f),
                new Vector3f( 2.0f, 2.0f, -2.0f),
                new Vector3f(-5.0f,  2.0f, -5.0f)
        };
        Vector3f[] pointLightColours = {
                new Vector3f(0.0f, 1.0f, 1.0f),     //blue
                new Vector3f(1.0f,  0.0f, 0.0f),    // red
                new Vector3f(1.0f, 1.0f, 0.0f)      // yellow
        };

        // point light 1
        PointLight pointLight1 = new PointLight(
                pointLightPositions[0],
                pointLightColours[0],
                2.5f,
                1.0f,
                 0.09f,
                0.032f
        );
        pointLightsList.add(pointLight1);

        // point light 2
        PointLight pointLight2 = new PointLight(
                pointLightPositions[1],
                pointLightColours[1],
                2.5f,
                1.0f,
                0.09f,
                0.032f
        );
        pointLightsList.add(pointLight2);

        // point light 3
        PointLight pointLight3 = new PointLight(
                pointLightPositions[2],
                pointLightColours[2],
                2.5f,
                1.0f,
                0.14f,
                0.07f
        );
        pointLightsList.add(pointLight3);

        Vector3f ambientIntensity = new Vector3f(0.7f,0.7f,1.0f);

        // --- SET UP ENTITIES ---
        List<Texture> texList = Arrays.asList(
                new Texture("./resources/textures/container2.png", false, TextureType.DIFFUSE),
                new Texture("./resources/textures/container2_specular.png", false, TextureType.SPECULAR)
        );
        Material material = new Material(texList);
        Shape shape = new Cube(material); // new Square(material); //new ShapeFromOBJ("./resources/models/cargo_container.obj", material, true);

        // calc local transform matrix for shape
        Matrix4f local_transform1 = new Matrix4f();
        local_transform1.translate(-2.0f, -2.0f, -2.0f);

        // create 1st cube entity
        Entity entity1 = new DrawableEntity(null, local_transform1, new Vector3f(2.0f), shape);

        // create 2nd cube entity
        Matrix4f local_transform2 = new Matrix4f();
        local_transform2.translate(0.0f, 0.75f, 0.0f);

        // create 2nd cube entity, child of 1st cube entity
        Entity entity2 = new DrawableEntity(entity1, local_transform2, new Vector3f(0.5f), shape);
        entity1.addChild(entity2);

        // add entities to components list
        List<Entity> components = Arrays.asList(entity1, entity2);

        // --- CREATE SCENE ---
        scene = new Scene(components, dirLight, flashLight, pointLightsList, ambientIntensity);


        // --------- RENDER LOOP ---------

        // --- prepare renderers ---
        entityRenderer.prepare(scene);
        lightSourceRenderer.prepare(scene);

        // --- (per frame info...) ---
        float deltaTime;	        // Time between current frame and last frame
        float lastFrameT = 0.0f;    // Time of last frame

        int currentKeyFState = WindowManager.getKeyState(GLFW_KEY_F); // get current state of F key (for flashlight)

        // --- repeat while GLFW isn't instructed to close ---
        while(!WindowManager.windowShouldClose()){
            // --- per-frame time logic ---
            float currentFrameT = (float) glfwGetTime();
            deltaTime = currentFrameT - lastFrameT;
            lastFrameT = currentFrameT;

            // --- process keyboard arrows input --
            processAWSDInput(deltaTime);
            currentKeyFState = processFlashLightToggle(flashLight, currentKeyFState);

            // --- clear screen ---
            WindowManager.clearScreen();

            // --- render commands ---

            Matrix4f view = camera.calcLookAt(); // calc view matrix
            Matrix4f projection = new Matrix4f(); // create projection matrix
            projection.setPerspective((float) Math.toRadians(camera.getFOV()), (float) SCR_WIDTH / SCR_HEIGHT, 0.1f, 100.0f);

            RenderContext.setContext(view, projection, camera.getCameraPos(), camera.getCameraFront());

            entityRenderer.render(scene);
            lightSourceRenderer.render(scene);

            // --- check events & swap buffers ---
            WindowManager.updateWindow();
            glfwPollEvents();           // checks if any events are triggered, updates window state, & calls corresponding funcs
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);    // unbind any VBO
        glBindVertexArray(0);                       // unbind any VAO
    }

    /**
     * Called in render loop to continually process input from keyboard AWSD keys in each frame.
     */
    private void processAWSDInput(float deltaTime){
        // camera movement using AWSD
        if (WindowManager.getKeyState(GLFW_KEY_W) == GLFW_PRESS) camera.processKeyboardInput(CameraMovement.FORWARD, deltaTime);
        if (WindowManager.getKeyState(GLFW_KEY_S) == GLFW_PRESS) camera.processKeyboardInput(CameraMovement.BACKWARD, deltaTime);
        if (WindowManager.getKeyState(GLFW_KEY_A) == GLFW_PRESS) camera.processKeyboardInput(CameraMovement.LEFT, deltaTime);
        if (WindowManager.getKeyState(GLFW_KEY_D) == GLFW_PRESS) camera.processKeyboardInput(CameraMovement.RIGHT, deltaTime);
        if (WindowManager.getKeyState(GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) camera.processKeyboardInput(CameraMovement.DOWNWARD, deltaTime);
        if (WindowManager.getKeyState(GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) camera.processKeyboardInput(CameraMovement.UPWARD, deltaTime);
    }

    /**
     * Process keyboard input (press of key F) to toggle the flashlight
     * @param flashLight the flashlight to toggle ON/OFF
     * @return the new GLFW state of the F key
     */
    public int processFlashLightToggle(FlashLight flashLight, int currentFKeyState){
        int newKeyState = WindowManager.getKeyState(GLFW_KEY_F);
        if (currentFKeyState == GLFW_PRESS && newKeyState == GLFW_RELEASE) flashLight.toggle();
        return  newKeyState;
    }

    /**
     * Terminate GLFW & window
     */
    public void terminate(){

        WindowManager.closeWindow();

        // de-allocate all resources
        scene.deallocateMeshResources();
        cubeShaderProgram.delete();
        lightShaderProgram.delete();

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
