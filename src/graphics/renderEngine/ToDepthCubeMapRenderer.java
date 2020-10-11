package graphics.renderEngine;

import graphics.core.WindowManager;
import graphics.scene.Entity;
import graphics.scene.Scene;
import graphics.shaders.ShaderProgram;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * For rendering the scene to a depth cubemap, rendering it from the 1st point light's perspective.
 */
public class ToDepthCubeMapRenderer extends Renderer {
    private int fbo;
    private int cubemap;
    private int shadowMapWidth, shadowMapHeight;

    public ToDepthCubeMapRenderer(ShaderProgram shaderToUse, int shadowMapWidth, int shadowMapHeight) {
        super(shaderToUse);
        this.shadowMapWidth = shadowMapWidth;
        this.shadowMapHeight = shadowMapHeight;
    }

    /**
     * Prepares the framebuffer to which the scene will be rendered when render() is called.
     * @param scene not used in this method (required for extending Renderer)
     */
    @Override
    public void prepare(Scene scene) {
        // create fbo
        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        // generate cubemap
        cubemap = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubemap);
        // assign each of the 6 cubemap faces a depth values tex
        for (int i = 0; i < 6; i++)  glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_DEPTH_COMPONENT, shadowMapWidth, shadowMapHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
        // set tex params
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);    // unbind cubemap

        // attach cubemap as depth attachment of currently bound fbo
        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, cubemap, 0);

        // set read & write buffers to GL_NONE (to explicitly tell OpenGL no colour data is to be rendered)
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        // check if fbo is complete
        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("Framebuffer is not complete.");

        glBindFramebuffer(GL_FRAMEBUFFER, 0);   // unbind framebuffer

        // upload light position to shader
        shaderProgram.use();
        shaderProgram.uploadVec3f("lightPos", scene.getPointLights().get(0).getPosition());
    }

    @Override
    public void render(Scene scene) {

        shaderProgram.use();

        glViewport(0, 0, shadowMapWidth, shadowMapHeight);
        bindFBOtoUse();
        WindowManager.clearDepthBuffer();

        for(int i = 0; i < 6; i++) shaderProgram.uploadMatrix4f("shadowMatrices[" + i + "]", RenderContext.getPointLightSpaceMatricesList().get(i));

        // render components
        for(Entity component : scene.getComponents()) component.renderToDepthMap(shaderProgram);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);   // unbind fbo
        glViewport(0, 0, WindowManager.getScrWidth(), WindowManager.getScrHeight());    // reset OpenGL viewport
    }

    /**
     * Binds the FBO set up in prepare() (fbo to which to render...)
     */
    private void bindFBOtoUse(){
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    }

    public int getDepthCubeMap(){
        return cubemap;
    }

    public int getShadowMapWidth() {
        return shadowMapWidth;
    }

    public int getShadowMapHeight() {
        return shadowMapHeight;
    }
}
