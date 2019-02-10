#ifdef GLES
precision highp float;
#endif
uniform vec2 u_pixel;
attribute vec4 a_pos;
varying vec2 tex_pos;

void main() {
    gl_Position = a_pos;
    tex_pos = (a_pos.xy + 1.0) * 0.5;
}

$$

#ifdef GLES
precision highp float;
#endif
uniform sampler2D u_texColor;
uniform vec2 u_pixel;
varying vec2 tex_pos;

void main() {
    gl_FragColor = texture2D(u_texColor, tex_pos) * 0.8;
}
