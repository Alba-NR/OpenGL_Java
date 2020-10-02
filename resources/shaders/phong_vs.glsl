#version 330 core

layout (location = 0) in vec3 oc_pos;       // vertex position in object coord
layout (location = 1) in vec3 oc_normal;    // vertex normal in obj coord
layout (location = 2) in vec2 aTexCoords;    // texture UV coord

out VS_OUT {
    vec2 TexCoords;   // texture UV coord
    vec3 wc_normal;  // fragment normal in world coord
    vec3 wc_fragPos; // fragment position in world coord
} vs_out;

uniform mat4 model_m;   // model matrix
uniform mat4 mvp_m;     // model-view-projection matrix
uniform mat4 normal_m;  // matrix to transform normal from oc to wc

void main()
{
    gl_Position = mvp_m * vec4(oc_pos, 1.0);            // also used as output of shader
    vs_out.wc_fragPos = vec3(model_m * vec4(oc_pos, 1.0));     // calculate fragment pos in wc
    vs_out.wc_normal = mat3(normal_m) * oc_normal;             // tranform normal from oc to wc
    vs_out.TexCoords = aTexCoords;
}