// mali 400's fp varying has only mediump... 
// youilabs.com/blog/mobile-gpu-floating-point-accuracy-variances/
//  community.arm.com/groups/arm-mali-graphics/blog/2011/09/06/
//  at-home-on-the-range--why-floating-point-formats-matter-in-graphics

#ifdef GLES
precision highp float;
#endif
uniform mat4 u_mvp;
uniform vec2 u_screen;
attribute vec2 a_pos;
varying vec4 v_pos;
varying vec2 s_pos;

void main() {
    v_pos = u_mvp * vec4(a_pos, 0.0, 1.0);
    s_pos = v_pos.xy / v_pos.w;
    gl_Position = v_pos;
}

$$

#ifdef GLES
precision highp float;
#endif
uniform vec4 u_color;
uniform float u_width;
uniform vec2 u_screen;
varying vec4 v_pos;
varying vec2 s_pos;

void main() {
    vec2 pos = (v_pos.xy) / v_pos.w * u_screen;

    float l = length(gl_FragCoord.xy - u_screen - pos.xy);
    float z = clamp(0.6, 1.0, 1.2 - (v_pos.z / v_pos.w));

    gl_FragColor = u_color * (smoothstep(1.0, 0.0, l / u_width) * z);
}
