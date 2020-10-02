package graphics.scene;

import graphics.renderEngine.RenderContext;
import graphics.shaders.ShaderProgram;
import graphics.shapes.Shape;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * Represents an entity (node in the scene node graph) which
 * has a shape and hence can be rendered.
 */
public class DrawableEntity extends Entity {
    private Shape shape;

    public DrawableEntity(Entity parent, Matrix4f local_transform, Vector3f shape_scale, Shape shape) {
        super(parent, local_transform, shape_scale);
        this.shape = shape;
    }

    @Override
    public void render(ShaderProgram shaderProgram) {
        glBindVertexArray(shape.getMesh().getVAOHandle());

        // bind data to shader
        shaderProgram.bindDataToShader(0, shape.getMesh().getVertexVBOHandle(), 3);
        shaderProgram.bindDataToShader(1, shape.getMesh().getNormalHandle(), 3);
        shaderProgram.bindDataToShader(2, shape.getMesh().getTexHandle(), 2);

        // upload world transform matrix as model matrix to shader
        shaderProgram.uploadMatrix4f("model_m", world_transform);

        // calc MVP matrix (once in CPU rather than per fragment in GPU...)
        Matrix4f mvp =  new Matrix4f(RenderContext.getProjMatrix());
        mvp.mul(RenderContext.getViewMatrix()).mul(world_transform);
        shaderProgram.uploadMatrix4f("mvp_m", mvp);

        // calc matrix to transform normal vect from oc to wc
        Matrix4f normalM = new Matrix4f();
        world_transform.invert(normalM).transpose();
        shaderProgram.uploadMatrix4f("normal_m", normalM);

        // render shape
        shape.bindMaterialTextures();
        shape.uploadMaterialToShader(shaderProgram);
        shape.getMesh().render();

        // render children
        for(Entity child : children) child.render(shaderProgram);
    }

    @Override
    public void renderToDepthMap(ShaderProgram shaderProgram) {
        glBindVertexArray(shape.getMesh().getVAOHandle());

        // bind data to shader
        shaderProgram.bindDataToShader(0, shape.getMesh().getVertexVBOHandle(), 3);

        // upload world transform matrix as model matrix to shader
        shaderProgram.uploadMatrix4f("model_m", world_transform);

        shape.getMesh().render();

        // render children
        for(Entity child : children) child.renderToDepthMap(shaderProgram);
    }

    @Override
    public void deallocateMeshResources(){
        shape.getMesh().deallocateResources();
        children.forEach(Entity::deallocateMeshResources);
    }
}
