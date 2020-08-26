#version 330 core
out vec4 FragColor;
in vec2 TexCoord;

uniform sampler2D texture1;
uniform sampler2D texture2;

void main()
{
    // mixture of the 2 textures (80% 1st input colour, 20% 2nd input colour):
    FragColor = mix(texture(texture1, TexCoord), texture(texture2, TexCoord), 0.2);
}