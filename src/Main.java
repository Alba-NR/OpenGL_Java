
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
    private ShaderProgram cubeShaderProgram;  // shader prog to use for cubes
    private ShaderProgram lightShaderProgram;  // shader prog to use for light cube
    private CubeMesh cubeMesh;  // cube mesh

    final private int SCR_WIDTH = 1200;  // screen size settings
    final private int SCR_HEIGHT = 900;

    private Camera camera = new Camera();   // camera & mouse
    private double lastX = SCR_WIDTH / 2.0f, lastY = SCR_HEIGHT / 2.0f;
    private boolean firstMouse = true;


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
            if (key == GLFW_KEY_F) { // view in wireframe mode whilst F is pressed
                if ( action == GLFW_PRESS ) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                else if ( action == GLFW_RELEASE ) glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            }
            // AWSD used to move camera (in processArrowsInput() method)
        });
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED); // use mouse
        glfwSetCursorPosCallback(window, (long window, double xpos, double ypos) -> {
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
        glfwSetScrollCallback(window, (long window, double xoffset, double yoffset) -> { // 'zoom' illusion when scroll w/mouse
            camera.processMouseScroll(yoffset);
        });

        // make window visible
        glfwShowWindow(window);

        // --- set up shaders ---

        // create cube vertex shader
        Shader cubeVertexShader = new Shader(GL_VERTEX_SHADER, "./resources/cube_vertex_shader.glsl");
        // create cube fragment shader
        Shader cubeFragmentShader = new Shader(GL_FRAGMENT_SHADER, "./resources/cube_fragment_shader.glsl");
        // create cube shader program
        cubeShaderProgram = new ShaderProgram(cubeVertexShader, cubeFragmentShader);

        // create light cube vertex shader
        Shader lightVertexShader = new Shader(GL_VERTEX_SHADER, "./resources/lightSource_vertex_shader.glsl");
        // create light cube fragment shader
        Shader lightFragmentShader = new Shader(GL_FRAGMENT_SHADER, "./resources/lightSource_fragment_shader.glsl");
        // create light cube shader program
        lightShaderProgram = new ShaderProgram(lightVertexShader, lightFragmentShader);


        // --- set up vertex data & buffers, config cube's VAO & VBO and link vertex attributes USING CUBEMESH---

        cubeMesh = new CubeMesh();
        cubeShaderProgram.bindDataToShader(0, cubeMesh.getVertexVBOHandle(), 3);
        cubeShaderProgram.bindDataToShader(1, cubeMesh.getNormalHandle(), 3);
        cubeShaderProgram.bindDataToShader(2, cubeMesh.getTexHandle(), 2);

        // --- config light's VAO & VBO (vbo same bc light is a cube atm) ---
        // Note: rendering a cube to repr the light source, to explicitly see it's position in the scene
        lightShaderProgram.bindDataToShader(0, cubeMesh.getVertexVBOHandle(), 3);

        // --- unbind...
        glBindBuffer(GL_ARRAY_BUFFER, 0);    // unbind VBO
        glBindVertexArray(0);                       // unbind VAO
    }

    /**
     * Rendering loop
     */
    public void renderLoop(){
        Vector3f bgColour = new Vector3f(0.2f, 0.2f, 0.2f);
        cubeShaderProgram.use();    // set shader program to use

        // set-up lights

        // directional light
        cubeShaderProgram.uploadVec3f("dirLight.direction", -0.2f, -1.0f, -0.3f);
        cubeShaderProgram.uploadVec3f("dirLight.diffuse", 0.4f, 0.4f, 0.4f);
        cubeShaderProgram.uploadVec3f("dirLight.specular", 0.5f, 0.5f, 0.5f);

        // flashlight spotlight
        cubeShaderProgram.uploadVec3f("spotLight.ambient",  0.05f, 0.05f, 0.05f);
        cubeShaderProgram.uploadVec3f("spotLight.diffuse",  0.8f, 0.8f, 0.8f);
        cubeShaderProgram.uploadVec3f("spotLight.specular",  1.0f, 1.0f, 1.0f);
        cubeShaderProgram.uploadFloat("spotLight.constant",  1.0f); // set constants for attenuation
        cubeShaderProgram.uploadFloat("spotLight.linear",    0.07f);
        cubeShaderProgram.uploadFloat("spotLight.quadratic", 0.017f);
        cubeShaderProgram.uploadFloat("spotLight.cutoffCosine", (float) Math.cos(Math.toRadians(12.5)));
        cubeShaderProgram.uploadFloat("spotLight.outerCutoffCosine", (float) Math.cos(Math.toRadians(17.5)));

        // point lights
        Vector3f[] pointLightPositions = {
                new Vector3f( 0.7f,  0.2f,  2.0f),
                new Vector3f( 2.3f, -3.3f, -4.0f),
                new Vector3f(-4.0f,  2.0f, -12.0f)
        };

        // point light 1
        cubeShaderProgram.uploadVec3f("pointLights[0].position", pointLightPositions[0]);
        cubeShaderProgram.uploadVec3f("pointLights[0].ambient",  0.05f, 0.05f, 0.05f);
        cubeShaderProgram.uploadVec3f("pointLights[0].diffuse",  0.8f, 0.8f, 0.8f);
        cubeShaderProgram.uploadVec3f("pointLights[0].specular",  1.0f, 1.0f, 1.0f);
        cubeShaderProgram.uploadFloat("pointLights[0].constant",  1.0f); // set constants for attenuation
        cubeShaderProgram.uploadFloat("pointLights[0].linear",    0.14f);
        cubeShaderProgram.uploadFloat("pointLights[0].quadratic", 0.07f);

        // point light 2
        cubeShaderProgram.uploadVec3f("pointLights[1].position", pointLightPositions[1]);
        cubeShaderProgram.uploadVec3f("pointLights[1].ambient",  0.05f, 0.05f, 0.05f);
        cubeShaderProgram.uploadVec3f("pointLights[1].diffuse",  0.8f, 0.8f, 0.8f);
        cubeShaderProgram.uploadVec3f("pointLights[1].specular",  1.0f, 1.0f, 1.0f);
        cubeShaderProgram.uploadFloat("pointLights[1].constant",  1.0f); // set constants for attenuation
        cubeShaderProgram.uploadFloat("pointLights[1].linear",    0.09f);
        cubeShaderProgram.uploadFloat("pointLights[1].quadratic", 0.032f);

        // point light 3
        cubeShaderProgram.uploadVec3f("pointLights[2].position", pointLightPositions[2]);
        cubeShaderProgram.uploadVec3f("pointLights[2].ambient",  0.05f, 0.05f, 0.05f);
        cubeShaderProgram.uploadVec3f("pointLights[2].diffuse",  0.8f, 0.8f, 0.8f);
        cubeShaderProgram.uploadVec3f("pointLights[2].specular",  1.0f, 1.0f, 1.0f);
        cubeShaderProgram.uploadFloat("pointLights[2].constant",  1.0f); // set constants for attenuation
        cubeShaderProgram.uploadFloat("pointLights[2].linear",    0.14f);
        cubeShaderProgram.uploadFloat("pointLights[2].quadratic", 0.07f);


        // set-up cube material
        Texture diffuseMap = new Texture("./resources/container2.png", false);
        Texture specularMap = new Texture("./resources/container2_specular.png", false);
        cubeShaderProgram.uploadInt("material.diffuseColour", 0);   // diffuse map is at texture unit 0
        cubeShaderProgram.uploadInt("material.specularColour", 1);  // specular map is at texture unit 1
        cubeShaderProgram.uploadFloat("material.shininess", 64.0f);

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

            // --- process keyboard arrows input --
            processAWSDInput(deltaTime);

            // --- clear screen ---
            glClearColor(bgColour.x, bgColour.y, bgColour.z, 1.0f); // specify colour to clear to
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear screen's color buffer & depth buffer

            // --- render commands ---
            cubeShaderProgram.use();    // use cube shader
            cubeShaderProgram.uploadVec3f("wc_cameraPos", camera.getCameraPos());
            cubeShaderProgram.uploadVec3f("spotLight.position",  camera.getCameraPos());    // for spotlight
            cubeShaderProgram.uploadVec3f("spotLight.direction",  camera.getCameraFront());

            // textures
            glActiveTexture(GL_TEXTURE0);       // bind texture to texture unit 0
            glBindTexture(GL_TEXTURE_2D, diffuseMap.getHandle());
            glActiveTexture(GL_TEXTURE1);       // bind texture to texture unit 1
            glBindTexture(GL_TEXTURE_2D, specularMap.getHandle());

            // draw/render
            glBindVertexArray(cubeMesh.getVAOHandle());     // bind vertex attrib buffer

             // calc view matrix
            Matrix4f view = camera.calcLookAt();

            // create & upload projection matrix
            Matrix4f projection = new Matrix4f();
            projection.setPerspective((float) Math.toRadians(camera.getFOV()), (float) SCR_WIDTH / SCR_HEIGHT, 0.1f, 100.0f);

            for(int i = 0; i < cubePositions.length; i++){
                Matrix4f model = new Matrix4f();  // calc model matrix
                model.translate(cubePositions[i]);
                model.rotate((float) Math.toRadians(20.0f * i), (new Vector3f(1.0f, 0.3f, 0.5f)).normalize());
                cubeShaderProgram.uploadMatrix4f("model_m", model);

                Matrix4f mvp =  new Matrix4f(projection);   // calc MVP matrix (once in CPU rather than per fragment in GPU...)
                mvp.mul(view).mul(model);
                cubeShaderProgram.uploadMatrix4f("mvp_m", mvp);

                Matrix4f normalM = new Matrix4f();  // calc matrix to tranform normal vect from oc to wc
                model.invert(normalM).transpose();
                cubeShaderProgram.uploadMatrix4f("normal_m", normalM);

                glDrawElements(GL_TRIANGLES, cubeMesh.getNumOfTriangles(), GL_UNSIGNED_INT, 0); // draw it as triangles
            }
            glBindVertexArray(0);       // remove the binding

            // render light cube objects for point lights
            glBindVertexArray(cubeMesh.getVAOHandle());
            lightShaderProgram.use();
            lightShaderProgram.uploadVec3f("lightColour",  1.0f, 1.0f, 1.0f);

            for (Vector3f pointLightPosition : pointLightPositions) {
                Matrix4f lightModel = new Matrix4f();   // calc model matrix
                lightModel.translate(pointLightPosition);
                lightModel.scale(new Vector3f(0.2f));

                Matrix4f mvp = new Matrix4f(projection);   // calc MVP matrix
                mvp.mul(view).mul(lightModel);
                lightShaderProgram.uploadMatrix4f("mvp_m", mvp);

                glDrawElements(GL_TRIANGLES, cubeMesh.getNumOfTriangles(), GL_UNSIGNED_INT, 0);
            }
            glBindVertexArray(0);       // remove the binding

            // --- check events & swap buffers ---
            glfwSwapBuffers(window);    // swap back & front buffers
            glfwPollEvents();           // checks if any events are triggered, updates window state, & calls corresponding funcs
        }
    }

    /**
     * Called in render loop to continually process input from keyboard AWSD keys in each frame.
     */
    private void processAWSDInput(float deltaTime){
        // camera movement using AWSD
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) camera.processKeyboardInput(CameraMovement.FORWARD, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) camera.processKeyboardInput(CameraMovement.BACKWARD, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) camera.processKeyboardInput(CameraMovement.LEFT, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) camera.processKeyboardInput(CameraMovement.RIGHT, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) camera.processKeyboardInput(CameraMovement.DOWNWARD, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) camera.processKeyboardInput(CameraMovement.UPWARD, deltaTime);
    }

    /**
     * Terminate GLFW & window
     */
    public void terminate(){

        // free the window callbacks & destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // de-allocate all resources
        cubeMesh.deallocateResources();
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
