package graphics.renderEngine;

import graphics.lights.PointLight;
import graphics.scene.Scene;
import graphics.shaders.ShaderProgram;
import graphics.shapes.Cube;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class PointLightRenderer extends Renderer {
    private Cube cube;

    public PointLightRenderer(ShaderProgram shaderToUse) {
        super(shaderToUse);
    }

    @Override
    public void prepare(Scene scene) {
        shaderProgram.use();

        // bind mesh data
        cube = new Cube();
        glBindVertexArray(cube.getMesh().getVAOHandle());
        shaderProgram.bindDataToShader(0, cube.getMesh().getVertexVBOHandle(), 3);
    }

    @Override
    public void render(Scene scene) {
        shaderProgram.use();

        // render light cube objects for point lights

        for(PointLight pointLight : scene.getPointLights()) {
            // upload light colour
            shaderProgram.uploadVec3f("lightColour", pointLight.getColour());

            Matrix4f lightModel = new Matrix4f();   // calc model matrix
            lightModel.translate(pointLight.getPosition());
            lightModel.scale(0.2f);

            Matrix4f mvp = new Matrix4f(RenderContext.getProjMatrix());   // calc MVP matrix
            mvp.mul(RenderContext.getViewMatrix()).mul(lightModel);
            shaderProgram.uploadMatrix4f("mvp_m", mvp);

            cube.getMesh().render();    // render cube
        }
    }

}
