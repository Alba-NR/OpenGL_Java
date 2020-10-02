package graphics.renderEngine;

import graphics.renderEngine.postProcessing.EffectsManager;
import graphics.renderEngine.postProcessing.PostProcessingEffect;
import graphics.shaders.ShaderProgram;
import graphics.shapes.ScreenQuad;

import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * Renderer for rendering a quad/square {@link ScreenQuad} of the size of the screen.
 * (and so which covers the entire screen... in normalised device coords...)
 */
public class ScreenQuadRenderer {
    private ShaderProgram shaderProgram;    // shader to use for rendering
    private ScreenQuad quad;

    public ScreenQuadRenderer(ShaderProgram shaderToUse) {
        shaderProgram = shaderToUse;
    }

    /**
     * Prepare {@link ShaderProgram} shaderProgram by binding
     * {@link ScreenQuad} quad mesh's attributes to it & uploading tex handle to uniform.
     */
    public void prepare(ScreenQuad screenQuad) {
        this.quad = screenQuad;
        shaderProgram.use();

        // bind mesh data to shader
        glBindVertexArray(quad.getMesh().getVAOHandle());
        shaderProgram.bindDataToShader(0, quad.getMesh().getVertexVBOHandle(), 2);
        shaderProgram.bindDataToShader(1, quad.getMesh().getTexHandle(), 2);

        shaderProgram.uploadInt("screenTexture", 0); // tex at texture unit 0
    }

    /**
     * Render the quad using the {@link ShaderProgram} associated w/the renderer.
     */
    public void render() {
        shaderProgram.use();

        PostProcessingEffect effect = RenderContext.getPostProcessingEffect();
        // upload effect data to shader
        int effectID = EffectsManager.getEffectIntID(effect);
        int intValueToUpload;
        if(effectID < 3) intValueToUpload = effectID;
        else{
            intValueToUpload = 3;
            shaderProgram.uploadFloatArray("kernel3x3", effect.getKernel());
        }
        shaderProgram.uploadInt("effectToUse", intValueToUpload);

        quad.bindTexture();
        quad.getMesh().render();
    }
}
