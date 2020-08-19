#version 330 core
out vec4 FragColor;
in vec3 ourColor;
in vec2 TexCoord;

uniform sampler2D texture1;
uniform sampler2D texture2;

void main()
{
    //FragColor = vec4(ourColor, 1.0);
    //FragColor = texture(texture1, TexCoord); // sample the colour of the texture
    //FragColor = texture(texture1, TexCoord) * vec4(ourColor, 1.0); // mixing texture colour & vertex colour -> rainbow wooden box!:)
    // mixture of the 2 textures (80% 1st input colour, 20% 2nd input colour):
    FragColor = mix(texture(texture1, TexCoord), texture(texture2, TexCoord), 0.2);
}