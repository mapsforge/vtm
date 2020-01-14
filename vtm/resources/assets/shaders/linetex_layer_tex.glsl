#ifdef GLES
precision highp float;
#endif
uniform mat4 u_mvp;
uniform vec4 u_color;
uniform float u_pscale;
uniform float u_width;
attribute vec4 a_pos0; // x:posX, y:posY, z:extrusX, w:extrusY
attribute vec4 a_pos1;
attribute vec2 a_len0; // x:lineLength, y:unused
attribute vec2 a_len1;
attribute float a_flip; // flip state
varying vec2 v_st;

void main() {
    vec4 pos;
    if (a_flip == 0.0) {
        //    vec2 dir = u_width * a_pos0.zw;
        pos = vec4(a_pos0.xy + (u_width * a_pos0.zw), 0.0, 1.0);
        v_st = vec2(a_len0.x / u_pscale, 1.0);
    } else {
        //    vec2 dir = u_width * a_pos1.zw;
        pos = vec4(a_pos1.xy - (u_width * a_pos1.zw), 0.0, 1.0);
        v_st = vec2(a_len1.x / u_pscale, -1.0);
    }
    gl_Position = u_mvp * pos;
}

$$

#ifdef GL_OES_standard_derivatives
#extension GL_OES_standard_derivatives : enable
#endif
#ifdef GLES
precision highp float;
#endif
uniform vec4 u_color;
uniform vec4 u_bgcolor;
uniform float u_pwidth;
varying vec2 v_st;
uniform sampler2D u_tex;
uniform int u_mode;

void main() {
    if (u_mode >= 1) {
        /* Dash array or texture */
        float step = 2.0;
        if (u_mode == 2) { // dashed texture
            step = 1.0;
        }
        // use lineLength mod texture step (mod is always positive)
        // add 1.0 to avoid static line textures while zooming
        vec4 c = texture2D(u_tex, vec2(mod(v_st.s + 1.0, step), (v_st.t + 1.0) * 0.5));
        float fuzz = fwidth(c.a);
        gl_FragColor = (c * u_color) * smoothstep(0.5 - fuzz, 0.5 + fuzz, c.a);
    } else {
        /* No dash array or texture */
        /* distance on perpendicular to the line */
        float dist = abs(v_st.t);
        float fuzz = fwidth(v_st.t);
        float fuzz_p = fwidth(v_st.s);
        float line_w = smoothstep(0.0, fuzz, 1.0 - dist);
        float stipple_w = smoothstep(0.0, fuzz, u_pwidth - dist);
        /* triangle waveform in the range 0..1 for regular pattern */
        float phase = abs(mod(v_st.s, 2.0) - 1.0);
        /* interpolate between on/off phase, 0.5 = equal phase length */
        float stipple_p = smoothstep(0.5 - fuzz_p, 0.5 + fuzz_p, phase);
        gl_FragColor = line_w * mix(u_bgcolor, u_color, min(stipple_w, stipple_p));
    }
}
