#version 330 core

struct Material {
    sampler2D diffuse_tex1;    // diffuse map (for diffuse colour)
    sampler2D specular_tex1;   // specular map (for specular reflection)
    sampler2D reflection_tex0;   // reflection map (reflection from skybox)
    vec3 diffuseColour;        // diffuse colour
    vec3 specularColour;       // specular colour
    float K_a;          // ambient reflection coefficient
    float K_diff;       // diff reflection coeff
    float K_spec;       // spec reflection coeff
    float K_refl;       // reflectivity coeff (for reflection of skybox, when using plain colours for material not when using maps)
    float shininess;    // shininness coeff (for specular reflection)
};

struct DirLight { // directional light in scene (1 atm)
    vec3 colour;        // light colour
    vec3 direction;     // light direction
    float strength;     // light strength/intensity
};

struct PointLight { // point light
    vec3 position;      // light pos in wc
    vec3 colour;        // light colour
    float strength;     // light strength/intensity

    float constant;     // constants for impl attenuation
    float linear;
    float quadratic;
};


struct SpotLight { // flash light (spotlight)
    vec3 position;      // light pos in wc
    vec3 colour;        // light colour
    float strength;     // light strength/intensity
    vec3 direction;     // light direction
    float cutoffCosine; // cosine of spotlight cutoff angle
    float outerCutoffCosine; // cosine of outer spotlight cutoff angle (for softer borders)

    float constant;     // constants for impl attenuation
    float linear;
    float quadratic;
};

in VS_OUT {
    vec2 TexCoords;   // texture UV coord
    vec3 wc_normal;  // fragment normal in world coord
    vec3 wc_fragPos; // fragment position in world coord
} fs_in;

out vec4 FragColor;

#define MAX_POINT_LIGHTS 3
uniform vec3 I_a;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform DirLight dirLight;
uniform SpotLight spotLight;
uniform Material material;
uniform bool materialUsesTextures;
uniform vec3 wc_cameraPos;
uniform bool flashLightIsON;
uniform samplerCube skybox;
uniform bool isReflectiveMaterial;

// function prototypes
vec3 CalcDirLight(DirLight light, vec3 N, vec3 V, vec3 diffColour, vec3 specColour);
vec3 CalcPointLight(PointLight light, vec3 N, vec3 V, vec3 diffColour, vec3 specColour);
vec3 CalcSpotLight(SpotLight light, vec3 N, vec3 V, vec3 diffColour, vec3 specColour);

void main()
{
    vec3 I_result;

    // calc vectors
    vec3 N = normalize(fs_in.wc_normal);
    vec3 V = normalize(wc_cameraPos - fs_in.wc_fragPos);

    // get diffuse & specular colours...
    vec3 diffColour, specColour;
    vec3 reflectedColour = vec3(0.0);
    vec3 minusVreflectedOnN;
    if(isReflectiveMaterial) minusVreflectedOnN = reflect(-V, N);
    if(materialUsesTextures){
        // ...from textures (the maps...)
        diffColour = vec3(texture(material.diffuse_tex1, fs_in.TexCoords));
        specColour = vec3(texture(material.specular_tex1, fs_in.TexCoords));
        if(isReflectiveMaterial) reflectedColour = vec3(texture(material.reflection_tex0, fs_in.TexCoords)) * texture(skybox, minusVreflectedOnN).rgb;
    } else {
        diffColour = material.diffuseColour;
        specColour = material.specularColour;
        if(isReflectiveMaterial) reflectedColour = material.K_refl * texture(skybox, minusVreflectedOnN).rgb;
    }

    // Directional lighting
    I_result = CalcDirLight(dirLight, N, V, diffColour, specColour);

    // Point lights
    for(int i = 0; i < MAX_POINT_LIGHTS; i++) I_result += CalcPointLight(pointLights[i], N, V, diffColour, specColour);

    // Flashlight spotlight
    if(flashLightIsON) I_result += CalcSpotLight(spotLight, N, V, diffColour, specColour);

    // ambient light
    I_result += I_a * diffColour * material.K_a;

    // colour reflected from skybox
    I_result += reflectedColour;

    FragColor = vec4(I_result, 1.0);
}


vec3 CalcDirLight(DirLight light, vec3 N, vec3 V, vec3 diffColour, vec3 specColour)
{
    // calc vectors
    vec3 L = normalize(-light.direction);
    vec3 R = reflect(-L, N);

    // diffuse & specular shading
    vec3 I_diffuse = light.colour * diffColour * material.K_diff * max(dot(N, L), 0.0);
    vec3 I_specular = light.colour * specColour * material.K_spec * pow(max(dot(V, R), 0.0), material.shininess);

    return (I_diffuse + I_specular) * light.strength;
}

vec3 CalcPointLight(PointLight light, vec3 N, vec3 V, vec3 diffColour, vec3 specColour)
{
    //calc vectors
    vec3 L = normalize(light.position - fs_in.wc_fragPos);
    vec3 R = reflect(-L, N);

    // attenuation
    float distance = length(light.position - fs_in.wc_fragPos);
    float attenuation = light.strength / (light.constant + light.linear * distance + light.quadratic * (distance * distance));

    // diffuse & specular shading
    vec3 I_diffuse = light.colour * diffColour * material.K_diff * max(dot(N, L), 0.0);
    vec3 I_specular = light.colour * specColour * material.K_spec * pow(max(dot(V, R), 0.0), material.shininess);

    I_diffuse *= attenuation;
    I_specular *= attenuation;

    return I_diffuse + I_specular;
}

vec3 CalcSpotLight(SpotLight light, vec3 N, vec3 V, vec3 diffColour, vec3 specColour)
{
    //calc vectors
    vec3 L = normalize(light.position - fs_in.wc_fragPos);
    vec3 R = reflect(-L, N);

    // attenuation
    float distance = length(light.position - fs_in.wc_fragPos);
    float attenuation =  light.strength / (light.constant + light.linear * distance + light.quadratic * (distance * distance));

    // angles for cutoff of spotlight
    float theta = dot(L, normalize(-light.direction));
    float I = clamp((theta - light.outerCutoffCosine) / (light.cutoffCosine - light.outerCutoffCosine), 0.0, 1.0); // clamp values to [0.0, 1.0] range

    // diffuse & specular shading
    vec3 I_diffuse = light.colour * diffColour * material.K_diff * max(dot(N, L), 0.0);
    vec3 I_specular = light.colour * specColour * material.K_spec * pow(max(dot(V, R), 0.0), material.shininess);

    I_diffuse *= attenuation * I;
    I_specular *= attenuation * I;

    return I_diffuse + I_specular;
}
