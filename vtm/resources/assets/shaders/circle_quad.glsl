#ifdef GLES
precision highp float;
#endif
uniform mat4 u_mvp;
attribute vec2 a_pos;
varying vec2 v_pos;

void main() {
    gl_Position = u_mvp * vec4(a_pos, 0.0, 1.0);
    v_pos = a_pos;
}

$$

#ifdef GLES
precision highp float;
#endif
uniform vec4 u_fill;
uniform float u_radius;
uniform vec4 u_stroke;
uniform float u_width;
varying vec2 v_pos;

void main() {
    gl_FragColor = u_fill;
}
