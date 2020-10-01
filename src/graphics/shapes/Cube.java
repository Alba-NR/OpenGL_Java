package graphics.shapes;

import graphics.materials.Material;
import graphics.shapes.meshes.CubeMesh;

/**
 * Represents a unit cube.
 */
public class Cube extends Shape {

    public Cube(Material material){
        super(CubeMesh.getInstance(), material);
    }

    public Cube(){
        super(CubeMesh.getInstance(), new Material());
    }
}
