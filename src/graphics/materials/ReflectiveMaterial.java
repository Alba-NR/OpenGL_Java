package graphics.materials;

import graphics.shaders.ShaderProgram;
import graphics.textures.Texture;
import graphics.textures.TextureType;
import org.joml.Vector3f;

import java.util.List;

/**
 * Represents a material that has some sort of reflection (full or partial)
 */
public class ReflectiveMaterial extends Material{

    private float K_refl = 0.0f;    // note: 0.0f when using textures, as it'll have a reflection map texture

    public ReflectiveMaterial(){
        super();
        K_refl = 1.0f;
    }

    public ReflectiveMaterial(Vector3f diffColour, Vector3f specColour){
        super(diffColour, specColour);
        K_refl = 0.5f;
    }

    public ReflectiveMaterial(Vector3f diffColour, Vector3f specColour, float K_refl){
        super(diffColour, specColour);
        this.K_refl = K_refl;
    }

    public ReflectiveMaterial(List<Texture> texList){
        super(texList);
    }

    public ReflectiveMaterial(float K_a, float K_diff, float K_spec, float shininess, Vector3f diffColour, Vector3f specColour){
        super(K_a, K_diff, K_spec, shininess, diffColour, specColour);
        K_refl = 0.5f;
    }

    public ReflectiveMaterial(float K_a, float K_diff, float K_spec, float shininess, Vector3f diffColour, Vector3f specColour, float K_refl){
        super(K_a, K_diff, K_spec, shininess, diffColour, specColour);
        this.K_refl = K_refl;
    }

    public ReflectiveMaterial(float K_a, float K_diff, float K_spec, float shininess, List<Texture> texList){
        super(K_a, K_diff, K_spec, shininess, texList);
    }

    @Override
    public void uploadToShader(ShaderProgram shader){
        shader.uploadFloat("material.K_a", this.getK_a());
        shader.uploadFloat("material.K_diff", this.getK_diff());
        shader.uploadFloat("material.K_spec", this.getK_spec());
        shader.uploadFloat("material.K_refl", K_refl);
        shader.uploadFloat("material.shininess", this.getShininess());

        if(this.getTexturesList() == null){ // upload colours
            shader.uploadInt("materialUsesTextures", 0);
            shader.uploadVec3f("material.diffuseColour", this.getDiffColour());
            shader.uploadVec3f("material.specularColour", this.getSpecColour());
        }else{  // upload textures
            shader.uploadInt("materialUsesTextures", 1);
            uploadTexturesToShader(shader);
        }

        shader.uploadInt("isReflectiveMaterial", 1);    // not a reflective material
    }


    private void uploadTexturesToShader(ShaderProgram shader) {
        int diffNum = 1;
        int specNum = 1;

        List<Texture> texturesList = this.getTexturesList();

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
                case REFLECTION:
                    typeString = "reflection_tex";
                    break;
            }
            shader.uploadInt("material." + typeString + num, i);     // upload texture
        }
    }
}
