#ifdef GLES
precision highp float;
#endif
attribute vec4 vertex;
attribute vec2 tex_coord;
uniform mat4 u_mv;
uniform mat4 u_proj;
uniform float u_scale;
uniform float u_coord_scale;
uniform vec2 u_div;
varying vec2 tex_c;
void
main(){
  vec4 pos;
  vec2 dir = vertex.zw;
  float coord_scale = 1.0 / u_coord_scale;
  if (abs(mod(vertex.x, 2.0)) == 0.0) {
    pos = u_proj * (u_mv * vec4(vertex.xy + dir * u_scale, 0.0, 1.0));
  }
  else { // place as billboard
    vec4 center = u_mv * vec4(vertex.xy, 0.0, 1.0);
    pos = u_proj * (center + vec4(dir * coord_scale, 0.0, 0.0));
  }
  gl_Position = pos;
  tex_c = tex_coord * u_div;
}

$$

#ifdef GLES
precision highp float;
#endif
uniform sampler2D tex;
varying vec2 tex_c;
void
main(){
  gl_FragColor = texture2D(tex, tex_c.xy);
}
