#ifdef GLES
precision highp float;
#endif
uniform mat4 u_mvp;
uniform vec4 u_color[4];
uniform int u_mode;
uniform float u_alpha;
attribute vec4 a_pos;
attribute vec2 a_light;
varying vec4 color;
varying float depth;
const float ff = 255.0;
void
main(){
  //   change height by u_alpha
  gl_Position = u_mvp * vec4(a_pos.xy, a_pos.z * u_alpha, 1.0);
  //  depth = gl_Position.z;
  if (u_mode == -1) {
    ;
    //     roof / depth pass
    //    color = u_color[0] * u_alpha;
  }
  else if (u_mode == 0) {
    //     roof / depth pass
    color = u_color[0] * u_alpha;
  }
  else if (u_mode == 1) {
    //     sides 1 - use 0xff00
    //     scale direction to -0.5<>0.5
    float dir = a_light.y / ff;
    float z = (0.98 + gl_Position.z * 0.02);
    float h = 0.9 + clamp(a_pos.z / 2000.0, 0.0, 0.1);
    color = u_color[1];
    color.rgb *= (0.8 + dir * 0.2) * z * h;
    color *= u_alpha;
  }
  else if (u_mode == 2) {
    //     sides 2 - use 0x00ff
    float dir = a_light.x / ff;
    float z = (0.98 + gl_Position.z * 0.02);
    float h = 0.9 + clamp(a_pos.z / 2000.0, 0.0, 0.1);
    color = u_color[2];
    color.rgb *= (0.8 + dir * 0.2) * z * h;
    color *= u_alpha;
  }
  else if (u_mode == 3) {
    //     outline
    float z = (0.98 - gl_Position.z * 0.02);
    color = u_color[3] * z;
  }
}

$$

#ifdef GLES
precision highp float;
#endif
varying vec4 color;
void
main(){
  gl_FragColor = color;
}
