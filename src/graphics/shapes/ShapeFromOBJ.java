package graphics.shapes;

import graphics.materials.Material;
import graphics.core.io.ModelLoader;

public class ShapeFromOBJ extends Shape {
    public ShapeFromOBJ(String fileName, Material material, boolean useFaceCulling) {
        super(ModelLoader.loadModel(fileName, useFaceCulling), material);
    }

    public ShapeFromOBJ(String fileName, boolean useFaceCulling) {
        super(ModelLoader.loadModel(fileName, useFaceCulling), new Material());
    }
}
