#version 330 core

in vec2 TexCoord;   // texture UV coord
in vec3 wc_normal;  // fragment normal in world coord
in vec3 wc_fragPos; // fragment position in world coord

out vec4 FragColor;

uniform vec3 wc_cameraPos;
uniform samplerCube skybox;
uniform float refractiveIndex;

void main()
{
    vec3 N = normalize(wc_normal);
    vec3 I = normalize(wc_fragPos - wc_cameraPos);
    float ratio = 1.0 / refractiveIndex;
    vec3 R = refract(I, N, ratio);
    FragColor = vec4(texture(skybox, R).rgb, 1.0);
}