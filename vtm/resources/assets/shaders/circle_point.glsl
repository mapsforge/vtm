#ifdef GLES
precision highp float;
#endif
uniform mat4 u_mvp;
uniform float u_radius;
attribute vec2 a_pos;

void main() {
    gl_Position = u_mvp * vec4(a_pos, 0.0, 1.0);
    gl_PointSize = 2.0 * u_radius / gl_Position.z;
}

$$

#ifdef GL_OES_standard_derivatives
#extension GL_OES_standard_derivatives : enable
#endif
#ifdef GLES
precision highp float;
#endif
uniform vec4 u_fill;
uniform float u_radius;
uniform vec4 u_stroke;
uniform float u_width;

void main() {
    vec2 cxy = 2.0 * gl_PointCoord - 1.0;
    float r = dot(cxy, cxy);
    float delta = fwidth(r);
    float alpha = 1.0 - smoothstep(1.0 - delta, 1.0 + delta, r);
    float edge = 1.0 - u_width / u_radius;
    float stroke = 1.0 - smoothstep(edge - delta, edge + delta, r);
    gl_FragColor = mix(u_stroke, u_fill, stroke) * alpha;
}
