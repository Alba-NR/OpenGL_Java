#version 330 core

in vec3 TexCoords;  // direction vector representing 3D texture coord

out vec4 FragColor;

uniform samplerCube skybox;

void main()
{
    FragColor = texture(skybox, TexCoords);
}