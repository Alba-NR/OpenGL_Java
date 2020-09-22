#version 330 core
layout (location = 0) in vec3 aPos;

out vec3 TexCoords;

uniform mat4 viewProjection_m;

void main()
{
    TexCoords = aPos;
    vec4 pos = viewProjection_m * vec4(aPos, 1.0);
    gl_Position = pos.xyww;
}