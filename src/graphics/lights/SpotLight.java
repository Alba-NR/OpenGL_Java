package graphics.lights;

import graphics.shaders.ShaderProgram;
import org.joml.Vector3f;

/**
 * Represents a spotlight, which has:
 *      - a direction
 *      - an inner cut-off cosine
 *      - an outer cut-off cosine
 */
public class SpotLight extends PointLight implements LightSource {

    private Vector3f direction;
    private float cutoffCosine;
    private float outerCutoffCosine;

    public SpotLight(Vector3f position, Vector3f colour, float strength, Vector3f direction, float atten_const, float atten_linear, float atten_quadr, float cutoffCosine, float outerCutoffCosine) {
        super(position, colour, strength, atten_const, atten_linear, atten_quadr);
        this.direction = direction;
        this.cutoffCosine = cutoffCosine;
        this.outerCutoffCosine = outerCutoffCosine;
    }

    @Override
    public void uploadSpecsToShader(ShaderProgram shader, String uniformName) {
        super.uploadSpecsToShader(shader, uniformName);
        shader.uploadVec3f(uniformName + ".direction", direction);
        shader.uploadFloat(uniformName + ".cutoffCosine", cutoffCosine);
        shader.uploadFloat(uniformName + ".outerCutoffCosine", outerCutoffCosine);
    }

    /**
     * Sets the spotlight's direction & uploads this new value to the given shader.
     * @param direction new light direction
     * @param shader {@link ShaderProgram} to which to upload direction
     * @param uniformName name of uniform in shader to which to upload direction
     */
    public void setAndUploadDirection(Vector3f direction, ShaderProgram shader, String uniformName){
        setDirection(direction);
        shader.uploadVec3f(uniformName + ".direction", direction);
    }

    /**
     * Sets the spotlight's position & uploads this new value to the given shader.
     * @param position new light position
     * @param shader {@link ShaderProgram} to which to upload position
     * @param uniformName name of uniform in shader to which to upload position
     */
    public void setAndUploadPosition(Vector3f position, ShaderProgram shader, String uniformName){
        setPosition(position);
        shader.uploadVec3f(uniformName + ".position", position);
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }
    public void setCutoffCosine(float cutoffCosine) {
        this.cutoffCosine = cutoffCosine;
    }
    public void setOuterCutoffCosine(float outerCutoffCosine) {
        this.outerCutoffCosine = outerCutoffCosine;
    }
}