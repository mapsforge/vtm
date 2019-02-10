#ifdef GLES
precision highp float;
#endif
uniform vec2 u_pixel;
attribute vec4 a_pos;
varying vec2 tex_pos;

void main() {
    gl_Position = a_pos;
    tex_pos = (a_pos.xy + 1.0) * 0.5;
}

$$

#ifdef GLES
precision highp float;
#endif
uniform sampler2D u_tex;
uniform sampler2D u_texColor;
uniform vec2 u_pixel;

varying vec2 tex_pos;
// gauss bell center
const float gdisplace = 0.2;
const float nearZ = 1.0;
const float farZ = 4.0;
const int iterations = 4;

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

    //return (step(diff, 0.0) * -0.1) + pow(2.7182, -2.0 * pow(diff - gdisplace, 2.0) / pow(garea, 2.0));
    return pow(2.7182, -2.0 * pow(diff - gdisplace,2.0) / pow(garea, 2.0));
}

void addAO(in float depth, in float x1, in float y1, in float x2, in float y2, inout float ao) {
    float z_11 = getDepth(texture2D(u_tex, vec2(x1, y1)).x);
    float z_12 = getDepth(texture2D(u_tex, vec2(x1, y2)).x);
    float z_21 = getDepth(texture2D(u_tex, vec2(x2, y1)).x);
    float z_22 = getDepth(texture2D(u_tex, vec2(x2, y2)).x);
    //depth = 0.99 * depth + (z_11 + z_12 + z_21 + z_22) * (0.25 * 0.01);

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

void main() {
    //randomization texture:
    //vec2 fres = vec2(20.0, 20.0);
    //vec3 random = texture2D(rand, gl_TexCoord[0].st * fres.xy);
    //random = random * 2.0 - vec3(1.0);
    vec4 color = texture2D(u_texColor, tex_pos);
    float depth = texture2D(u_tex, tex_pos).x;

    float fog = pow(depth, 3.0);
    //return;
    depth = getDepth(depth);

    float x = tex_pos.x;
    float y = tex_pos.y;
    float pw = u_pixel.x;
    float ph = u_pixel.y;
    float ao = 0.0;

    for (int i = 0; i < iterations; i++) {
        float pwByDepth = pw / depth;
        float phByDepth = ph / depth;
        addAO(depth, x + pwByDepth, y + phByDepth, x - pwByDepth, y - phByDepth, ao);
        pwByDepth *= 1.2;
        phByDepth *= 1.2;
        addAO(depth, x + pwByDepth, y, x, y - phByDepth, ao);
        // sample jittering:
        //	pw += random.x * 0.0007;
        //	ph += random.y * 0.0007;
        // increase sampling area:
        pw *= 1.7;
        ph *= 1.7;
    }

    //vec3 vao = vec3(fog * pow(ao / float(iterations * 8), 1.2));
    ao = ao / float(iterations * 8);
    ao *= 0.2;
    ao = clamp(ao, 0.0, 1.0);

    vec3 vao = vec3(ao);

    //gl_FragColor = vec4(0.5 - vao, max(0.5, ao));
    gl_FragColor = vec4(color.rgb - ao, max(color.a, ao));

    //gl_FragColor = color - (fog * vec4(vao, 0.0));

    //gl_FragColor = vec4(vec3(0.8) - vao, 1.0);

    //color *= 0.5;

    //gl_FragColor = vec4(color.rgb - fog * vao, max(color.a, ao));

    //gl_FragColor = vec4(1.0) - (vec4(vao, 0.0));
    //gl_FragColor = vec4((color.rgb + vao)*0.5, color.a);

    //}
    //gl_FragColor = vec4(vao, 1.0) * texture2D(u_texColor, tex_pos.xy);
    //gl_FragColor = vec4(gl_TexCoord[0].xy, 0.0, 1.0);
    //gl_FragColor = vec4(tex_pos.xy, 0.0, 1.0);
    //gl_FragColor = vec4(gl_FragCoord.xy / u_screen, 0.0, 1.0);
}
