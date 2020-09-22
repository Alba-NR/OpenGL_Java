package graphics.scene;

import graphics.lights.*;
import graphics.shapes.CubeMapCube;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private List<Entity> components;
    private DirLight dirLight;
    private FlashLight flashLight;
    private List<PointLight> pointLights;
    private Vector3f I_a;
    private CubeMapCube skybox;

    public Scene(List<Entity> components, DirLight dirLight, FlashLight flashLight, List<PointLight> pointLights, Vector3f ambient_intensity) {
        if(components != null) this.components = components;
        else this.components = new ArrayList<>();
        this.dirLight = dirLight;
        this.flashLight = flashLight;
        this.pointLights = pointLights;
        I_a = ambient_intensity;
        skybox = null;
    }

    public Scene(List<Entity> components, DirLight dirLight, FlashLight flashLight, List<PointLight> pointLights, Vector3f ambient_intensity, CubeMapCube skybox) {
        if(components != null) this.components = components;
        else this.components = new ArrayList<>();
        this.dirLight = dirLight;
        this.flashLight = flashLight;
        this.pointLights = pointLights;
        I_a = ambient_intensity;
        this.skybox = skybox;
    }


    /**
     * Deallocate the mesh resources of the SceneNodes that form this scene
     */
    public void deallocateMeshResources(){
        components.forEach(Entity::deallocateMeshResources);
        skybox.getMesh().deallocateResources();
    }

    public List<Entity> getComponents() {
        return components;
    }
    public DirLight getDirLight() {
        return dirLight;
    }
    public FlashLight getFlashLight() {
        return flashLight;
    }
    public List<PointLight> getPointLights() {
        return pointLights;
    }
    public Vector3f getI_a() {
        return I_a;
    }
    public CubeMapCube getSkybox() {
        return skybox;
    }
}
