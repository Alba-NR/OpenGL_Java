#version 330 core
in vec2 TexCoord;
in vec3 Normal;
in vec3 FragPos;

out vec4 FragColor;

uniform sampler2D texture;
uniform vec3 objectColor;
uniform vec3 lightColor;
uniform vec3 lightPos;
uniform vec3 viewPos;

const float ambientCoeff = 0.1;
const float diffuseCoeff = 1.0;
const float specularCoeff = 0.5;

void main()
{
    // ambient reflection
    vec3 ambient = ambientCoeff * lightColor;

    // diffuse reflection
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(lightPos - FragPos);
    vec3 diffuse = lightColor * max(dot(norm, lightDir), 0.0);

    // specular reflection
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    vec3 specular = specularCoeff * lightColor * pow(max(dot(viewDir, reflectDir), 0.0), 32);

    vec3 result = (ambient + diffuse + specular) * objectColor;
    // mixture of texture & obj colour w/lighting (80% 1st input colour, 20% 2nd input colour):
    FragColor = mix(texture(texture, TexCoord), vec4(result, 1.0), 0.5f);
}

