#version 330 core

layout (location = 0) in vec3 aPos;

uniform mat4 lightSpace_m;
uniform mat4 model_m;

void main()
{
    gl_Position = lightSpace_m * model_m * vec4(aPos, 1.0);
}