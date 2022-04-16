#version 150
in vec2 inPosition;// input from the vertex buffer

uniform float time;// variable constant for all vertices in a single draw
uniform int type;
uniform mat4 view, projection, model;
uniform vec3 lightPos, lightPos2, cameraPos;

out vec2 texCoord;
out vec3 vertColor, normalOut, finalPosition, lightDirView, lightDirView2, lightDirWorld, lightDirWorld2, viewDir;
out vec4 posOut;



const float PI = 3.1415;

float getZ(vec2 vec) {
    return sin(vec.y * PI * 2);
}

vec3 getSphere(vec2 vec) {
    float az = vec.x * PI;// <-1;1> -> <-PI;PI>
    float ze = vec.y * PI / 2.0;// <-1;1> -> <-PI/2;PI/2>
    float r = 1.0;
    float x = r * cos(az) * cos(ze);
    float y = r * sin(az) * cos(ze);
    float z = r * sin(ze);
    return vec3(x, y, z);
}
vec3 getSphereNormal(vec2 vec, vec3 currentPosition) {
    vec3 tvaz = currentPosition - getSphere(vec - vec2(0.001, 0.0));
    vec3 tvze = currentPosition - getSphere(vec - vec2(0.0, 0.001));
    return cross(tvaz, tvze);
}

vec3 getGrid(vec2 vec) {
    float x = vec.x;
    float y = vec.y;
    return vec3(x*5, y*5, -1);
}

vec3 getGridNormal(vec2 vec, vec3 currentPosition) {
    vec3 tvx = currentPosition - getGrid(vec - vec2(0.001, 0.0));
    vec3 tvy = currentPosition - getGrid(vec - vec2(0.0, 0.001));
    return cross(tvx, tvy);
}

vec3 getHat(vec2 vec) {
    float a = vec.x * PI;
    float r = (vec.y + 1) * PI;

    float x = r * sin(a);
    float y = r * cos(a);
    float z = sin(r) - r / 2;
    return vec3(x, y, z);
}

vec3 getHatNormal(vec2 vec, vec3 currentPosition) {
    vec3 tvx = currentPosition - getHat(vec - vec2(0.001, 0.0));
    vec3 tvy = currentPosition - getHat(vec - vec2(0.0, 0.001));
    return cross(tvx, tvy);
}

vec3 getRing(vec2 vec) {
    float r = 2;
    float u = vec.x * PI;// <-1;1> -> <-PI;PI>
    float v = vec.y * PI;// <-1;1> -> <-PI;PI>
    float x = cos(u) * (1.5*r + cos(v));
    float y = sin(u) * (1.5*r + cos(v));
    float z = 0.2f * sin(v);

    return vec3(x, y, z);
}

vec3 getRingNormal(vec2 vec, vec3 currentPosition) {
    vec3 tvu = currentPosition - getRing(vec - vec2(0.001, 0.0));
    vec3 tvv = currentPosition - getRing(vec - vec2(0.0,0.001));
    return cross(tvu, tvv);
}

vec3 getSecondCylinder(vec2 vec) {
    float r = 3;
    float u = vec.x * PI/2;
    float v = vec.y * PI/2;
    float x = r*cos(u);
    float y = r*sin(u);
    float z = v;
    return vec3(x,y,z);
}

vec3 getSecondCylinderNormal(vec2 vec, vec3 currentPosition) {
    vec3 tvu = currentPosition - getSecondCylinder(vec - vec2(0.001, 0.0));
    vec3 tvv = currentPosition - getSecondCylinder(vec - vec2(0.0,0.001));
    return cross(tvu, tvv);
}

vec3 getFirstFunction(vec2 vec) {
    return vec3(vec.x + 2, vec.y + 2, getZ(vec));
}

vec3 getFirstFunctionNormal(vec2 vec, vec3 currentPosition) {
    vec3 tva = currentPosition - getFirstFunction(vec - vec2(0.001, 0.0));
    vec3 tvb = currentPosition - getFirstFunction(vec - vec2(0.0,0.001));
    return cross(tva, tvb);
}

vec3 getSecondFunction(vec2 vec) {
        return vec3(vec.x * PI, vec.y * PI, sin(vec.x * PI * cos(time/10)));
}

vec3 getSecondFunctionNormal(vec2 vec, vec3 currentPosition) {
    vec3 tva = currentPosition - getSecondFunction(vec - vec2(0.001, 0.0));
    vec3 tvb = currentPosition - getSecondFunction(vec - vec2(0.0,0.001));
    return cross(tva, tvb);
}


void main() {
    texCoord = inPosition;
    // grid je <0;1> - chci <-1;1>
    vec2 position = inPosition * 2 - 1;
    vec3 normal;
    normal = vec3(0.0, 0.0, 1.0);

    if (type == 1) {
        finalPosition = getSphere(position);
        normal = getSphereNormal(position, finalPosition);
    } else if (type == 2) {
        finalPosition = getFirstFunction(position);
        normal = getFirstFunctionNormal(position, finalPosition);
    } else if (type == 3) {
        finalPosition = getGrid(position);
        normal = getGridNormal(position, finalPosition);
    } else if (type == 4) {
        finalPosition = getHat(position);
        normal = getHatNormal(position, finalPosition);
    } else if (type == 5) {
        finalPosition = getRing(position);
        normal = getRingNormal(position, finalPosition);
    } else if (type== 6) {
        finalPosition = getSecondCylinder(position);
        normal = getSecondCylinderNormal(position, finalPosition);
    } else if (type== 7) {
        finalPosition = getSecondFunction(position);
        normal = getSecondFunctionNormal(position, finalPosition);
    } else if (type == 8) {
        finalPosition = getSphere(position);
        normal = getSphereNormal(position, finalPosition);
    }


    vec4 pos4 = vec4(finalPosition, 1.0);
    gl_Position = projection * view * model * pos4;
    posOut = pos4;
    // smer k pozorovteli
    viewDir = -(view * model * pos4).xyz;

    normalOut = transpose(inverse(mat3(view * model))) * normalize(normal);

    lightDirView = vec3(view * vec4(lightPos, 1.0)) + viewDir;
    lightDirView2 = vec3(view * vec4(lightPos2, 1.0)) + viewDir;
    lightDirWorld = lightPos - vec3(model * vec4(finalPosition, 1.0));
    lightDirWorld2 = lightPos2 - vec3(model * vec4(finalPosition, 1.0));



}
