#version 330 core
in vec2 TexCoord;
in vec3 Normal;
in vec3 FragPos;

out vec4 FragColor;

uniform sampler2D texture;
uniform vec3 objectColor;
uniform vec3 lightColor;
uniform vec3 lightPos;

const float ambientCoeff = 0.1;

void main()
{
    // ambient reflection
    vec3 ambient = ambientCoeff * lightColor;

    // diffuse reflection
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(lightPos - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    vec3 result = (ambient + diffuse) * objectColor;
    // mixture of texture & obj colour w/lighting (80% 1st input colour, 20% 2nd input colour):
    FragColor = mix(texture(texture, TexCoord), vec4(result, 1.0), 1.0f);
}

