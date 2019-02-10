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

/**
 * The diffuse of surface dependent on the light position
 *
 * @param r_norm - the normal vector of vertex's face
 */
float diffuse(in vec3 r_norm) {
    float l = dot(normalize(r_norm), normalize(u_light));
    l = clamp((1.0 + l) / 2.0, 0.0, 1.0);
    return(0.8 + l * 0.2);
}

void main() {
    // change height by u_alpha
    float height = a_pos.z * u_alpha;
    if (height > u_zlimit) {
        height = u_zlimit;
    }
    gl_Position = u_mvp * vec4(a_pos.xy, height, 1.0);
    //depth = gl_Position.z;
    if (u_mode == -1) {
        ;
    } else if (u_mode >= 0 && u_mode <= 2) {
        vec3 r_norm;
        if (u_mode == 0) {
            // roof / depth pass
            r_norm = vec3(0.0, 0.0, 1.0);
            color = u_color[0] * u_alpha;
            color.rgb *= diffuse(r_norm);
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
            color.rgb *= diffuse(r_norm) * z * h;
        }
        color *= u_alpha;
    } else if (u_mode == 3) {
        // outline
        float z = (0.98 - gl_Position.z * 0.02);
        color = u_color[3] * z;
    }
}

$$

#ifdef GLES
precision highp float;
#endif
varying vec4 color;

void main() {
    gl_FragColor = color;
}
