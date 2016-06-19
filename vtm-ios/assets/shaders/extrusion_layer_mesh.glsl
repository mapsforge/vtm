#ifdef GLES
precision highp float;
#endif
uniform mat4 u_mvp;
uniform vec4 u_color;
uniform float u_alpha;
attribute vec4 a_pos;
attribute vec2 a_light;
varying vec4 color;
varying float depth;
const float alpha = 1.0;
void
main(){
  //   change height by u_alpha
  vec4 pos = a_pos;
  pos.z *= u_alpha;
  gl_Position = u_mvp * pos;
  //   normalize face x/y direction
  vec2 enc = (a_light / 255.0);
  vec2 fenc = enc * 4.0 - 2.0;
  float f = dot(fenc, fenc);
  float g = sqrt(1.0 - f / 4.0);
  vec3 r_norm;
  r_norm.xy = fenc * g;
  r_norm.z = 1.0 - f / 2.0;
  //     normal points up or down (1,-1)
  ////    float dir = 1.0 - (2.0 * abs(mod(a_light.x,2.0)));
  //     recreate face normal vector
  ///    vec3 r_norm = vec3(n.xy, dir * (1.0 - length(n.xy)));
  vec3 light_dir = normalize(vec3(0.2, 0.2, 1.0));
  float l = dot(r_norm, light_dir) * 0.8;

  light_dir = normalize(vec3(-0.2, -0.2, 1.0));
  l += dot(r_norm, light_dir) * 0.2;

  //  l = (l + (1.0 - r_norm.z))*0.5;
  l = 0.4 + l * 0.6;

  // extreme fake-ssao by height
  l += (clamp(a_pos.z / 2048.0, 0.0, 0.1) - 0.05);
  color = vec4(u_color.rgb * (clamp(l, 0.0, 1.0) * alpha), u_color.a * alpha);
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
