#version 330 core

struct Material {
    sampler2D diffuseColour;    // diffuse map (for diffuse colour)
    sampler2D specularColour;   // specular map (for specular reflection)
    float K_a;
    float K_diff;
    float K_spec;
    float shininess;    // shininness coeff (for specular reflection)
};

struct DirLight { // directional light in scene (1 atm)
    vec3 colour;        // light colour
    vec3 direction;     // light direction
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

in vec2 TexCoord;   // texture UV coord
in vec3 wc_normal;  // fragment normal in world coord
in vec3 wc_fragPos; // fragment position in world coord

out vec4 FragColor;

#define MAX_POINT_LIGHTS 3
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform DirLight dirLight;
uniform SpotLight spotLight;
uniform Material material;
uniform vec3 wc_cameraPos;
uniform vec3 I_a;

// function prototypes
vec3 CalcDirLight(DirLight light, vec3 N, vec3 V, vec3 diffColour, vec3 specColour);
vec3 CalcPointLight(PointLight light, vec3 N, vec3 V, vec3 diffColour, vec3 specColour);
vec3 CalcSpotLight(SpotLight light, vec3 N, vec3 V, vec3 diffColour, vec3 specColour);

void main()
{
    vec3 I_result;

    // calc vectors
    vec3 N = normalize(wc_normal);
    vec3 V = normalize(wc_cameraPos - wc_fragPos);

    // get diffuse & specular colours from textures (the maps...)
    vec3 diffColour = vec3(texture(material.diffuseColour, TexCoord));
    vec3 specColour = vec3(texture(material.specularColour, TexCoord));

    // Directional lighting
    I_result = CalcDirLight(dirLight, N, V, diffColour, specColour);

    // Point lights
    for(int i = 0; i < MAX_POINT_LIGHTS; i++) I_result += CalcPointLight(pointLights[i], N, V, diffColour, specColour);

    // Flashlight spotlight
    //I_result += CalcSpotLight(spotLight, N, V, diffColour, specColour);

    // ambient light
    I_result += I_a * diffColour * material.K_a;

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

    return I_diffuse + I_specular;
}

vec3 CalcPointLight(PointLight light, vec3 N, vec3 V, vec3 diffColour, vec3 specColour)
{
    //calc vectors
    vec3 L = normalize(light.position - wc_fragPos);
    vec3 R = reflect(-L, N);

    // attenuation
    float distance = length(light.position - wc_fragPos);
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
    vec3 L = normalize(light.position - wc_fragPos);
    vec3 R = reflect(-L, N);

    // attenuation
    float distance = length(light.position - wc_fragPos);
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
