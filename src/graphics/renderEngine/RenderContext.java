package graphics.renderEngine;

import graphics.renderEngine.postProcessing.PostProcessingEffect;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Stores the rendering context needed by the renderers to correctly set-up and render the scene.
 * Includes:
 *      - view and projection matrices (calculated using the camera's data in the main program)
 *      - the camera's position and camera front vector
 * Also includes:
 *      - post-processing effect to use
 */
public class RenderContext {
    private static Matrix4f viewMatrix, projMatrix;
    private static Vector3f cameraPos, cameraFront;
    private static PostProcessingEffect postProcessingEffect = PostProcessingEffect.NONE;

    public static void setContext(Matrix4f view_m, Matrix4f projection_m, Vector3f camera_pos, Vector3f camera_front){
        viewMatrix = view_m;
        projMatrix = projection_m;
        cameraPos = camera_pos;
        cameraFront = camera_front;
    }

    public static void setPostProcessingEffect(PostProcessingEffect effect){
        postProcessingEffect = effect;
    }

    public static Matrix4f getViewMatrix(){
        return viewMatrix;
    }

    public static Matrix4f getProjMatrix(){
        return projMatrix;
    }

    public static Vector3f getCameraPos() {
        return cameraPos;
    }

    public static Vector3f getCameraFront() {
        return cameraFront;
    }

    public static PostProcessingEffect getPostProcessingEffect() {
        return postProcessingEffect;
    }
}
