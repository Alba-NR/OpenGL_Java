package graphics.shapes;

import graphics.materials.Material;
import graphics.shaders.ShaderProgram;
import graphics.shapes.meshes.Mesh;

/**
 * Represents any shape. A shape must have:
 *      - a {@link Mesh} that defines the shape's form/shape
 *      - a {@link Material} that gives it its material properties...
 */
public abstract class Shape {

    private final Mesh mesh;
    private Material material;

    Shape(Mesh mesh, Material material){
        this.mesh = mesh;
        this.material = material;
    }

    /**
     * Bind the shape's material's textures to the appropriate texture units.
     */
    public void bindMaterialTextures(){
        material.bindTextures();
    }

    /**
     * Bind the shape's material's textures to the appropriate sampler2D in the given shader program.
     * @param shader {@link ShaderProgram} to which to upload textures.
     */
    public void uploadMaterialToShader(ShaderProgram shader) {
        material.uploadToShader(shader);
    }

    public Mesh getMesh() {
        return mesh;
    }
    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
