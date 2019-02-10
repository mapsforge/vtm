#ifdef GLES
precision highp float;
#endif
uniform mat4 u_mvp;
uniform vec4 u_color;
uniform float u_alpha;
uniform vec3 u_light;
attribute vec4 a_pos;
attribute vec2 a_normal;
varying vec4 color;

void main() {
    // change height by u_alpha
    vec4 pos = a_pos;
    pos.z *= u_alpha;
    gl_Position = u_mvp * pos;
    // normalize face x/y direction
    vec2 enc = (a_normal / 255.0);

    vec3 r_norm;
    // 1² - |xy|² = |z|²
    r_norm.xy = enc * 2.0 - 1.0;
    // normal points up or down (1,-1)
    float dir = -1.0 + (2.0 * abs(mod(a_normal.x, 2.0)));
    // recreate z vector
    r_norm.z = dir * sqrt(clamp(1.0 - (r_norm.x * r_norm.x + r_norm.y * r_norm.y), 0.0, 1.0));
    r_norm = normalize(r_norm);

    float l = dot(r_norm, normalize(u_light));

    //l *= 0.8
    //vec3 opp_light_dir = normalize(vec3(-u_light.xy, u_light.z));
    //l += dot(r_norm, opp_light_dir) * 0.2;

    // [-1,1] to range [0,1]
    l = (1.0 + l) / 2.0;

    l = 0.75 + l * 0.25;

    // extreme fake-ssao by height
    l += (clamp(a_pos.z / 2048.0, 0.0, 0.1) - 0.05);
    color = vec4(u_color.rgb * (clamp(l, 0.0, 1.0)), u_color.a) * u_alpha;
}

$$

#ifdef GLES
precision highp float;
#endif
varying vec4 color;

void main() {
    gl_FragColor = color;
}
