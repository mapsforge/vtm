#ifdef GLES
precision highp float;
#endif
uniform mat4 u_mvp;
uniform vec2 u_scale;
attribute vec4 a_pos;
varying vec2 v_st;
varying vec2 v_st2;

void main() {
    v_st = clamp(a_pos.xy, 0.0, 1.0) * (2.0 / u_scale.y);
    v_st2 = clamp(a_pos.xy, 0.0, 1.0) * (4.0 / u_scale.y);
    gl_Position = u_mvp * a_pos;
}

$$

#ifdef GLES
precision highp float;
#endif
uniform vec4 u_color;
uniform sampler2D u_tex;
uniform vec2 u_scale;
varying vec2 v_st;
varying vec2 v_st2;

void main() {
    gl_FragColor = mix(texture2D(u_tex, v_st), texture2D(u_tex, v_st2), u_scale.x);
}
