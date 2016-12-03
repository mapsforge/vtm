#ifdef GLES
precision highp float;
#endif
uniform mat4 u_mvp;
uniform float u_radius;
uniform vec2 u_screen;
attribute vec2 a_pos;
varying vec2 v_tex;

void
main(){
  gl_Position = u_mvp * vec4(a_pos, 0.0, 1.0);
  v_tex = a_pos;
}

$$

#ifdef GLES
precision highp float;
#endif
varying vec2 v_tex;
uniform float u_radius;
uniform vec4 u_color;
uniform vec2 u_screen;

void
main(){
  float len = 1.0 - length(v_tex - u_screen);
  gl_FragColor = u_color * smoothstep(0.0, u_radius, len);
}
