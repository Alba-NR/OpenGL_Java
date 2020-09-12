package graphics.shapes;

import graphics.materials.Material;
import graphics.shapes.meshes.ModelLoader;

public class ShapeFromOBJ extends Shape {
    public ShapeFromOBJ(String fileName, Material material, boolean useFaceCulling) {
        super(ModelLoader.loadModel(fileName, useFaceCulling), material);
    }

    public ShapeFromOBJ(String fileName, boolean useFaceCulling) {
        super(ModelLoader.loadModel(fileName, useFaceCulling), new Material());
    }
}
