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

//float FxaaLuma(float3 rgb) {
//  return rgb.g * (0.587/0.299) + rgb.b;
//}

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

//  if (max(dir.x, dir.y) > 0.1){
//    gl_FragColor = vec4(dir*0.5, 0.0, 0.5);
//  } else{
//    gl_FragColor = vec4(rgbM.rgb * 0.2, 0.8);
//  }
//  return;

  float dirReduce = max((lumaNW + lumaNE + lumaSW + lumaSE) * (0.25 * m_ReduceMul), FXAA_REDUCE_MIN);

  float rcpDirMin = 1.0 / (dirReduce + min(abs(dir.x), abs(dir.y)));

  dir = min(m_SpanMax, max(-m_SpanMax, dir * rcpDirMin)) * pixel;

  vec4 rgbA = 0.5 * (texture2D(u_texColor, tex_pos + dir * vec2(1.0 / 3.0 - 0.5))
      + texture2D(u_texColor, tex_pos + dir * vec2(2.0 / 3.0 - 0.5)));

  vec4 rgbB = rgbA * 0.5 + 0.25 * (texture2D(u_texColor, tex_pos + dir * vec2(0.0 / 3.0 - 0.5))
      + texture2D(u_texColor, tex_pos + dir * vec2(3.0 / 3.0 - 0.5)));

  float lumaB = dot(rgbB.rgb, luma.rgb);

  float d = step(lumaB, lumaMin) + step(lumaMax, lumaB);

  gl_FragColor = (1.0 - d) * rgbB + d * rgbA;

  //gl_FragColor = vec4(rgbM.rgb * 0.5, 1.0) + vec4(((1.0 - d) * rgbB.rgb + d * rgbA.rgb) - rgbM.rgb, 1.0) * 2.0;

//  if ((lumaB < lumaMin) || (lumaB > lumaMax))
//    { return rgbA; } else { return rgbB; }
}

//vec2 rcpFrame = vec2(1.0) / g_Resolution;
//gl_FragColor = vec4(FxaaPixelShader(tex_pos, m_Texture, rcpFrame), 1.0);
//gl_FragColor = FxaaPixelShader(tex_pos, u_texColor, u_pixel * 3.15);
//gl_FragColor = FxaaPixelShader (tex_pos, u_texColor, u_pixel * 2.35);
//vec4 c = texture2D (u_texColor, tex_pos);
//gl_FragColor = vec4 (c.rgb * 0.5, 1.0) + vec4(FxaaPixelShader (tex_pos, u_texColor, u_pixel * 3.15).rgb - c.rgb, 1.0) * 2.0;
//gl_FragColor = 0.2*c + (FxaaPixelShader (tex_pos, u_texColor, u_pixel * 1.2)) * 0.8;
//}
