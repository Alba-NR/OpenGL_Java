package graphics.shapes;

import graphics.materials.Material;
import graphics.shapes.meshes.ModelLoader;

public class ShapeFromOBJ extends Shape {
    public ShapeFromOBJ(String fileName, Material material) {
        super(ModelLoader.loadModel(fileName), material);
    }

    public ShapeFromOBJ(String fileName) {
        super(ModelLoader.loadModel(fileName), new Material());
    }
}
