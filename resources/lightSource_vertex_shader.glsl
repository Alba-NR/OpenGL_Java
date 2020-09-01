#version 330 core

layout (location = 0) in vec3 oc_pos;

uniform mat4 mvp_m; // model-view-projection matrix

void main()
{
    gl_Position = mvp_m * vec4(oc_pos, 1.0);  // also used as output of shader
}