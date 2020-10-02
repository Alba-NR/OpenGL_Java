#version 330 core

in VS_OUT {
    vec2 TexCoords;   // texture UV coord
    vec3 wc_normal;  // fragment normal in world coord
    vec3 wc_fragPos; // fragment position in world coord
} fs_in;

out vec4 FragColor;

uniform vec3 wc_cameraPos;
uniform samplerCube skybox;

void main()
{
    vec3 N = normalize(fs_in.wc_normal);
    vec3 I = normalize(fs_in.wc_fragPos - wc_cameraPos);
    vec3 R = reflect(I, N);
    FragColor = vec4(texture(skybox, R).rgb, 1.0);
}