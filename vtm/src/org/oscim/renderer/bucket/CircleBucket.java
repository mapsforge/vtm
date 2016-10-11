package org.oscim.renderer.bucket;

import org.oscim.backend.GL;
import org.oscim.core.GeometryBuffer;
import org.oscim.renderer.GLShader;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLUtils;
import org.oscim.renderer.GLViewport;
import org.oscim.theme.styles.CircleStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.oscim.backend.GLAdapter.gl;
import static org.oscim.renderer.MapRenderer.COORD_SCALE;

public class CircleBucket extends RenderBucket {
    static final Logger log = LoggerFactory.getLogger(CircleBucket.class);

    public CircleStyle circle;

    public CircleBucket(int level) {
        super(RenderBucket.CIRCLE, true, false);
        this.level = level;
    }

    public void addCircle(GeometryBuffer geom) {
        if (!geom.isPoint()) {
            log.error("Circle style applied to non-point geometry");
            return;
        }

        float x = geom.getPointX(0);
        float y = geom.getPointY(0);

        vertexItems.add((short) (x * COORD_SCALE), (short) (y * COORD_SCALE));
        indiceItems.add((short) numVertices++);
        numIndices++;
    }

    public static class Renderer {
        static CircleBucket.Renderer.Shader shader;

        static boolean init() {
            shader = new CircleBucket.Renderer.Shader("circle");
            return true;
        }

        public static class Shader extends GLShader {
            int uMVP, uColor, uRadius, uScreen, aPos;

            Shader(String shaderFile) {
                if (!create(shaderFile))
                    return;

                uMVP = getUniform("u_mvp");
                uColor = getUniform("u_color");
                uRadius = getUniform("u_radius");
                uScreen = getUniform("u_screen");
                aPos = getAttrib("a_pos");
            }

            public void set(GLViewport v) {
                useProgram();
                GLState.enableVertexArrays(aPos, -1);

                v.mvp.setAsUniform(uMVP);
                gl.uniform2f(uScreen, v.getWidth() / 2, v.getHeight() / 2);
                gl.lineWidth(2);
            }
        }

        public static RenderBucket draw(RenderBucket b, GLViewport v) {
            GLState.blend(true);

            CircleBucket.Renderer.Shader s = shader;

            s.set(v);

            for (; b != null && b.type == CIRCLE; b = b.next) {
                CircleBucket cb = (CircleBucket) b;

                GLUtils.setColor(s.uColor, cb.circle.fill, 1);
                gl.uniform1f(s.uRadius, cb.circle.radius);

                gl.vertexAttribPointer(s.aPos, 2, GL.SHORT,
                        false, 0, cb.vertexOffset);

                gl.drawArrays(GL.TRIANGLE_STRIP, 0, 4);
            }

            return b;
        }
    }
}
