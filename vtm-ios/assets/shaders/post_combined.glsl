#ifdef GLES
precision highp float;
#endif
uniform vec2 u_pixel;
attribute vec4 a_pos;
varying vec2 tex_pos;
varying vec2 tex_nw;
varying vec2 tex_ne;
varying vec2 tex_sw;
varying vec2 tex_se;
void
main()
{
  gl_Position = a_pos;
  tex_pos = (a_pos.xy + 1.0) * 0.5;
  vec2 pixel = u_pixel * 2.5;
  tex_nw = tex_pos + vec2(-1.0, -1.0) * pixel;
  tex_ne = tex_pos + vec2(1.0, -1.0) * pixel;
  tex_sw = tex_pos + vec2(-1.0, 1.0) * pixel;
  tex_se = tex_pos + vec2(1.0, 1.0) * pixel;
}

$$

#ifdef GLES
precision highp float;
#endif
uniform sampler2D u_texColor;
uniform sampler2D u_tex;
uniform vec2 u_pixel;
varying vec2 tex_pos;
varying vec2 tex_nw;
varying vec2 tex_ne;
varying vec2 tex_sw;
varying vec2 tex_se;

const vec2 m_SpanMax = vec2(2.5, 2.5);
const float m_ReduceMul = 0.15;
const vec3 luma = vec3(0.299, 0.587, 0.114);

#define FXAA_REDUCE_MIN   (1.0/128.0)

// gauss bell center
const float gdisplace = 0.2;
const float nearZ = 1.0;
const float farZ = 8.0;
const int iterations = 2;

float getDepth(float posZ) {
  return (2.0 * nearZ) / (nearZ + farZ - posZ * (farZ - nearZ));
}
float compareDepths(in float depth1, in float depth2, inout float far) {
  //   depth difference (0-100)
  float diff = (depth1 - depth2) * 100.0;
  //   set 'far == 1.0' when 'diff' > 'gdisplace'
  far = step(diff, gdisplace);
  // gauss bell width 2,
  // if far reduce left bell width to avoid self-shadowing
  float garea = max((1.0 - far) * 2.0, 0.1);

  //return (step(diff, 0.0) * -0.1) + pow(2.7182, -2.0 * pow(diff - gdisplace,2.0) / pow(garea, 2.0));
  return  pow(2.7182, -2.0 * pow(diff - gdisplace,2.0) / pow(garea, 2.0));
}

void
addAO(int run, inout float depth, in float x1, in float y1, in float x2, in float y2, inout float ao){
  float z_11 = getDepth(texture2D(u_tex, vec2(x1, y1)).x);
  float z_12 = getDepth(texture2D(u_tex, vec2(x1, y2)).x);
  float z_21 = getDepth(texture2D(u_tex, vec2(x2, y1)).x);
  float z_22 = getDepth(texture2D(u_tex, vec2(x2, y2)).x);

  if (run == 0)
    depth = 0.5 * depth + (z_11 + z_12 + z_21 + z_22) * (0.25 * 0.5);

  float f_11;
  float d_11 = compareDepths(depth, z_11, f_11);

  float f_12;
  float d_12 = compareDepths(depth, z_12, f_12);

  float f_21;
  float d_21 = compareDepths(depth, z_21, f_21);

  float f_22;
  float d_22 = compareDepths(depth, z_22, f_22);

  ao += 1.0 //(1.0 - step(1.0, x1)) * (1.0 - step(1.0, y1))
      * (d_11 + f_11 * (1.0 - d_11) * d_22);

  ao += 1.0 //(1.0 - step(1.0, x1)) * step(0.0, y2)
      * (d_12 + f_12 * (1.0 - d_12) * d_21);

  ao += 1.0 //step(0.0, x2) * (1.0 - step(1.0, y1))
      * (d_21 + f_21 * (1.0 - d_21) * d_12);

  ao += 1.0 //step(0.0, x2) * step(0.0, y2)
      * (d_22 + f_22 * (1.0 - d_22) * d_11);
}


void
main(){
  vec2 pixel = u_pixel * 3.15;

  vec4 rgbNW = texture2D(u_texColor, tex_nw);
  vec4 rgbNE = texture2D(u_texColor, tex_ne);
  vec4 rgbSW = texture2D(u_texColor, tex_sw);
  vec4 rgbSE = texture2D(u_texColor, tex_se);

  vec4 rgbM = texture2D(u_texColor, tex_pos);

  if (rgbNW.a + rgbNE.a + rgbSW.a + rgbSE.a < 0.1) {
    gl_FragColor = rgbM;
    return;
  }
  //return vec4(rgbM - (rgbNW + rgbNE)*0.25,1.0);

  float lumaNW = dot(rgbNW.rgb, luma);
  float lumaNE = dot(rgbNE.rgb, luma);
  float lumaSW = dot(rgbSW.rgb, luma);
  float lumaSE = dot(rgbSE.rgb, luma);
  float lumaM = dot(rgbM.rgb, luma);

  float lumaMin = min(lumaM, min(min(lumaNW, lumaNE), min(lumaSW, lumaSE)));
  float lumaMax = max(lumaM, max(max(lumaNW, lumaNE), max(lumaSW, lumaSE)));

  //vec4 rgb = texture2D (tex, tex_pos);
  //return vec4(0.5 + lumaM - lumaMin, lumaM, 0.5 + lumaM - lumaMax, 1.0) * rgb.a;

  vec2 dir;
  dir.x = -((lumaNW + lumaNE) - (lumaSW + lumaSE));
  dir.y = ((lumaNW + lumaSW) - (lumaNE + lumaSE));

  float dirReduce = max((lumaNW + lumaNE + lumaSW + lumaSE) * (0.25 * m_ReduceMul), FXAA_REDUCE_MIN);

  float rcpDirMin = 1.0 / (dirReduce + min(abs(dir.x), abs(dir.y)));

  dir = min(m_SpanMax, max(-m_SpanMax, dir * rcpDirMin)) * pixel;

  vec4 rgbA = 0.5 * (texture2D(u_texColor, tex_pos + dir * vec2(1.0 / 3.0 - 0.5))
      + texture2D(u_texColor, tex_pos + dir * vec2(2.0 / 3.0 - 0.5)));

  vec4 rgbB = rgbA * 0.5 + 0.25 * (texture2D(u_texColor, tex_pos + dir * vec2(0.0 / 3.0 - 0.5))
      + texture2D(u_texColor, tex_pos + dir * vec2(3.0 / 3.0 - 0.5)));

  float lumaB = dot(rgbB.rgb, luma.rgb);

  float d = step(lumaB, lumaMin) + step(lumaMax, lumaB);

  vec4 color = (1.0 - d) * rgbB + d * rgbA;

  //vec4 color = texture2D(u_texColor, tex_pos);

  float depth = texture2D(u_tex, tex_pos).x;
  float foggy = pow(depth,3.0);

  depth = getDepth(depth);

  float x = tex_pos.x;
  float y = tex_pos.y;
  float pw = u_pixel.x;
  float ph = u_pixel.y;
  float ao = 0.0;
  for (int i = 0; i < iterations; i++) {
    float pwByDepth = pw / depth;
    float phByDepth = ph / depth;
    addAO(i, depth, x + pwByDepth, y + phByDepth, x - pwByDepth, y - phByDepth, ao);
    pwByDepth *= 1.2;
    phByDepth *= 1.2;
    addAO(i, depth, x + pwByDepth, y, x, y - phByDepth, ao);
    // sample jittering:
    //	pw += random.x * 0.0007;
    //	ph += random.y * 0.0007;
    // increase sampling area:
    pw *= 1.7;
    ph *= 1.7;

  }

  ao = ao / float(iterations * 8);
  ao *= 0.4 * foggy;
  ao = clamp(ao, 0.0, 1.0);

  //gl_FragColor = vec4(0.5 - vao, max(0.5, ao));
  gl_FragColor =  vec4(color.rgb - ao, max(color.a, ao));
}

