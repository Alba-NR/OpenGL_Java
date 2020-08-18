#version 330 core
out vec4 FragColor;

uniform vec4 ourColor; // this uniform var is set in java code

void main()
{
    FragColor = ourColor;
}