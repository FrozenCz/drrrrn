#version 150

in vec2 texCoord;
in vec3 vertColor, normalOut, finalPosition, lightDirView, lightDirView2, lightDirWorld, lightDirWorld2, viewDir;
in vec4 posOut;

uniform float ambientStrength, diffuseStrength, specularStrength, spotCutOff;
uniform int type, elementsFillMode, lightOn;
uniform sampler2D texturePointer;
uniform vec3 lightColor, cameraPos, elementColor;

out vec4 outColor;// output from the fragment shader

void main() {
    vec3 color, resultColor;
    vec3 normalizedNormal = normalize(normalOut);
    const float lightConstant = 1.0f;
    const float lightLinear = 0.07f;
    const float attQuad = 0.017f;
    float lightDistance = length(lightDirWorld);
    const float PI = 3.1415;

    switch (elementsFillMode) {
        case 0:
        // texture
        color = vec3(texture(texturePointer, texCoord));
        break;
        case 1:
        // position
        color = normalize(finalPosition);
        break;
        case 2:
        // depth
        color = vec3(gl_FragCoord.w * 5);
        break;
        case 3:
        // normals
        color = normalizedNormal;
        break;
        case 4:
        // coords to texture
        color = vec3(texCoord, 1);
        break;
        case 5:
        // light distance
        color = vec3(1- lightDistance / 30);
        break;
        case 6:
        color = elementColor;
        break;
    }


    if (lightOn == 1) {
        float att = 32.0 / (lightConstant + lightLinear * lightDistance + attQuad * (lightDistance * lightDistance));
        vec3 ambient = ambientStrength * lightColor;
        vec3 lightDir = normalize(lightDirView);
        vec3 lightDir2 = normalize(lightDirView2);
        vec3 sptDir;
        vec3 viewDir = normalize(viewDir);

        if (type == 8) {
            lightDir = lightDir + 2*PI;
            viewDir = viewDir + 2*PI;
        }

        float diff = max(dot(normalizedNormal, normalize(lightDir)), 0.0);
        float diff2 = max(dot(normalizedNormal, normalize(lightDir2)), 0.0);
        vec3 diffuse = diffuseStrength * (diff2) * lightColor;

        // stred mezi smerem k pozorvali a ke svetlu
        vec3 halfWayDir = normalize((lightDir + lightDir2) + viewDir);
        float specAngle = max(dot(normalizedNormal, halfWayDir), 0.0);
        float spec = pow(specAngle, 50);

        if (type == 8) {
            spec = 1;
        }
        vec3 specular = specularStrength * spec * lightColor;
        float spotEffect = max(dot(normalize(cameraPos), normalize(- lightDirWorld)), 0);
        float spotEffect2 = max(dot(normalize(cameraPos), normalize(- lightDirWorld2)), 0);

        if ((spotEffect > spotCutOff) || (spotEffect2 > spotCutOff)  || type == 8) {
            float blend = clamp((spotEffect - spotCutOff) / (1 - spotCutOff), 0.0, 1.0);
            float blend2 = clamp((spotEffect2 - spotCutOff) / (1 - spotCutOff), 0.0, 1.0);
            resultColor = mix(ambient, ambient + att * (diffuse + specular), (blend + blend2));
        } else resultColor = ambient;
        outColor = vec4(resultColor * color, 1.0);

        
    } else {
        outColor = vec4(color, 1.0);
    }


}
