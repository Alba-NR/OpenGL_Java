#version 330 core

in vec2 TexCoord;   // texture UV coord
in vec3 wc_normal;  // fragment normal in world coord
in vec3 wc_fragPos; // fragment position in world coord

out vec4 FragColor;

uniform vec3 wc_cameraPos;
uniform samplerCube skybox;

void main()
{
    vec3 N = normalize(wc_normal);
    vec3 I = normalize(wc_fragPos - wc_cameraPos);
    vec3 R = reflect(I, N);
    FragColor = vec4(texture(skybox, R).rgb, 1.0);
}