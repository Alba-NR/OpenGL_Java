package graphics.renderEngine;

import graphics.scene.Scene;
import graphics.shaders.ShaderProgram;

public abstract class Renderer {
    ShaderProgram shaderProgram;    // shader to use for rendering

    Renderer(ShaderProgram shaderToUse){
        shaderProgram = shaderToUse;
    }

    /**
     * Prepare {@link ShaderProgram} shaderProgram by uploading {@link Scene} scene light data.
     */
    public abstract void prepare(Scene scene);

    /**
     * Renders given {@link Scene} using the {@link ShaderProgram} shaderProgram field
     * Must have called prepare() at least once before.
     * @param scene {@link Scene} to render
     */
    public abstract void render(Scene scene);
}
