#ifdef GLES
precision highp float;
precision highp int;
#endif
uniform mat4 u_mvp;
uniform vec4 u_color[4];
uniform int u_mode;
uniform float u_zlimit;
attribute vec4 a_pos;

void main(void) {
    gl_Position = u_mvp * a_pos;
}

$$

#ifdef GLES
precision highp float;
precision highp int;
#endif

uniform float u_alpha;

#if GLVERSION == 20
vec4 encodeFloat (float depth) {
    const vec4 bitShift = vec4(
    256.0 * 256.0 * 256.0,
    256.0 * 256.0,
    256.0,
    1.0
    );
    const vec4 bitMask = vec4(
    0.0,
    1.0 / 256.0,
    1.0 / 256.0,
    1.0 / 256.0
    );
    vec4 comp = fract(depth * bitShift);
    comp -= comp.xxyz * bitMask;
    return comp;
}
    #endif

void main(void) {
    if (u_alpha < 0.8)
    discard; // remove shadow when alpha is too small

    #if GLVERSION == 20
    gl_FragColor = encodeFloat(gl_FragCoord.z);
    #else // GL30
    gl_FragColor = vec4(1.0);
    #endif
}
