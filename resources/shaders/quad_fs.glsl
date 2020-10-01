#version 330 core

out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D screenTexture;

// post-processing effects
vec3 calcInverseColour(vec3 colour);
vec3 calcGreyscale(vec3 colour);
vec3 apply3x3Kernel(float[9] kernel);

const float offset = 1.0 / 300.0;

void main()
{
    //vec3 texColour = texture(screenTexture, TexCoords).rgb;
    //FragColor = vec4(calcGreyscale(texColour), 1.0);

    float kernel[9] = float[](
        1, 1, 1,
        1, -8, 1,
        1, 1, 1
    );

    FragColor = vec4(apply3x3Kernel(kernel), 1.0);
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


