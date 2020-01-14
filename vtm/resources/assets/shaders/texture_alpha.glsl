#ifdef GLES
precision highp float;
#endif
attribute vec2 a_pos;
attribute vec2 a_tex_coord;
uniform mat4 u_mvp;
varying vec2 tex_c;

void main() {
    gl_Position = u_mvp * vec4(a_pos, 0.0, 1.0);
    tex_c = a_tex_coord;
}

$$

#ifdef GLES
precision highp float;
#endif
uniform sampler2D u_tex;
uniform float u_alpha;
varying vec2 tex_c;

void main() {
    gl_FragColor = texture2D(u_tex, tex_c) * u_alpha;
}
