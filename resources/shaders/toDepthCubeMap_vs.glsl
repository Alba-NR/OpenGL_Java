#version 330 core

layout (location = 0) in vec3 aPos;

uniform mat4 model_m;

void main()
{
    gl_Position = model_m * vec4(aPos, 1.0);
}