#version 330 core

struct Material {
    sampler2D diffuse_tex1;    // diffuse map (for diffuse colour)
    sampler2D specular_tex1;   // specular map (for specular reflection)
    sampler2D reflection_tex0;   // reflection map (reflection from skybox)
    sampler2D refraction_tex0;   // refraction map (refraction from skybox)
    vec3 diffuseColour;        // diffuse colour
    vec3 specularColour;       // specular colour
    float K_a;          // ambient reflection coefficient
    float K_diff;       // diff reflection coeff
    float K_spec;       // spec reflection coeff
    float K_refl;       // reflectivity coeff (for reflection of skybox, when using plain colours for material not when using maps)
    float K_refr;       // refraction coeff
    float shininess;    // shininness coeff (for specular reflection)
    float refractiveIndex;  // refractive index
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
    vec2 TexCoords;     // texture UV coord
    vec3 wc_normal;     // fragment normal in world coord
    vec3 wc_fragPos;    // fragment position in world coord
    vec4 lightSpace_fragPos;    // fragment position in directional light's space
} fs_in;

out vec4 FragColor;

#define MAX_POINT_LIGHTS 3
uniform vec3 I_a;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform DirLight dirLight;
uniform SpotLight spotLight;
uniform bool flashLightIsON;
uniform samplerCube skybox;

uniform Material material;
uniform bool materialUsesTextures;
uniform bool isReflectiveMaterial;
uniform bool isRefractiveMaterial;

uniform vec3 wc_cameraPos;
uniform sampler2D shadowMap;

// function prototypes
vec3 CalcDirLight(DirLight light, vec3 N, vec3 V, vec3 diffColour, vec3 specColour);
vec3 CalcPointLight(PointLight light, vec3 N, vec3 V, vec3 diffColour, vec3 specColour);
vec3 CalcSpotLight(SpotLight light, vec3 N, vec3 V, vec3 diffColour, vec3 specColour);
float CalcShadow(DirLight light, vec3 N, vec4 fragPosLightSpace);
vec3 toneMapAndDisplayEncode(vec3 linearRGB);

void main()
{
    vec3 I_result = vec3(0);

    // calc vectors
    vec3 N = normalize(fs_in.wc_normal);
    vec3 V = normalize(wc_cameraPos - fs_in.wc_fragPos);

    // --- get diffuse & specular colours... ---
    vec3 diffColour, specColour;  // diff & spec colours multiplied by the appropriate material coeff (K_diff & K_spec)
    vec3 reflectedColour = vec3(0.0);
    vec3 refractedColour = vec3(0.0);

    // reflection
    vec3 minusVreflectedOnN;
    if(isReflectiveMaterial) minusVreflectedOnN = reflect(-V, N);

    // refraction
    vec3 refractedVector;
    if(isRefractiveMaterial){
        float ratio = 1.00 / material.refractiveIndex; // ratio of refr indeces of air to material's medium
        refractedVector = refract(-V, N, ratio);
    }

    if(materialUsesTextures){
        // ...from textures (the maps...)
        vec4 diffSampleFromTex = texture(material.diffuse_tex1, fs_in.TexCoords);
        if(diffSampleFromTex.a < 0.1) discard;
        diffColour = vec3(diffSampleFromTex);
        specColour = vec3(texture(material.specular_tex1, fs_in.TexCoords));
        if(isReflectiveMaterial) reflectedColour = vec3(texture(material.reflection_tex0, fs_in.TexCoords)) * texture(skybox, minusVreflectedOnN).rgb;
        if(isRefractiveMaterial) refractedColour = vec3(texture(material.refraction_tex0, fs_in.TexCoords)) * texture(skybox, refractedVector).rgb;
    } else {
        diffColour = material.diffuseColour;
        specColour = material.specularColour;
        if(isReflectiveMaterial) reflectedColour = material.K_refl * texture(skybox, minusVreflectedOnN).rgb;
        if(isRefractiveMaterial) refractedColour = material.K_refr * texture(skybox, refractedVector).rgb;
    }

    vec3 diffComponent = material.K_diff * diffColour;
    vec3 specComponent = material.K_spec * specColour;

    // Directional lighting
    float shadow = CalcShadow(dirLight, N, fs_in.lightSpace_fragPos);
    I_result += (1.0 - shadow) * CalcDirLight(dirLight, N, V, diffComponent, specComponent);

    // Point lights
    for(int i = 0; i < MAX_POINT_LIGHTS; i++) I_result += CalcPointLight(pointLights[i], N, V, diffComponent, specComponent);

    // Flashlight spotlight
    if(flashLightIsON) I_result += CalcSpotLight(spotLight, N, V, diffComponent, specComponent);

    // ambient light
    I_result += I_a * diffColour * material.K_a;

    // colour reflected from skybox
    I_result += reflectedColour;
    // colour refracted from skybox
    I_result += refractedColour;

    // perform basic tonemapping (adjust brightness) and display encoding (apply gamma correction)
    FragColor = vec4(toneMapAndDisplayEncode(I_result), 1.0);

}

// performs tone mapping (-- here: brightness adjustment) and display encoding (-- here: gamma correction) combined
vec3 toneMapAndDisplayEncode(vec3 linearRGB)
{
    float L_white = 0.7; // scene-referrered luminance of white (controls brightness of img)
    float inverseGamma = 1.0/2.2;   // gamma value is 2.2

    return pow(linearRGB / L_white, vec3(inverseGamma));
}

// calc colour of fragment coming from light from the given directional light
vec3 CalcDirLight(DirLight light, vec3 N, vec3 V, vec3 diffComponent, vec3 specComponent)
{
    // calc vectors
    vec3 L = normalize(-light.direction);
    vec3 H = normalize(N + V); // halfway vector (half way btwn V & N)

    // diffuse & specular shading
    vec3 I_diffuse = light.colour * diffComponent * max(dot(N, L), 0.0);
    vec3 I_specular = light.colour * specComponent * pow(max(dot(N, H), 0.0), material.shininess);

    return (I_diffuse + I_specular) * light.strength;
}

// calc colour of fragment coming from light from the given point light
vec3 CalcPointLight(PointLight light, vec3 N, vec3 V, vec3 diffComponent, vec3 specComponent)
{
    //calc vectors
    vec3 L = normalize(light.position - fs_in.wc_fragPos);
    vec3 H = normalize(N + V);

    // attenuation
    float distance = length(light.position - fs_in.wc_fragPos);
    float attenuation = light.strength / (light.constant + light.linear * distance + light.quadratic * (distance * distance));

    // diffuse & specular shading
    vec3 I_diffuse = light.colour * diffComponent * max(dot(N, L), 0.0);
    vec3 I_specular = light.colour * specComponent * pow(max(dot(N, H), 0.0), material.shininess);

    I_diffuse *= attenuation;
    I_specular *= attenuation;

    return I_diffuse + I_specular;
}

// calc colour of fragment coming from light from the given spotlight
vec3 CalcSpotLight(SpotLight light, vec3 N, vec3 V, vec3 diffComponent, vec3 specComponent)
{
    //calc vectors
    vec3 L = normalize(light.position - fs_in.wc_fragPos);
    vec3 H = normalize(N + V);

    // attenuation
    float distance = length(light.position - fs_in.wc_fragPos);
    float attenuation =  light.strength / (light.constant + light.linear * distance + light.quadratic * (distance * distance));

    // angles for cutoff of spotlight
    float theta = dot(L, normalize(-light.direction));
    float I = clamp((theta - light.outerCutoffCosine) / (light.cutoffCosine - light.outerCutoffCosine), 0.0, 1.0); // clamp values to [0.0, 1.0] range

    // diffuse & specular shading
    vec3 I_diffuse = light.colour * diffComponent * max(dot(N, L), 0.0);
    vec3 I_specular = light.colour * specComponent * pow(max(dot(N, H), 0.0), material.shininess);

    I_diffuse *= attenuation * I;
    I_specular *= attenuation * I;

    return I_diffuse + I_specular;
}

float CalcShadow(DirLight light, vec3 N, vec4 fragPosLightSpace)
{
    // (vv all done from dir light's perspective)

    // perform perspective divide (used later w/perspective projection; not needed w/orthographic proj)
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    // transform to [0,1] range
    projCoords = projCoords * 0.5 + 0.5; // todo why?

    float shadow = 0.0;
    if(projCoords.z <= 1.0){
        vec3 L = normalize(-light.direction); // calc to light vector
        /*
        float closestDepth = texture(shadowMap, projCoords.xy).r;   // get closest depth value
        float currentDepth = projCoords.z;  // get depth of current frag

        float bias = max(0.05 * (1.0 - dot(N, L)), 0.005);  // calc bias (to avoid 'shadow acne' // moiré pattern aliasing)
        shadow = currentDepth - bias > closestDepth  ? 1.0 : 0.0; // check whether current frag pos is in shadow
        */
        // impl PCF (percentage-closer filtering) to produce softer shadows
        float currentDepth = projCoords.z;  // get depth of current frag
        float bias = max(0.05 * (1.0 - dot(N, L)), 0.005);  // calc bias (to avoid 'shadow acne' // moiré pattern aliasing)

        vec2 texelSize = 1.0 / textureSize(shadowMap, 0);

        for(int x = -1; x <= 1; ++x){
            for(int y = -1; y <= 1; ++y){
                float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
                shadow += currentDepth - bias > pcfDepth ? 1.0 : 0.0;
            }
        }
        shadow /= 9.0;
    }

    return shadow;
}