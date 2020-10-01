package graphics.renderEngine;

import graphics.shaders.ShaderProgram;
import graphics.shapes.ScreenQuad;

import static org.lwjgl.opengl.GL30.glBindVertexArray;

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

    public void render() {
        shaderProgram.use();

        quad.bindTexture();
        quad.getMesh().render();
    }
}
