#ifdef GLES
// highp is necessary to not loose texture coordinate bits
precision highp float;
#endif
uniform mat4 u_mvp;
// uniform mat4 u_vp;
// factor to increase line width relative to scale
uniform float u_width;
// xy hold position, zw extrusion vector
attribute vec4 a_pos;
uniform float u_height;
varying vec2 v_st;

void main() {
    // scale extrusion to u_width pixel
    // just ignore the two most insignificant bits.
    vec2 dir = a_pos.zw;
    gl_Position = u_mvp * vec4(a_pos.xy + (u_width * dir), u_height, 1.0);

    // last two bits hold the texture coordinates.
    v_st = abs(mod(dir, 4.0)) - 1.0;
}

$$

#ifdef GL_OES_standard_derivatives
#extension GL_OES_standard_derivatives : enable
#endif
#ifdef GLES
precision highp float;
#endif
uniform sampler2D u_tex;
uniform int u_mode;
uniform vec4 u_color;
uniform float u_fade;
varying vec2 v_st;

void main() {
    float len;
    float fuzz;
    if (u_mode == 2) {
        /* round cap line */
#ifdef DESKTOP_QUIRKS
        len = length(v_st);
#else
        len = texture2D(u_tex, v_st).a;
#endif
        vec2 st_width = fwidth(v_st);
        fuzz = max(st_width.s, st_width.t);
    } else {
        /* flat cap line */
        len = abs(v_st.s);
        fuzz = fwidth(v_st.s);
    }
    // u_mode == 0 -> thin line
    // len = len * clamp(float(u_mode), len, 1.0);
    if (fuzz > 2.0)
        gl_FragColor = u_color * 0.5;
    else
        gl_FragColor = u_color * clamp((1.0 - len) / max(u_fade, fuzz), 0.0, 1.0);
        //gl_FragColor = u_color * clamp((1.0 - len), 0.0, 1.0);
}
