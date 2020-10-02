#version 330 core

in vec2 TexCoords;

out vec4 FragColor;

uniform sampler2D screenTexture;
uniform float[9] kernel3x3;
uniform int effectToUse; // 0 <=> none, 1 <=> invert, 2 <=> greyscale, 3 <=> apply 3x3 kernel

// post-processing effects
vec3 calcInverseColour(vec3 colour);
vec3 calcGreyscale(vec3 colour);
vec3 apply3x3Kernel(float[9] kernel);

const float offset = 1.0 / 300.0;

void main()
{
    switch(effectToUse){
        case 0: // none
            FragColor = texture(screenTexture, TexCoords);
            break;
        case 1: // invert colours
            FragColor = vec4(calcInverseColour(texture(screenTexture, TexCoords).rbg), 1.0);
            break;
        case 2: // greyscale
            FragColor = vec4(calcGreyscale(texture(screenTexture, TexCoords).rgb), 1.0);
            break;
        case 3: // apply 3x3 kernel
            FragColor = vec4(apply3x3Kernel(kernel3x3), 1.0);
            break;
    }
}

vec3 calcInverseColour(vec3 colour){
    return 1.0 - colour;
}

vec3 calcGreyscale(vec3 colour){
    float average = 0.2126 * colour.r + 0.7152 * colour.g + 0.0722 * colour.b;  // calc weighted average
    return vec3(average);
}


vec3 apply3x3Kernel(float[9] kernel){

    vec2 offsets[9] = vec2[](
        vec2(-offset,  offset), // top-left
        vec2( 0.0f,    offset), // top-center
        vec2( offset,  offset), // top-right
        vec2(-offset,  0.0f),   // center-left
        vec2( 0.0f,    0.0f),   // center-center
        vec2( offset,  0.0f),   // center-right
        vec2(-offset, -offset), // bottom-left
        vec2( 0.0f,   -offset), // bottom-center
        vec2( offset, -offset)  // bottom-right
    );

    vec3 sampleTex[9];
    for(int i = 0; i < 9; i++) {
        sampleTex[i] = vec3(texture(screenTexture, TexCoords + offsets[i]));
    }
    vec3 colour = vec3(0.0);
    for(int i = 0; i < 9; i++) colour += sampleTex[i] * kernel[i];

    return colour;
}


