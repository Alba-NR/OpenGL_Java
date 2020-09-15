package graphics.materials;

import graphics.shaders.ShaderProgram;
import graphics.textures.Texture;
import graphics.textures.TextureType;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Material {

    private Vector3f diffColour, specColour;
    private float K_a, K_diff, K_spec;
    private float shininess;
    private List<Texture> texturesList = null;

    public Material(){
        diffColour = new Vector3f(0.973f, 0.639f, 0.475f);  // coral orange colour
        specColour = new Vector3f(	0.984f, 0.851f, 0.663f);
        K_a = 0.5f;
        K_diff = 0.4f;
        K_spec = 0.8f;
        shininess = 64f;
        texturesList = null;
    }

    public Material(List<Texture> texList){
        diffColour = null;
        specColour = null;
        K_a = 0.5f;
        K_diff = 0.4f;
        K_spec = 0.8f;
        shininess = 64f;
        texturesList = List.copyOf(texList);
    }

    public Material(float K_a, float K_diff, float K_spec, Vector3f diffColour, Vector3f specColour){
        this.diffColour = diffColour;
        this.specColour = specColour;
        this.K_a = K_a;
        this.K_diff = K_diff;
        this.K_spec = K_spec;
        texturesList = null;
    }

    public Material(float K_a, float K_diff, float K_spec, List<Texture> texList){
        diffColour = null;
        specColour = null;
        this.K_a = K_a;
        this.K_diff = K_diff;
        this.K_spec = K_spec;
        texturesList = List.copyOf(texList);
    }

    /**
     * Upload the material's attributes to the 'material' uniform in the
     * given shader program.
     */
    public void uploadToShader(ShaderProgram shader){
        shader.uploadFloat("material.K_a", K_a);
        shader.uploadFloat("material.K_diff", K_diff);
        shader.uploadFloat("material.K_spec", K_spec);
        shader.uploadFloat("material.shininess", shininess);

        if(texturesList == null){ // upload colours
            shader.uploadVec3f("material.diffuseColour", diffColour);
            shader.uploadVec3f("material.specularColour", specColour);
        }else{  // upload textures
            uploadTexturesToShader(shader);
        }
    }

    /**
     * Upload the material's textures to the appropriate sampler2D in the given shader program.
     * Currently: upload to attrib of 'material' Material uniform.
     *      DIFFUSE textures to material.diffuse_texN
     *      SPECULAR textures to material.specular_texN
     * Note: texturesList must not be null
     * @param shader {@link ShaderProgram} to which to upload textures.
     */
    private void uploadTexturesToShader(ShaderProgram shader) {
        int diffNum = 1;
        int specNum = 1;

        for (int i = 0; i < texturesList.size(); i++) {
            // determine name of uniform to which to upload texture
            int num = 0;
            TextureType texType = texturesList.get(i).getType();
            String typeString = "diffuse_tex";
            switch (texType) {
                case DIFFUSE:
                    num = diffNum++;
                    //typeString = "diffuse_tex";
                    break;
                case SPECULAR:
                    num = specNum++;
                    typeString = "specular_tex";
                    break;
            }
            shader.uploadInt("material." + typeString + num, i);     // upload texture
        }
    }

    /**
     * Bind the material's textures to the appropriate texture units.
     */
    public void bindTextures(){
        if(texturesList != null) {
            for (int i = 0; i < texturesList.size(); i++) {
                glActiveTexture(GL_TEXTURE0 + i); // activate proper texture unit before binding
                glBindTexture(GL_TEXTURE_2D, texturesList.get(i).getHandle());  // bind texture to appropriate texture unit
            }
        }
    }

    public Vector3f getDiffColour() {
        return diffColour;
    }
    public Vector3f getSpecColour() {
        return specColour;
    }
    public float getK_a() {
        return K_a;
    }
    public float getK_diff() {
        return K_diff;
    }
    public float getK_spec() {
        return K_spec;
    }
    public float getShininess() {
        return shininess;
    }
    public List<Texture> getTexturesList() {
        return List.copyOf(texturesList);
    }

    public void setDiffColour(Vector3f diffColour) {
        this.diffColour = diffColour;
    }
    public void setSpecColour(Vector3f specColour) {
        this.specColour = specColour;
    }
    public void setK_a(float k_a) {
        K_a = k_a;
    }
    public void setK_diff(float k_diff) {
        K_diff = k_diff;
    }
    public void setK_spec(float k_spec) {
        K_spec = k_spec;
    }
    public void setShininess(float shininess) {
        this.shininess = shininess;
    }
    public void setTexturesList(List<Texture> texturesList) {
        this.texturesList = List.copyOf(texturesList);
    }
}
