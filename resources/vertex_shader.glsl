#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aColor;
layout (location = 2) in vec2 aTexCoord;

out vec3 ourColor; // output colour to fragment shader
out vec2 TexCoord; // output texture coord to frag sh.

uniform mat4 mvp_matrix; // MVP matrix

void main()
{
    gl_Position = mvp_matrix * vec4(aPos, 1.0);  // also used as output of shader
    ourColor = aColor;
    TexCoord = aTexCoord;
}