package main;

import graphics.camera.Camera;
import graphics.camera.CameraMovement;
import graphics.core.WindowManager;
import graphics.lights.DirLight;
import graphics.lights.FlashLight;
import graphics.lights.PointLight;
import graphics.materials.Material;
import graphics.materials.ReflectiveMaterial;
import graphics.materials.RefractiveMaterial;
import graphics.renderEngine.*;
import graphics.renderEngine.postProcessing.EffectsManager;
import graphics.scene.DrawableEntity;
import graphics.scene.Entity;
import graphics.scene.Scene;
import graphics.shapes.*;
import graphics.shaders.Shader;
import graphics.shaders.ShaderProgram;
import graphics.textures.CubeMapTexture;
import graphics.textures.Texture;
import graphics.textures.TextureType;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

/**
 * The main app program.
 */
class OpenGLApp {

    private ShaderProgram phongShaderProgram;    // phong shader program using diff & spec textures or colours
    private ShaderProgram lightShaderProgram;           // shader prog to use for light cubes
    private ShaderProgram skyboxShaderProgram;          // shader prog to use for skybox
    private ShaderProgram quadShaderProgram;            // shader prog to use for quad
    private ShaderProgram toDepthTexShaderProgram;      // shader prog to use for rendering to depth texture
    private ShaderProgram toDepthCubeMapShaderProgram;      // shader prog to use for rendering to depth texture
    private Scene scene;                                // scene to render
    private ScreenQuad screenQuad;                      // quad filling entire screen (scene displayed as it's colour texture...)

    final private int SCR_WIDTH = WindowManager.getScrWidth();  // screen size settings
    final private int SCR_HEIGHT = WindowManager.getScrHeight();

    private Camera camera = new Camera();   // camera & mouse
    private double lastX = SCR_WIDTH / 2.0f, lastY = SCR_HEIGHT / 2.0f;
    private boolean firstMouse = true;


    /**
     * Initialise GLFW & window for rendering
     */
    void init() {
        // --- init & config GLFW ---
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // --- GLFW window creation (& init GLFW context)---
        WindowManager.createWindow();

        glEnable(GL_MULTISAMPLE);   // enable MSAA
        glEnable(GL_DEPTH_TEST);    // enable depth testing
        glEnable(GL_CULL_FACE);     // enable culling
        glCullFace(GL_BACK);        // cull back faces
        glFrontFace(GL_CCW);        // initially set front faces as those w/counter clockwise winding

        // --- callback functions registered after window is created & before render loop is init ---
        setCallbacks();

        // make window visible
        WindowManager.makeWindowVisible();

        // --- set up shaders ---
        setUpShaders();
    }

    /**
     * Create any shaders here.
     */
    private void setUpShaders() {
        // create (blinn-)phong shaders
        Shader phong_vs = new Shader(GL_VERTEX_SHADER, "./resources/shaders/phong_shadowMaps_vs.glsl");
        Shader phong_fs = new Shader(GL_FRAGMENT_SHADER, "./resources/shaders/blinnPhong_wReflectionRefraction_ALLshadowMaps_fs.glsl");
        phongShaderProgram = new ShaderProgram(phong_vs, phong_fs);

        // create light cube shaders
        Shader light_vs = new Shader(GL_VERTEX_SHADER, "./resources/shaders/lightSource_vs.glsl");
        Shader light_fs = new Shader(GL_FRAGMENT_SHADER, "./resources/shaders/lightSource_fs.glsl");
        lightShaderProgram = new ShaderProgram(light_vs, light_fs);

        // create skybox shaders
        Shader skybox_vs = new Shader(GL_VERTEX_SHADER, "./resources/shaders/skybox_vs.glsl");
        Shader skybox_fs = new Shader(GL_FRAGMENT_SHADER, "./resources/shaders/skybox_fs.glsl");
        skyboxShaderProgram = new ShaderProgram(skybox_vs, skybox_fs);

        // create quad shaders
        Shader quad_vs = new Shader(GL_VERTEX_SHADER, "./resources/shaders/quad_vs.glsl");
        Shader quad_fs = new Shader(GL_FRAGMENT_SHADER, "./resources/shaders/quad_fs.glsl");
        quadShaderProgram = new ShaderProgram(quad_vs, quad_fs);

        // create to depth texture shaders
        Shader toDepthMap_vs = new Shader(GL_VERTEX_SHADER, "./resources/shaders/toDepthMap_vs.glsl");
        Shader toDepthMap_fs = new Shader(GL_FRAGMENT_SHADER, "./resources/shaders/toDepthMap_fs.glsl");
        toDepthTexShaderProgram = new ShaderProgram(toDepthMap_vs, toDepthMap_fs);

        // create to depth cubemap shaders
        Shader toDepthCubeMap_vs = new Shader(GL_VERTEX_SHADER, "./resources/shaders/toDepthCubeMap_vs.glsl");
        Shader toDepthCubeMap_fs = new Shader(GL_FRAGMENT_SHADER, "./resources/shaders/toDepthCubeMap_fs.glsl");
        Shader toDepthCubeMap_gs = new Shader(GL_GEOMETRY_SHADER, "./resources/shaders/toDepthCubeMap_gs.glsl");
        toDepthCubeMapShaderProgram = new ShaderProgram(toDepthCubeMap_vs, toDepthCubeMap_fs, toDepthCubeMap_gs);
    }

    /**
     * Set-up the scene to render here.
     */
    private void setUpScene() {
        // --- SET UP SKYBOX ---
        String filepath = "./resources/textures/yokohama_skybox/";
        String[] facesFileNames = new String[]{
                filepath +  "right.jpg",
                filepath +  "left.jpg",
                filepath +  "top.jpg",
                filepath +  "bottom.jpg",
                filepath +  "front.jpg",
                filepath +  "back.jpg"
        };
        CubeMapTexture cubeMapTexture = new CubeMapTexture(facesFileNames);
        CubeMapCube skybox = new CubeMapCube(cubeMapTexture);

        // --- SET UP LIGHTS ---

        // directional light
        DirLight dirLight = new DirLight(new Vector3f(1.0f, 1.0f, 1.0f), 2.0f, new Vector3f(-0.2f, -1.0f, -0.3f));

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
                10.0f,
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
        // WOODEN CUBES
        List<Texture> woodenCube_texList = Arrays.asList(
                new Texture("./resources/textures/container2.png", false, TextureType.DIFFUSE),
                new Texture("./resources/textures/container2_specular.png", false, TextureType.SPECULAR),
                new Texture("./resources/textures/container2_reflection2.png", false, TextureType.REFLECTION)
        );
        Material cubeMaterial = new ReflectiveMaterial(woodenCube_texList);
        cubeMaterial.setK_spec(0.5f);
        Shape cube = new Cube(cubeMaterial);

        // calc local transform matrix for cube 1
        Matrix4f cube1_local_transform = new Matrix4f();
        cube1_local_transform.translate(-2.0f, 0.0f, -2.0f)
                .rotate((float) Math.toRadians(45), 0.0f, 1.0f, 0.0f);

        // create 1st cube entity
        Entity cube1_entity = new DrawableEntity(null, cube1_local_transform, new Vector3f(2.0f), cube);

        // calc local transform for 2nd cube entity
        Matrix4f cube2_local_transform = new Matrix4f();
        cube2_local_transform.translate(0.0f, 0.75f, 0.0f)
                .rotate((float) Math.toRadians(30), 0.0f, 1.0f, 0.0f);

        // create 2nd cube entity, child of 1st cube entity
        Entity cube2_entity = new DrawableEntity(cube1_entity, cube2_local_transform, new Vector3f(0.5f), cube);
        cube1_entity.addChild(cube2_entity);

        // calc local transform for 3rd cube entity
        Matrix4f cube3_local_transform = new Matrix4f();
        cube3_local_transform.translate(2.0f, -0.2f, 0.0f)
                .rotate((float) Math.toRadians(60), 0.0f, 1.0f, 0.0f);

        // create 3rd cube entity, child of 1st cube entity
        Entity cube3_entity = new DrawableEntity(cube1_entity, cube3_local_transform, new Vector3f(0.6f), cube);
        cube1_entity.addChild(cube3_entity);

        // FLOOR PLANE
        //Shape square = new Square(new ReflectiveMaterial(0.2f, 0.8f, 0.01f, 4f, new Vector3f(51/255f, 56/255f, 62/255f), new Vector3f(1f)));
        Texture woodenFloorTex = new Texture("./resources/textures/floor.png", true, TextureType.DIFFUSE);
        Shape square = new Square(
                new Material(Arrays.asList(woodenFloorTex))
        );

        // calc local transform matrix for square
        Matrix4f floor_local_transform = new Matrix4f();
        floor_local_transform.translate(0f, -1.0f, 0f)
                .rotate((float) Math.toRadians(90), 1.0f, 0.0f, 0.0f);

        // create floor entity
        Entity floor = new DrawableEntity(null, floor_local_transform, new Vector3f(50), square);

        // DRAGON
        Shape dragonShape = new ShapeFromOBJ("./resources/models/dragon.obj",
                new RefractiveMaterial(
                        0.01f,
                        0.2f,
                        0.025f,
                        64f,
                        new Vector3f(1.0f, 51/255f, 51/255f),
                        new Vector3f(1.0f),
                        1f
                        ),
                true); // red glass dragon


        // calc local transform matrix for dragon
        Matrix4f dragon_local_transform = new Matrix4f();
        dragon_local_transform.translate(2f, -1.0f, 2f)
                .rotateAffine((float) -Math.toRadians(135), 0f, 1f, 0f);

        // create dragon entity
        Entity dragon = new DrawableEntity(null, dragon_local_transform, new Vector3f(0.25f), dragonShape);

        // GRASS
        Texture grassTex = new Texture("./resources/textures/grass.png", true, TextureType.DIFFUSE);
        grassTex.setTexWrapToClampToEdge();
        Material grassMaterial = new Material(0.5f, 0.99f, 0.01f, 64f, Collections.singletonList(grassTex));
        Shape grassShape = new Square(grassMaterial);

        List<Entity> grassEntities = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            Matrix4f grass_local_transform = new Matrix4f();
            grass_local_transform.translate(5.0f, -0.5f, -5.0f)
                                    .rotate((float) Math.toRadians(45) * i, 0, 1, 0);
            grassEntities.add(new DrawableEntity(null, grass_local_transform, new Vector3f(1), grassShape));
        }

        // --- COMPONENTS LIST: add entities to components list
        List<Entity> components = new ArrayList<>();
        components.addAll(Arrays.asList(cube1_entity, dragon, floor));
        components.addAll(grassEntities);

        // --- CREATE SCENE ---
        scene = new Scene(components, dirLight, flashLight, pointLightsList, ambientIntensity, skybox);

    }

    /**
     * Rendering loop
     */
    void renderLoop(){

        // --- create renderers ---
        Renderer entityRenderer;    // created after preparing toDepthTextureRenderer (bc uses depth tex handle)
        Renderer lightSourceRenderer = new PointLightRenderer(lightShaderProgram);
        Renderer skyboxRenderer = new SkyboxRenderer(skyboxShaderProgram);
        ScreenQuadRenderer screenQuadRenderer = new ScreenQuadRenderer(quadShaderProgram);
        ToColourTextureRenderer toColourTextureRenderer = new ToColourTextureRenderer();
        ToDepthTextureRenderer toDepthTextureRenderer = new ToDepthTextureRenderer(toDepthTexShaderProgram, 1024, 1024);
        ToDepthCubeMapRenderer toDepthCubeMapRenderer = new ToDepthCubeMapRenderer(toDepthCubeMapShaderProgram, 1024, 1024);

        // --------- SET UP SCENE ---------
        setUpScene();

        // --------- RENDER LOOP ---------

        //--- directional light's light space matrix, for shadow mapping ---
        Matrix4f dirLightProjection = new Matrix4f().ortho(-10.0f, 10.0f, -10.0f, 10.0f, 1.0f, 7.5f);
        Matrix4f dirLightView = new Matrix4f().lookAt(
                new Vector3f(scene.getDirLight().getDirection()).mul(-1),
                new Vector3f(0),
                new Vector3f(0.0f, 10.f, 0.0f)
        );
        Matrix4f dirLightSpaceMatrix = new Matrix4f(dirLightProjection);
        dirLightSpaceMatrix.mul(dirLightView);

        RenderContext.setDirLightSpaceMatrix(dirLightSpaceMatrix);

        //--- 1st point light's space matrices, for omnidirectional shadow mapping ---
        List<Matrix4f> pointLightSpaceMatrices = new ArrayList<>();
        float farPlane =  25.0f;
        toDepthCubeMapShaderProgram.use();
        toDepthCubeMapShaderProgram.uploadFloat("farPlane", farPlane); // todo: move to renderers themselves
        phongShaderProgram.use();
        phongShaderProgram.uploadFloat("farPlane", farPlane);
        Matrix4f pointLightProjection = new Matrix4f().perspective((float) Math.toRadians(90.0f), 1.0f, 1.0f, farPlane);
        List<Vector3f> firstVectList = Arrays.asList(
                new Vector3f(1.0f, 0.0f, 0.0f),
                new Vector3f(-1.0f, 0.0f, 0.0f),
                new Vector3f(0.0f, 1.0f, 0.0f),
                new Vector3f(0.0f, -1.0f, 0.0f),
                new Vector3f(0.0f, 0.0f, 1.0f),
                new Vector3f(0.0f, 0.0f, -1.0f)
        );
        List<Vector3f> secondVectList = Arrays.asList(
                new Vector3f(0.0f, -1.0f, 0.0f),
                new Vector3f(0.0f, -1.0f, 0.0f),
                new Vector3f(0.0f, 0.0f, 1.0f),
                new Vector3f(0.0f, 0.0f, -1.0f),
                new Vector3f(0.0f, -1.0f, 0.0f),
                new Vector3f(0.0f, -1.0f, 0.0f)
        );
        for(int i = 0; i < 6; i++){
            PointLight light = scene.getPointLights().get(0);
            Matrix4f lightView = new Matrix4f().lookAt(
                    new Vector3f(light.getPosition()),
                    new Vector3f(light.getPosition()).add(firstVectList.get(i)),
                    secondVectList.get(i)
            );
            Matrix4f lightSpaceMatrix = new Matrix4f(pointLightProjection);
            lightSpaceMatrix.mul(lightView);

            pointLightSpaceMatrices.add(lightSpaceMatrix);
        }

        RenderContext.setPointLightSpaceMatricesList(pointLightSpaceMatrices);


        // --- prepare renderers ---
        toDepthTextureRenderer.prepare(scene);
        toDepthCubeMapRenderer.prepare(scene);

        entityRenderer = new EntityPhongWAllShadowMapsRenderer(phongShaderProgram, toDepthTextureRenderer.getDepthTex(), toDepthCubeMapRenderer.getDepthCubeMap());
        entityRenderer.prepare(scene);

        lightSourceRenderer.prepare(scene);

        toColourTextureRenderer.prepare();
        screenQuad = new ScreenQuad(toColourTextureRenderer.getColourTex());
        screenQuadRenderer.prepare(screenQuad);


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
            currentKeyFState = processFlashLightToggle(scene.getFlashLight(), currentKeyFState);

            // --- clear screen ---
            WindowManager.clearScreen();

            //--- render to depth map ---
            toDepthTextureRenderer.render(scene);
            toDepthCubeMapRenderer.render(scene);

            // --- bind fbo to which to render ---
            toColourTextureRenderer.bindFBOtoUse();
            WindowManager.clearColourDepthBuffers();

            // --- render commands ---

            Matrix4f view = camera.calcLookAt(); // calc view matrix
            Matrix4f projection = new Matrix4f(); // create projection matrix
            projection.setPerspective((float) Math.toRadians(camera.getFOV()), (float) SCR_WIDTH / SCR_HEIGHT, 0.1f, 100.0f);

            RenderContext.setContext(view, projection, camera.getCameraPos(), camera.getCameraFront());

            entityRenderer.render(scene);
            lightSourceRenderer.render(scene);
            skyboxRenderer.render(scene);


            // bind default framebuffer & render quad
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glDisable(GL_DEPTH_TEST);       // so that screen-space quad isn't discarded bc of depth test
            // clear relevant buffers
            WindowManager.clearColour(1.0f, 1.0f, 1.0f); // optional, to correctly see quad in wireframe mode
            WindowManager.clearColourBuffer();

            screenQuadRenderer.render();    // render screen quad
            glEnable(GL_DEPTH_TEST);


            // --- check events & swap buffers ---
            WindowManager.updateWindow();
            glfwPollEvents(); // checks if any events are triggered, updates window state, & calls corresponding funcs
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);    // unbind any VBO
        glBindVertexArray(0);                       // unbind any VAO
    }

    /**
     * Set the window callbacks.
     */
    private void setCallbacks(){
        long win = WindowManager.getWindowHandle();

        // whenever window is resized, call given funct -- adjusts viewport
        glfwSetFramebufferSizeCallback(win, (long window, int width, int height) -> glViewport(0, 0, width, height));

        // whenever key is pressed, repeated or released.
        glfwSetKeyCallback(win, (window, key, scancode, action, mods) -> {
            // close window when esc key is released
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);
            // view in wireframe mode whilst E is pressed
            if (key == GLFW_KEY_E) {
                if (action == GLFW_PRESS) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                else if (action == GLFW_RELEASE) glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            }
            // -> AWSD used to move camera (in processArrowsInput() method)
            // number keys used to set post-processing effects
            for(int i = 0; i < EffectsManager.getNumOfEffects(); i++){
                if (key == GLFW_KEY_0 + i) RenderContext.setPostProcessingEffect(EffectsManager.getEffectByIntID(i));
            }
        });

        // mouse-related callbacks
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
    private int processFlashLightToggle(FlashLight flashLight, int currentFKeyState){
        int newKeyState = WindowManager.getKeyState(GLFW_KEY_F);
        if (currentFKeyState == GLFW_PRESS && newKeyState == GLFW_RELEASE) flashLight.toggle();
        return  newKeyState;
    }

    /**
     * Terminate GLFW & window
     */
    void terminate(){

        WindowManager.closeWindow();

        // de-allocate all resources
        scene.deallocateMeshResources();
        screenQuad.getMesh().deallocateResources();
        phongShaderProgram.delete();
        lightShaderProgram.delete();
        skyboxShaderProgram.delete();
        quadShaderProgram.delete();
        toDepthTexShaderProgram.delete();

        // clean/delete all other GLFW's resources
        glfwTerminate();
    }
}
