#ifdef GLES
precision highp float;
#endif
attribute vec4 a_pos;
uniform mat4 u_mvp;
uniform mat4 u_light_mvp;

varying vec4 v_shadow_coords;

void main(void) {
    gl_Position = u_mvp * a_pos;
    vec4 positionFromLight = u_light_mvp * a_pos;
    v_shadow_coords = (positionFromLight / positionFromLight.w);
}

$$

#ifdef GLES
precision highp float;
#endif
varying vec4 v_shadow_coords;

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

void main() {
    float shadowX = abs((v_shadow_coords.x - 0.5) * 2.0);
    float shadowY = abs((v_shadow_coords.y - 0.5) * 2.0);
    if (shadowX > 1.0 || shadowY > 1.0) {
        // Outside the light texture set to 0.0
        gl_FragColor = vec4(u_lightColor.rgb, 1.0);
        if (DEBUG) {
            gl_FragColor = vec4(0.0, 1.0, 0.0, 0.1);
        }
    } else {
        // Inside set to 1.0; make a transition to the border
        float shadowOpacity = (shadowX < minTrans && shadowY < minTrans) ? 1.0 :
        (1.0 - (max(shadowX - minTrans, shadowY - minTrans) / transitionDistance));
        #if GLVERSION == 20
        float distanceToLight = v_shadow_coords.z - biasOffset; // remove shadow acne
        #else
        float distanceToLight = v_shadow_coords.z; // remove shadow acne
        #endif
        distanceToLight = clamp(distanceToLight, 0.0, 1.0); // avoid unexpected far shadow
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
            gl_FragColor = vec4(shadowDiffuse, 0.0, 0.0, 0.1);
        } else {
            gl_FragColor = vec4(u_lightColor.rgb * (1.0 - u_lightColor.a * shadowDiffuse), 1.0);
        }
    }
}
