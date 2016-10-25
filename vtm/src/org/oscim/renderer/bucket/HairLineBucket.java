package org.oscim.renderer.bucket;

import org.oscim.backend.GL;
import org.oscim.core.GeometryBuffer;
import org.oscim.renderer.GLShader;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLUtils;
import org.oscim.renderer.GLViewport;
import org.oscim.theme.styles.LineStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.oscim.backend.GLAdapter.gl;
import static org.oscim.renderer.MapRenderer.COORD_SCALE;

public class HairLineBucket extends RenderBucket {
    static final Logger log = LoggerFactory.getLogger(HairLineBucket.class);

    public LineStyle line;

    public HairLineBucket(int level) {
        super(RenderBucket.HAIRLINE, true, false);
        this.level = level;
    }

    public void addLine(GeometryBuffer geom) {
        short id = (short) numVertices;

        double pts[] = geom.points;

        boolean poly = geom.isPoly();
        int inPos = 0;

        for (int i = 0, n = geom.index.length; i < n; i++) {
            int len = geom.index[i];
            if (len < 0)
                break;

            if (len < 4 || (poly && len < 6)) {
                inPos += len;
                continue;
            }

            int end = inPos + len;

            vertexItems.add((short) (pts[inPos++] * COORD_SCALE),
                    (short) (pts[inPos++] * COORD_SCALE));
            short first = id;

            indiceItems.add(id++);
            numIndices++;

            while (inPos < end) {

                vertexItems.add((short) (pts[inPos++] * COORD_SCALE),
                        (short) (pts[inPos++] * COORD_SCALE));

                indiceItems.add(id);
                numIndices++;

                if (inPos == end) {
                    if (poly) {
                        indiceItems.add(id);
                        numIndices++;

                        indiceItems.add(first);
                        numIndices++;
                    }
                    id++;
                    break;
                }
                indiceItems.add(id++);
                numIndices++;
            }

        }
        numVertices = id;
    }

    public static class Renderer {
        static Shader shader;

        static boolean init() {
            shader = new Shader("hairline");
            return true;
        }

        public static class Shader extends GLShader {
            int uMVP, uColor, uWidth, uScreen, aPos;

            Shader(String shaderFile) {
                if (!create(shaderFile))
                    return;

                uMVP = getUniform("u_mvp");
                uColor = getUniform("u_color");
                uWidth = getUniform("u_width");
                uScreen = getUniform("u_screen");
                aPos = getAttrib("a_pos");
            }

            public void set(GLViewport v) {
                useProgram();
                GLState.enableVertexArrays(aPos, -1);

                v.mvp.setAsUniform(uMVP);

                gl.uniform2f(uScreen, v.getWidth() / 2, v.getHeight() / 2);
                gl.uniform1f(uWidth, 1.5f);
                gl.lineWidth(2);
            }
        }

        public static RenderBucket draw(RenderBucket l, GLViewport v) {
            GLState.blend(true);

            Shader s = shader;

            s.set(v);

            for (; l != null && l.type == HAIRLINE; l = l.next) {
                HairLineBucket ll = (HairLineBucket) l;

                GLUtils.setColor(s.uColor, ll.line.color, 1);

                gl.vertexAttribPointer(s.aPos, 2, GL.SHORT,
                        false, 0, ll.vertexOffset);

                gl.drawElements(GL.LINES,
                        ll.numIndices,
                        GL.UNSIGNED_SHORT,
                        ll.indiceOffset);
            }
            //GL.lineWidth(1);

            return l;
        }
    }
}
