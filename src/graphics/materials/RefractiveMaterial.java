package graphics.materials;

import graphics.shaders.ShaderProgram;
import graphics.textures.Texture;
import graphics.textures.TextureType;
import org.joml.Vector3f;

import java.util.List;

/**
 * Represents a material that has some sort of refraction
 */

public class RefractiveMaterial extends Material {

    private float K_refr = 0.0f;    // note: 0.0f when using textures, as it'll have a refraction map texture
    private float refrIndex = 1.52f; // glass

    public RefractiveMaterial(){
        super();
        K_refr = 1.0f;
    }

    public RefractiveMaterial(Vector3f diffColour, Vector3f specColour){
        super(diffColour, specColour);
        K_refr = 1.0f;
    }

    public RefractiveMaterial(Vector3f diffColour, Vector3f specColour, float K_refr){
        super(diffColour, specColour);
        this.K_refr = K_refr;
    }

    public RefractiveMaterial(Vector3f diffColour, Vector3f specColour, float K_refr, float refractionIndex){
        super(diffColour, specColour);
        this.K_refr = K_refr;
        this.refrIndex = refractionIndex;
    }

    public RefractiveMaterial(List<Texture> texList){
        super(texList);
    }

    public RefractiveMaterial(List<Texture> texList, float refractionIndex){
        super(texList);
        this.refrIndex = refractionIndex;
    }

    public RefractiveMaterial(float K_a, float K_diff, float K_spec, float shininess, Vector3f diffColour, Vector3f specColour){
        super(K_a, K_diff, K_spec, shininess, diffColour, specColour);
        K_refr = 1.0f;
    }

    public RefractiveMaterial(float K_a, float K_diff, float K_spec, float shininess, Vector3f diffColour, Vector3f specColour, float K_refr){
        super(K_a, K_diff, K_spec, shininess, diffColour, specColour);
        this.K_refr = K_refr;
    }

    public RefractiveMaterial(float K_a, float K_diff, float K_spec, float shininess, Vector3f diffColour, Vector3f specColour, float K_refr, float refractiveIndex){
        super(K_a, K_diff, K_spec, shininess, diffColour, specColour);
        this.K_refr = K_refr;
        this.refrIndex = refractiveIndex;
    }

    public RefractiveMaterial(float K_a, float K_diff, float K_spec, float shininess, List<Texture> texList){
        super(K_a, K_diff, K_spec, shininess, texList);
    }

    public RefractiveMaterial(float K_a, float K_diff, float K_spec, float shininess, List<Texture> texList, float refractiveIndex){
        super(K_a, K_diff, K_spec, shininess, texList);
        this.refrIndex = refractiveIndex;
    }

    @Override
    public void uploadToShader(ShaderProgram shader){
        shader.uploadFloat("material.K_a", this.getK_a());
        shader.uploadFloat("material.K_diff", this.getK_diff());
        shader.uploadFloat("material.K_spec", this.getK_spec());
        shader.uploadFloat("material.K_refr", K_refr);
        shader.uploadFloat("material.shininess", this.getShininess());
        shader.uploadFloat("material.refractiveIndex", refrIndex);

        if(this.getTexturesList() == null){ // upload colours
            shader.uploadInt("materialUsesTextures", 0);
            shader.uploadVec3f("material.diffuseColour", this.getDiffColour());
            shader.uploadVec3f("material.specularColour", this.getSpecColour());
        }else{  // upload textures
            shader.uploadInt("materialUsesTextures", 1);
            uploadTexturesToShader(shader);
        }

        shader.uploadInt("isRefractiveMaterial", 1);
        shader.uploadInt("isReflectiveMaterial", 0);    // not a reflective material
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
                case REFRACTION:
                    typeString = "refraction_tex";
                    break;
            }
            shader.uploadInt("material." + typeString + num, i);     // upload texture
        }
    }
}
