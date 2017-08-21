#ifdef GLES
precision highp float;
#endif
attribute vec2 vertex;
attribute vec2 tex_coord;
uniform mat4 u_mvp;
varying vec2 tex_c;
void
main(){
  gl_Position = u_mvp * vec4(vertex, 0.0, 1.0);
  tex_c = tex_coord;
}

$$

#ifdef GLES
precision highp float;
#endif
uniform sampler2D tex;
uniform float u_alpha;
varying vec2 tex_c;
void
main(){
  gl_FragColor = texture2D(tex, tex_c) * u_alpha;
}
