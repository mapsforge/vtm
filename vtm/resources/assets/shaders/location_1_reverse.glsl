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
uniform vec2 u_dir;
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
        float b = 0.5 * smoothstep(4.0 / u_scale, 5.0 / u_scale, len);
        // center point
        float c = 0.5 * (1.0 - smoothstep(14.0 / u_scale, 16.0 / u_scale, 1.0 - len));
        vec2 dir = normalize(v_tex);
        float d = dot(dir, u_dir);
        // 0.5 width of viewshed
        d = clamp(step(0.5, d), 0.4, 0.7);
        // - subtract inner from outer to create the outline
        // - multiply by viewshed
        // - add center point
        a = d * (a - (b + c)) + c;
        gl_FragColor = u_color * a;
    }
}
