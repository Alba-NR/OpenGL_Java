#version 330 core

struct Material {
    sampler2D diffuseColour;  // diffuse map (for diffuse colour)
    vec3 specularColour;      // colour of specular reflection
    float K_diff;       // diffuse coeff
    float K_spec;       // specular coeff
    float shininess;    // shininness coeff (for specular reflection)

};

struct Light {
    vec3 position;      // light pos in wc
    vec3 colour;        // light colour
    float intensity;    // magnitude of light's intensity (not colour)
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
    float I = light.intensity / (radians(180) * 4 * pow(distance, 2));

    // get diffuse colour (- ambient colour same as diffuse)
    vec3 diffColour = vec3(texture(material.diffuseColour, TexCoord));

    // ambient reflection
    vec3 I_ambient = I_a * diffColour;

    // diffuse reflection
    vec3 N = normalize(wc_normal);
    vec3 L = normalize(light.position - wc_fragPos);
    vec3 I_diffuse = light.colour * I * diffColour * material.K_diff * max(dot(N, L), 0.0);

    // specular reflection
    vec3 V = normalize(wc_cameraPos - wc_fragPos);
    vec3 R = reflect(-L, N);
    vec3 I_specular = light.colour * I * material.specularColour * material.K_spec * pow(max(dot(V, R), 0.0), material.shininess);

    vec3 I_result = I_ambient + I_diffuse + I_specular;
    FragColor = vec4(I_result, 1.0);
}

