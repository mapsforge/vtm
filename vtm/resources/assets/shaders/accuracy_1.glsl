#ifdef GLES
precision mediump float;
#endif
uniform mat4 u_mvp;
uniform float u_phase;
uniform float u_scale;
attribute vec2 a_pos;
varying vec2 v_tex;

void main() {
    gl_Position = u_mvp * vec4(a_pos * u_scale * u_phase, 0.0, 1.0);
    v_tex = a_pos;
}

$$

#ifdef GLES
precision mediump float;
#endif
varying vec2 v_tex;
uniform float u_scale;
uniform int u_mode;
uniform vec4 u_color;

void main() {
    float len = 1.0 - length(v_tex);
    if (u_mode == -1) {
        gl_FragColor = u_color * len;
    } else {
        // outer ring
        float a = smoothstep(0.0, 2.0 / u_scale, len);
        // inner ring
        float b = 0.8 * smoothstep(3.0 / u_scale, 4.0 / u_scale, len);
        // - subtract inner from outer to create the outline
        a = a - b;
        gl_FragColor = u_color * a;
    }
}
