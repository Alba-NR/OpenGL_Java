import org.joml.Vector3f;

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

/**
 * Represents a point light.
 */
class PointLight implements LightSource {
    private Vector3f position;
    private Vector3f colour;
    private float strength;
    private float atten_const;
    private float atten_linear;
    private float atten_quadr;

    PointLight(Vector3f position, Vector3f colour, float strength, float atten_const, float atten_linear, float atten_quadr) {
        this.position = position;
        this.colour = colour;
        this.strength = strength;
        this.atten_const = atten_const;
        this.atten_linear = atten_linear;
        this.atten_quadr = atten_quadr;
    }

    @Override
    public void uploadSpecsToShader(ShaderProgram shader, String uniformName) {
        shader.uploadVec3f(uniformName + ".position", position);
        shader.uploadVec3f(uniformName + ".colour", colour);
        shader.uploadFloat(uniformName + ".strength", strength);
        shader.uploadFloat(uniformName + ".constant", atten_const);
        shader.uploadFloat(uniformName + ".linear", atten_linear);
        shader.uploadFloat(uniformName + ".quadratic", atten_quadr);
    }

    public Vector3f getPosition() {
        return position;
    }
    public Vector3f getColour() {
        return colour;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }
    public void setColour(Vector3f colour) {
        this.colour = colour;
    }
    public void setStrength(float strength) {
        this.strength = strength;
    }
    public void setAtten_const(float atten_const) {
        this.atten_const = atten_const;
    }
    public void setAtten_linear(float atten_linear) {
        this.atten_linear = atten_linear;
    }
    public void setAtten_quadr(float atten_quadr) {
        this.atten_quadr = atten_quadr;
    }
}

/**
 * Represents a spotlight.
 */
class SpotLight extends PointLight implements LightSource {

    private Vector3f direction;
    private float cutoffCosine;
    private float outerCutoffCosine;

    SpotLight(Vector3f position, Vector3f colour, float strength, Vector3f direction, float atten_const, float atten_linear, float atten_quadr, float cutoffCosine, float outerCutoffCosine) {
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

    public void setAndUploadDirection(Vector3f direction, ShaderProgram shader, String uniformName){
        setDirection(direction);
        shader.uploadVec3f(uniformName + ".direction", direction);
    }

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

/**
 * Represents a directional light.
 */
class DirLight implements LightSource{
    private Vector3f colour;
    private float strength;
    private  Vector3f direction;

    DirLight(Vector3f colour, float strength, Vector3f direction){
        this.colour = colour;
        this.strength = strength;
        this.direction = direction;
    }

    @Override
    public void uploadSpecsToShader(ShaderProgram shader, String uniformName) {
        shader.uploadVec3f(uniformName + ".colour", colour);
        shader.uploadFloat(uniformName + ".strength", strength);
        shader.uploadVec3f(uniformName + ".direction", direction);
    }

    public Vector3f getDirection() {
        return direction;
    }
    public Vector3f getColour() {
        return colour;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }
    public void setStrength(float strength) {
        this.strength = strength;
    }
    public void setColour(Vector3f colour) {
        this.colour = colour;
    }
}