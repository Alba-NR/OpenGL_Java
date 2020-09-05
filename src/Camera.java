import org.joml.Matrix4f;
import org.joml.Vector3f;

enum CameraMovement{
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    UPWARD,
    DOWNWARD
}

public class Camera {
    private Vector3f cameraPos;     // position of camera
    private Vector3f cameraFront;   // vector along which camera is oriented/pointing to
    private Vector3f cameraUp;      // upward direction of camera

    private double yaw = -90.0;     // like azimuthal angle for 'lens' of camera as if centre of camera at O (spherical polar)
    private double pitch = 0.0;     // like polar angle ...
    private float cameraSpeed =  4.0f; // 1.0f for more 'cinematic' movement
    private float sensitivity =  0.08f; // 0.05f for more 'cinematic' movement
    private double fov =  45.0;


    Camera(Vector3f cameraPos, Vector3f cameraFront, Vector3f up){
        this.cameraPos = cameraPos;
        this.cameraFront = cameraFront.normalize();
        this.cameraUp = up.normalize();
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

    /**
     * Process keyboard input -- i.e. AWSD arrows to move camera (forwards/backward/left/right)
     * Using CameraMovement to abstract from window object.
     * @param direction direction of movement of camera
     */
    void processKeyboardInput(CameraMovement direction, float deltaTime){
        float velocity = cameraSpeed * deltaTime;
        if (direction == CameraMovement.FORWARD) {
            // FORWWARD -> cameraPos += cameraFront * velocity
            cameraPos.add(getCameraFront().mul(velocity));
        }if (direction == CameraMovement.BACKWARD) {
            // BACKWARD -> cameraPos -= cameraFront * velocity
            cameraPos.sub(getCameraFront().mul(velocity));
        }if (direction == CameraMovement.LEFT) {
            // LEFT -> cameraPos -= normalize(cross(cameraFront, cameraUp)) * velocity
            cameraPos.sub(getCameraFront().cross(cameraUp).normalize().mul(velocity));
        }if (direction == CameraMovement.RIGHT) {
            // RIGHT -> cameraPos += normalize(cross(cameraFront, cameraUp)) * velocity
            cameraPos.add(getCameraFront().cross(cameraUp).normalize().mul(velocity));
        }if (direction == CameraMovement.UPWARD) {
            // UPWARD -> cameraPos += cameraUp * velocity
            cameraPos.add(getCameraUp().mul(velocity));
        }if (direction == CameraMovement.DOWNWARD) {
            // DOWNWARD -> cameraPos -= cameraUp * velocity
            cameraPos.sub(getCameraUp().mul(velocity));
        }
    }

    /**
     * Process mouse movement input
     * @param xoffset x position of mouse
     * @param yoffset y position of mouse
     * @param  constrainPitch indicates whether to constrain pitch to -89.0f <= pitch <= 89.0f
     */
    void processMouseMovement(double xoffset, double yoffset, boolean constrainPitch){
        xoffset *= sensitivity;
        yoffset *= sensitivity;

        yaw += xoffset;
        pitch += yoffset;

        if(constrainPitch) {
            if (pitch > 89.0f) pitch = 89.0; // constraint pitch
            if (pitch < -89.0f) pitch = -89.0;
        }

        Vector3f direction = new Vector3f();
        direction.setComponent(0, (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))));
        direction.setComponent(1, (float) Math.toRadians(pitch));
        direction.setComponent(2, (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))));
        cameraFront = direction.normalize();
    }

    /**
     * Process mouse scroll (update fov, for zoom effect)
     * @param yoffset amount moved by scroll in mouse...
     */
    void processMouseScroll(double yoffset) {
        fov -= yoffset;
        if (fov < 1.0) fov = 1.0;
        if (fov > 45.0) fov = 45.0;
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
    public double getFOV(){
        return fov;
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
    public void setCameraSpeed(float speed) {
        this.cameraSpeed = speed;
    }
}
