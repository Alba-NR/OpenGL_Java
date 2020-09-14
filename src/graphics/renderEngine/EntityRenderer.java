package graphics.renderEngine;

import graphics.lights.FlashLight;
import graphics.lights.PointLight;
import graphics.scene.Entity;
import graphics.scene.Scene;
import graphics.shaders.ShaderProgram;

public class EntityRenderer extends Renderer{

    public EntityRenderer(ShaderProgram phongShaderToUse) {
        super(phongShaderToUse);
    }

    @Override
    public void prepare(Scene scene) {
        shaderProgram.use();

        shaderProgram.uploadVec3f("I_a", scene.getI_a()); // set ambient illumination intensity

        scene.getDirLight().uploadSpecsToShader(shaderProgram, "dirLight");
        scene.getFlashLight().uploadSpecsToShader(shaderProgram, "spotLight"); // currently only spotlight is the flashlight

        int i = 0;
        for(PointLight pointLight : scene.getPointLights()){
            pointLight.uploadSpecsToShader(shaderProgram, "pointLights[" + i + "]");
            i++;
        }
    }


    @Override
    public void render(Scene scene) {
        shaderProgram.use();

        // update flashlight info in shader
        updateFlashlightInShader(scene.getFlashLight());

        // render components
        for(Entity component : scene.getComponents()) component.render(shaderProgram);
    }

    private void updateFlashlightInShader(FlashLight flashLight){
        if(flashLight.getState()){ // if flashlight is ON
            flashLight.setAndUploadPosition(RenderContext.getCameraPos(), shaderProgram, "spotLight");
            flashLight.setAndUploadDirection(RenderContext.getCameraFront(), shaderProgram, "spotLight");
            shaderProgram.uploadInt("flashLightIsON", 1);
        } else shaderProgram.uploadInt("flashLightIsON", 0);    // flashlight is OFF
    }
}
