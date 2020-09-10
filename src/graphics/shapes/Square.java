package graphics.shapes;

import graphics.materials.Material;
import graphics.shapes.meshes.SquareMesh;

/**
 * Represents a square shape.
 */
public class Square extends Shape {
    public Square(Material material) {
        super(SquareMesh.getInstance(), material);
    }

    public Square() {
        super(SquareMesh.getInstance(), new Material());
    }
}
