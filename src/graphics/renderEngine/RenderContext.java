package graphics.renderEngine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderContext {
    private static Matrix4f viewMatrix, projMatrix;
    private static Vector3f cameraPos, cameraFront;

    public static void setContext(Matrix4f view_m, Matrix4f projection_m, Vector3f camera_pos, Vector3f camera_front){
        viewMatrix = view_m;
        projMatrix = projection_m;
        cameraPos = camera_pos;
        cameraFront = camera_front;
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
}
