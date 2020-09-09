package graphics.lights;


import graphics.shaders.ShaderProgram;

/**
 * Interface to represent a light source in the scene.
 */
public interface LightSource {
    /**
     * Upload this source light's info/specification into the given
     * uniform variable in the specified shader program
     * @param shader {@link ShaderProgram} to which to upload light specifications
     * @param uniformName {@link String} name of target uniform variable
     */
    void uploadSpecsToShader(ShaderProgram shader, String uniformName);
}