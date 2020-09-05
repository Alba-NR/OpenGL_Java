#version 330 core

struct Material {
    sampler2D diffuseColour;    // diffuse map (for diffuse colour)
    sampler2D specularColour;   // specular map (for specular reflection)
    float K_diff;       // diffuse coeff
    float K_spec;       // specular coeff
    float shininess;    // shininness coeff (for specular reflection)

};

struct Light { // flash light (spotlight)
    vec3 position;      // light pos in wc
    vec3 colour;        // light colour
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

uniform Material material;
uniform Light light;
uniform vec3 wc_cameraPos;

const vec3 I_a = vec3(1, 1, 1);     // ambient light intensity

void main()
{
    // point light intensity is smaller further away
    float distance = length(light.position - wc_fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));


    // get diffuse & specular colours from textures (the maps...)
    vec3 diffColour = vec3(texture(material.diffuseColour, TexCoord));
    vec3 specColour = vec3(texture(material.specularColour, TexCoord));

    // ambient reflection (- ambient colour same as diffuse)
    vec3 I_ambient = I_a * diffColour;

    // angles for cutoff of spotlight
    vec3 L = normalize(light.position - wc_fragPos);
    float theta = dot(L, normalize(-light.direction));
    float I = clamp((theta - light.outerCutoffCosine) / (light.cutoffCosine - light.outerCutoffCosine), 0.0, 1.0); // clamp values to [0.0, 1.0] range

    // diffuse reflection
    vec3 N = normalize(wc_normal);
    vec3 I_diffuse = light.colour * diffColour * material.K_diff * max(dot(N, L), 0.0);

    // specular reflection
    vec3 V = normalize(wc_cameraPos - wc_fragPos);
    vec3 R = reflect(-L, N);
    vec3 I_specular = light.colour * specColour * material.K_spec * pow(max(dot(V, R), 0.0), material.shininess);

    // attenuation
    I_ambient *= attenuation;
    I_diffuse *= attenuation * I;
    I_specular *= attenuation * I;

    vec3 I_result = I_ambient + I_diffuse + I_specular;
    FragColor = vec4(I_result, 1.0);
}

