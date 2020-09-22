package graphics.renderEngine;

import graphics.scene.Scene;
import graphics.shaders.ShaderProgram;
import graphics.shapes.CubeMapCube;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class SkyboxRenderer extends Renderer {

    public SkyboxRenderer(ShaderProgram skyboxShaderToUse) {
        super(skyboxShaderToUse);
    }

    @Override
    public void prepare(Scene scene) {
    }

    @Override
    public void render(Scene scene) {
        CubeMapCube skybox = scene.getSkybox();
        if(skybox == null) return;

        glDepthFunc(GL_LEQUAL); // depth test passes when values are <= depth buffer's content

        shaderProgram.use();
        glBindTexture(GL_TEXTURE_CUBE_MAP, skybox.getCubeMapTexture().getHandle());

        glBindVertexArray(skybox.getMesh().getVAOHandle());
        // bind vertex data to shader
        shaderProgram.bindDataToShader(0, skybox.getMesh().getVertexVBOHandle(), 3);

        // calc VP matrix & upload it to shader
        Matrix4f vp =  new Matrix4f(RenderContext.getProjMatrix());
        Matrix3f view3x3submatrix =  new Matrix3f();
        RenderContext.getViewMatrix().get3x3(view3x3submatrix);
        vp.mul(new Matrix4f(view3x3submatrix));
        shaderProgram.uploadMatrix4f("viewProjection_m", vp);

        // bind texture
        skybox.bindTexture();

        skybox.getMesh().render(); // render skybox
        glDepthFunc(GL_LESS);
    }
}
