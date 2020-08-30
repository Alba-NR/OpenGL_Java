#version 330 core
out vec4 FragColor;
in vec2 TexCoord;

uniform sampler2D texture;
uniform vec3 objectColor;
uniform vec3 lightColor;

void main()
{
    // mixture of texture & obj colour (80% 1st input colour, 20% 2nd input colour):
    FragColor = mix(texture(texture, TexCoord), vec4(lightColor * objectColor, 1.0), 0.2);
}

