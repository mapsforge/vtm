#ifdef GLES
precision highp float;
#endif
uniform mat4 u_mvp;
uniform vec4 u_color[4];
uniform int u_mode;
uniform float u_alpha;
uniform vec3 u_light;
uniform float u_zlimit;
attribute vec4 a_pos;
attribute vec2 a_normal;
varying vec4 color;
//varying float depth;
const float ff = 255.0;

#ifdef SHADOW
uniform mat4 u_light_mvp;
varying vec4 v_shadow_coords;
#endif

/**
 * The diffuse of surface dependent on the light position.
 *
 * @param r_norm the normal vector of vertex's face
 */
float diffuse(in vec3 r_norm, out bool hasLight) {
    float l = dot(normalize(r_norm), normalize(u_light));
    hasLight = l > 0.0;
    l = clamp((1.0 + l) / 2.0, 0.0, 1.0);
    #ifdef SHADOW
    if (hasLight) {
        //  l = (l + (1.0 - r_norm.z))*0.5;
        l = 0.8 + l * 0.2;
    } else {
        l = 0.5 + l * 0.3;
    }
        #else
    l = 0.8 + l * 0.2;
    #endif
    return (l);
}

void main() {
    // change height by u_alpha
    float height = a_pos.z * u_alpha;
    if (height > u_zlimit) {
        height = u_zlimit;
    }
    gl_Position = u_mvp * vec4(a_pos.xy, height, 1.0);
    bool hasLight = false;

    //depth = gl_Position.z;
    if (u_mode == -1) {
        ;
    } else if (u_mode >= 0 && u_mode <= 2) {
        vec3 r_norm;
        if (u_mode == 0) {
            // roof / depth pass
            r_norm = vec3(0.0, 0.0, 1.0);
            color = u_color[0] * u_alpha;
            color.rgb *= diffuse(r_norm, hasLight);
        } else {
            float lightX = u_mode == 1 ? a_normal.y : a_normal.x;
            r_norm.x = (lightX / ff) * 2.0 - 1.0;
            // normal points y left or right (1 or -1)
            float dir = -1.0 + (2.0 * abs(mod(lightX, 2.0)));
            // recreate y vector
            r_norm.y = dir * sqrt(clamp(1.0 - (r_norm.x * r_norm.x), 0.0, 1.0));
            r_norm.z = 0.0;

            float z = (0.98 + gl_Position.z * 0.02);
            float h = 0.9 + clamp(a_pos.z / 2000.0, 0.0, 0.1);
            if (u_mode == 1) {
                // sides 1 - use 0xff00
                color = u_color[1];
            } else {
                // sides 2 - use 0x00ff
                color = u_color[2];
            }
            color.rgb *= diffuse(r_norm, hasLight) * z * h;
        }
        color *= u_alpha;
    } else if (u_mode == 3) {
        // outline
        float z = (0.98 - gl_Position.z * 0.02);
        color = u_color[3] * z;
    }
        #ifdef SHADOW
    if (hasLight) {
        vec4 positionFromLight = u_light_mvp * a_pos;
        v_shadow_coords = (positionFromLight / positionFromLight.w);
    } else {
        // Discard shadow on unlighted faces
        v_shadow_coords = vec4(-1.0);
    }
        #endif
}

$$

#ifdef GLES
precision highp float;
#endif
varying vec4 color;

#ifdef SHADOW
varying vec4 v_shadow_coords; // the coords in shadow map

uniform sampler2D u_shadowMap;
uniform vec4 u_lightColor;
uniform float u_shadowRes;

const bool DEBUG = false;

const float transitionDistance = 0.05; // relative transition distance at the border of shadow tex
const float minTrans = 1.0 - transitionDistance;

const int pcfCount = 2; // the number of surrounding pixels to smooth shadow
const float biasOffset = 0.005; // offset to remove shadow acne
const float pcfTexels = float((pcfCount * 2 + 1) * (pcfCount * 2 + 1));

#if GLVERSION == 20
float decodeFloat (vec4 color) {
    const vec4 bitShift = vec4(
    1.0 / (256.0 * 256.0 * 256.0),
    1.0 / (256.0 * 256.0),
    1.0 / 256.0,
    1.0
    );
    return dot(color, bitShift);
}
    #endif
    #endif

void main() {
    #ifdef SHADOW
    float shadowX = abs((v_shadow_coords.x - 0.5) * 2.0);
    float shadowY = abs((v_shadow_coords.y - 0.5) * 2.0);
    if (shadowX > 1.0 || shadowY > 1.0) {
        // Outside the light texture set to 0.0
        gl_FragColor = vec4(color.rgb * u_lightColor.rgb, color.a);
        if (DEBUG) {
            gl_FragColor = vec4(0.0, 1.0, 0.0, 0.1);
        }
    } else {
        // Inside set to 1.0; make a transition to the border
        float shadowOpacity = (shadowX < minTrans && shadowY < minTrans) ? 1.0 :
        (1.0 - (max(shadowX - minTrans, shadowY - minTrans) / transitionDistance));
        float distanceToLight = clamp(v_shadow_coords.z - biasOffset, 0.0, 1.0); // avoid unexpected shadow

        // smooth shadow at borders
        float shadowDiffuse = 0.0;
        float texelSize = 1.0 / u_shadowRes;
        for (int x = -pcfCount; x <= pcfCount; x++) {
            for (int y = -pcfCount; y <= pcfCount; y++) {
                #if GLVERSION == 20
                float depth = decodeFloat(texture2D(u_shadowMap, v_shadow_coords.xy + vec2(x, y) * texelSize));
                #else
                float depth = texture2D(u_shadowMap, v_shadow_coords.xy + vec2(x, y) * texelSize).r;
                #endif
                if (distanceToLight > depth) {
                    shadowDiffuse += 1.0;
                }
            }
        }
        shadowDiffuse /= pcfTexels;
        shadowDiffuse *= shadowOpacity;

        if (DEBUG && shadowDiffuse < 1.0) {
            gl_FragColor = vec4(shadowDiffuse, color.gb, 0.1);
        } else {
            gl_FragColor = vec4((color.rgb * u_lightColor.rgb) * (1.0 - u_lightColor.a * shadowDiffuse), color.a);
        }
    }
        #else
    gl_FragColor = color;
    #endif
}
