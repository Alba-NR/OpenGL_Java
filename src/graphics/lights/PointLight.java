package graphics.lights;

import graphics.shaders.ShaderProgram;
import org.joml.Vector3f;

/**
 * Represents a point light, which has:
 *      - a position in the scene (world coord)
 *      - a colour
 *      - a strength/intensity
 *      - attenuation constants (for the quadratic, linear & constant terms)
 */
public class PointLight implements LightSource {
    private Vector3f position;
    private Vector3f colour;
    private float strength;
    private float atten_const;
    private float atten_linear;
    private float atten_quadr;

    public PointLight(Vector3f position, Vector3f colour, float strength, float atten_const, float atten_linear, float atten_quadr) {
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