#version 150 core
out vec4 outColor;

in vec2 texCoord;

uniform bool horizontal;
uniform float weight[5] = float[] (0.2, 0.2, 0.1, 0.07, 0.02);
uniform int postProcessingOn;
uniform sampler2D renderTargetTexture;

void main()
{
    if (postProcessingOn == 1) {
        vec2 tex_offset = 3.0 / textureSize(renderTargetTexture, 0);// gets size of single texel
        vec3 result = texture(renderTargetTexture, texCoord).rgb * weight[0];// current fragment's contribution
        if (horizontal)
        {
            for (int i = 1; i < 5; ++i)
            {
                result += texture(renderTargetTexture, texCoord + vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
                result += texture(renderTargetTexture, texCoord - vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
            }
        }
        else
        {
            for (int i = 1; i < 5; ++i)
            {
                result += texture(renderTargetTexture, texCoord + vec2(0.0, tex_offset.y * i)).rgb * weight[i];
                result += texture(renderTargetTexture, texCoord - vec2(0.0, tex_offset.y * i)).rgb * weight[i];
            }
        }
        outColor = vec4(result, 1.0);
    } else {
        vec4 textureColor = texture(renderTargetTexture, texCoord);
        outColor = textureColor;
    }
}

