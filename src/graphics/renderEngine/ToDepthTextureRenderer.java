package graphics.renderEngine;

import graphics.core.WindowManager;
import graphics.scene.Entity;
import graphics.scene.Scene;
import graphics.shaders.ShaderProgram;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * For rendering the scene to a depth texture, rendering it from the directional light's perspective.
 *
 *
 *
 */
public class ToDepthTextureRenderer extends Renderer {
    private int fbo;
    private int depthTex;
    private int shadowMapWidth, shadowMapHeight;

    public ToDepthTextureRenderer(ShaderProgram shaderToUse, int shadowMapWidth, int shadowMapHeight) {
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

        // generate depth map texture
        depthTex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTex);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, shadowMapWidth, shadowMapHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        glBindTexture(GL_TEXTURE_2D, 0);    // unbind tex

        // attach depth map tex as depth attachment of currently bound fbo
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTex, 0);

        // set read & write buffers to GL_NONE (to explicitly tell OpenGL no colour data is to be rendered)
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        // check if fbo is complete
        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("Framebuffer is not complete.");

        glBindFramebuffer(GL_FRAMEBUFFER, 0);   // unbind framebuffer
    }

    @Override
    public void render(Scene scene) {
        glCullFace(GL_FRONT);   // to avoid peter-panning shadow artifact

        shaderProgram.use();

        shaderProgram.uploadMatrix4f("lightSpace_m", RenderContext.getDirLightSpaceMatrix());

        glViewport(0, 0, shadowMapWidth, shadowMapHeight);
        bindFBOtoUse();
        WindowManager.clearDepthBuffer();

        // render components
        for(Entity component : scene.getComponents()) component.renderToDepthMap(shaderProgram);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);   // unbind fbo
        glViewport(0, 0, WindowManager.getScrWidth(), WindowManager.getScrHeight());    // reset OpenGL viewport
        glCullFace(GL_BACK);    // reset cull faces to back-facing faces
    }

    /**
     * Binds the FBO set up in prepare() (fbo to which to render...)
     */
    private void bindFBOtoUse(){
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    }

    public int getDepthTex(){
        return depthTex;
    }

    public int getShadowMapWidth() {
        return shadowMapWidth;
    }

    public int getShadowMapHeight() {
        return shadowMapHeight;
    }
}
