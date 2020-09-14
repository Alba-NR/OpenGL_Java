package graphics.scene;

import graphics.shaders.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Represents an entity (node in the scene node graph) which
 * doesn't have a shape and so cannot be rendered.
 */
public class AbstractEntity extends Entity{

    public AbstractEntity(Entity parent, Matrix4f local_transform) {
        super(parent, local_transform, new Vector3f(1.0f));
    }

    @Override
    public void render(ShaderProgram shaderProgram) {
        // render children
        for(Entity child : children) child.render(shaderProgram);
    }

    @Override
    public void deallocateMeshResources(){
        children.forEach(Entity::deallocateMeshResources);
    }
}
