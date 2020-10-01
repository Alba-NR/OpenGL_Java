package graphics.shapes;

import graphics.materials.Material;
import graphics.shapes.meshes.SquareMesh;

/**
 * Represents a square shape. Edges are unit length.
 */
public class Square extends Shape {
    public Square(Material material) {
        super(SquareMesh.getInstance(), material);
    }

    public Square() {
        super(SquareMesh.getInstance(), new Material());
    }
}
