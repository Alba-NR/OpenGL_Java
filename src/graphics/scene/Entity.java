package graphics.scene;

import graphics.shaders.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node of the scene graph.
 */
public abstract class Entity {

    private Entity parent;
    List<Entity> children;
    Matrix4f world_transform;           // position and orientation in the world overall
    private Matrix4f local_transform;   // position and orientation in relation to parent node
    private Vector3f shape_scale;       // scaling of shape/model w/o affecting children

    Entity(Entity parent, Matrix4f local_transform, Vector3f shape_scale){
        this.parent = parent;
        this.local_transform = local_transform;
        this.shape_scale = shape_scale;
        children = new ArrayList<>();

        world_transform = new Matrix4f();
        calcWorldMatrix();
    }

    /**
     * Calculates this node's world matrix using its parent's world matrix and this node's
     * local transform matrix. The world_transform field is updated. Also updates the world
     * matrices of its children.
     */
    private void calcWorldMatrix(){
        Matrix4f localTimesScale = new Matrix4f();
        local_transform.scale(shape_scale, localTimesScale);    // first get local transform * scale result

        // calc world matrix by mult node's (local * scale) matrix w/parent's world matrix.
        if(parent != null) parent.getWorld_transform().mul(localTimesScale, world_transform);
        else world_transform = localTimesScale; // if root node, world transform same as local transform (times scale matrix)

        // repeat for all of current node's children
        children.forEach(Entity::calcWorldMatrix);
    }

    /**
     * Render the model for which this node is the root in the scene graph.
     * Uses the current active shader to do so.
     * ! Light specs must be previously uploaded to the shader before calling this method.
     * (note: renders this node's children too)
     */
    public abstract void render(ShaderProgram shaderProgram);

    /**
     * Deallocate the node's & its childrens mesh's resources.
     */
    public abstract void deallocateMeshResources();

    /**
     * Adds the given node as a child of this node (& sets this node as the parent of the given node)
     * @param newChild {@link Entity} to add as child
     */
    public void addChild(Entity newChild){
        if (newChild != null) {
            children.add(newChild);
            newChild.setParent(this);
        }
    }

    /**
     * Adds all nodes in the given list as childs of this node (& sets this node as their parent)
     * @param newChildren {@link Entity} to add as child
     */
    public void addAllToChildren(List<Entity> newChildren){
        if (newChildren != null) {
            children.addAll(newChildren);
            for(Entity child : newChildren) child.setParent(this);
        }
    }

    private void setParent(Entity parent) {
        this.parent = parent;
        calcWorldMatrix();
    }
    public void setChildren(List<Entity> children) {
        for(Entity child : this.children) child.setParent(null);
        this.children = children;
        for(Entity child : children) child.setParent(this);
    }
    public void setLocal_transform(Matrix4f local_transform) {
        if(!local_transform.equals(this.local_transform)) { // if stmt to avoid unnecessarily calling calcWorldMatrix()
            this.local_transform = local_transform;
            calcWorldMatrix();
        }
    }
    public void updateWorld_transform(Matrix4f world_transform) {
        // todo not sure if this method should be allowed... - maybe it's useful for setting up / debuggin the entities positions on the scene?
        if (!world_transform.equals(this.world_transform)){ // if stmt to avoid unnecessarily calling calcWorldMatrix()
            this.world_transform = world_transform;
            for (Entity child : children) child.calcWorldMatrix();
        }
    }
    public void setShape_scale(Vector3f shape_scale) {
        if(!shape_scale.equals(this.shape_scale)) { // if stmt to avoid unnecessarily calling calcWorldMatrix()
            this.shape_scale = shape_scale;
            calcWorldMatrix();
        }
    }

    public List<Entity> getChildren() {
        return children;
    }
    public Entity getParent() {
        return parent;
    }
    public Matrix4f getLocal_transform() {
        return local_transform;
    }
    public Matrix4f getWorld_transform() {
        if(world_transform == null) calcWorldMatrix();
        return world_transform;
    }
    public Vector3f getShape_scale() {
        return shape_scale;
    }
}
