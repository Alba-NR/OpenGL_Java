import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    Vector3f cameraPos;     // position of camera
    Vector3f cameraFront;   // vector along which camera is oriented/pointing to
    Vector3f cameraUp;      // upward direction of camera

    Camera(Vector3f cameraPos, Vector3f cameraFront, Vector3f cameraUp){
        this.cameraPos = cameraPos;
        this.cameraFront = cameraFront;
        this.cameraUp = cameraUp;
    }

    Camera(){
        this.cameraPos = new Vector3f(0,0,0);
        this.cameraFront =  new Vector3f(0.0f, 0.0f, 1.0f);
        this.cameraUp =  new Vector3f(0.0f, 1.0f, 0.0f);
    }

    /**
     * Get the lookAt matrix from the camera.
     * @return lookAt matrix for the current config of the camera.
     */
    Matrix4f calcLookAt(){
        Matrix4f lookat_matrix = new Matrix4f();
        Vector3f target = new Vector3f(cameraPos);
        target.add(cameraFront);
        lookat_matrix.lookAt(cameraPos, target, cameraUp); // camera pos, target pos, vector repr up vec in world space
        return lookat_matrix;
    }

    public Vector3f getCameraFront() {
        return new Vector3f(cameraFront);
    }
    public Vector3f getCameraPos() {
        return new Vector3f(cameraPos);
    }
    public Vector3f getCameraUp() {
        return new Vector3f(cameraUp);
    }
    public void setCameraFront(Vector3f cameraFront) {
        this.cameraFront = cameraFront;
    }
    public void setCameraPos(Vector3f cameraPos) {
        this.cameraPos = cameraPos;
    }
    public void setCameraUp(Vector3f cameraUp) {
        this.cameraUp = cameraUp;
    }
}
