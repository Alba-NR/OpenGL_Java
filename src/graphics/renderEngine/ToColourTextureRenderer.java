package graphics.renderEngine;

import graphics.core.WindowManager;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * For rendering the scene to a colour texture, instead of rendering it to the default framebuffer (and hence the screen).
 *
 * Prepares the framebuffer to which the scene will be rendered -- Note! the scene is actually rendered by using the
 * appropriate renderers for the scene.
 *
 * In order for the scene to be rendered to the colour buffer of the FBO created, the fbo must be binded before
 * making any render calls, by calling bindFBOtoUse().
 */
public class ToColourTextureRenderer {
    private int fbo;
    private int colourTex;

    public ToColourTextureRenderer() {
    }

    public void prepare() {
        // create fbo
        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        // generate texture to use as colour buffer
        colourTex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, colourTex);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, WindowManager.getScrWidth(), WindowManager.getScrHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);    // unbind tex

        // attach colour tex as colour attachment of currently bound fbo
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colourTex, 0);

        // generate render buffer object for depth & stencil buffers
        int rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, WindowManager.getScrWidth(), WindowManager.getScrHeight());
        glBindRenderbuffer(GL_RENDERBUFFER, 0); // unbind rbo

        // attach rbo as depth & stencil attachment og fbo
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);

        // check if fbo is complete
        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("Framebuffer is not complete.");

        glBindFramebuffer(GL_FRAMEBUFFER, 0);   // unbind framebuffer
    }

    /**
     * Binds the FBO set up in prepare() (fbo to which to render...)
     */
    public void bindFBOtoUse(){
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    }

    public int getColourTex(){
        return colourTex;
    }
}
