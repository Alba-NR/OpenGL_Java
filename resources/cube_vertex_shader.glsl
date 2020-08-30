#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec2 aTexCoord;

out vec2 TexCoord; // outputs to frag sh.
out vec3 Normal;
out vec3 FragPos;

uniform mat4 model_m;   // model matrix
uniform mat4 view_m;    // view matrix
uniform mat4 proj_m;    // projection matrix

void main()
{
    gl_Position = proj_m * view_m * model_m * vec4(aPos, 1.0);  // also used as output of shader
    FragPos = vec3(model_m * vec4(aPos, 1.0));
    TexCoord = aTexCoord;
    Normal = mat3(transpose(inverse(model_m))) * aNormal;
}