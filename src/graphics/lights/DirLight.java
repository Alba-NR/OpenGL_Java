package graphics.lights;

import graphics.shaders.ShaderProgram;
import org.joml.Vector3f;

/**
 * Represents a directional light, which has:
 *      - a colour
 *      - an intensity/strength
 *      - a direction
 */
public class DirLight implements LightSource{
    private Vector3f colour;
    private float strength;
    private Vector3f direction;

    public DirLight(Vector3f colour, float strength, Vector3f direction){
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