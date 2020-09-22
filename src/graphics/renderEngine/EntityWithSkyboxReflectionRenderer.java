package graphics.renderEngine;

import graphics.lights.FlashLight;
import graphics.lights.PointLight;
import graphics.scene.Entity;
import graphics.scene.Scene;
import graphics.shaders.ShaderProgram;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;

public class EntityWithSkyboxReflectionRenderer extends Renderer{

    public EntityWithSkyboxReflectionRenderer(ShaderProgram phongShaderToUse) {
        super(phongShaderToUse);
    }

    @Override
    public void prepare(Scene scene) {
    }


    @Override
    public void render(Scene scene) {
        shaderProgram.use();
        shaderProgram.uploadVec3f("wc_cameraPos", RenderContext.getCameraPos());

        // if scene uses skybox, bind skybox texture
        if(scene.getSkybox() != null) glBindTexture(GL_TEXTURE_CUBE_MAP, scene.getSkybox().getCubeMapTexture().getHandle());

        // render components
        for(Entity component : scene.getComponents()) component.render(shaderProgram);
    }
}
