package graphics.lights;

import org.joml.Vector3f;

/**
 * Represents a flashlight that can be turned ON or OFF.
 * When created, the default is ON.
 * (a flashlight is a type of {@link SpotLight})
 */
public class FlashLight extends SpotLight{
    private boolean state = true;   // true <=> flashlight is ON

    public FlashLight(Vector3f position, Vector3f colour, float strength, Vector3f direction, float atten_const, float atten_linear, float atten_quadr, float cutoffCosine, float outerCutoffCosine) {
        super(position, colour, strength, direction, atten_const, atten_linear, atten_quadr, cutoffCosine, outerCutoffCosine);
    }

    /**
     * As if pressing flashlight button ON/OFF. Inverts state.
     */
    public void toggle(){
        state = !state;
    }

    public boolean getState(){
        return state;
    }
    public void setState(boolean state) {
        this.state = state;
    }
}
