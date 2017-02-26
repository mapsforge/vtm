/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.renderer.bucket;

import org.oscim.backend.GL;
import org.oscim.core.GeometryBuffer;
import org.oscim.renderer.GLShader;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLUtils;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.MapRenderer;
import org.oscim.theme.styles.LineStyle;
import org.oscim.utils.FastMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import static org.oscim.backend.GLAdapter.gl;
import static org.oscim.renderer.MapRenderer.COORD_SCALE;
import static org.oscim.renderer.MapRenderer.MAX_INDICES;
import static org.oscim.renderer.MapRenderer.bindQuadIndicesVBO;

/**
 * RenderElement for textured or stippled lines
 * <p/>
 * Interleave two segment quads in one block to be able to use
 * vertices twice. pos0 and pos1 use the same vertex array where
 * pos1 has an offset of one vertex. The vertex shader will use
 * pos0 when the vertexId is even, pos1 when the Id is odd.
 * <p/>
 * As there is no gl_VertexId in gles 2.0 an additional 'flip'
 * array is used. Depending on 'flip' extrusion is inverted.
 * <p/>
 * Indices and flip buffers can be static.
 * <p/>
 * <pre>
 * First pass: using even vertex array positions
 *   (used vertices are in braces)
 * vertex id   0  1  2  3  4  5  6  7
 * pos0     x (0) 1 (2) 3 (4) 5 (6) 7 x
 * pos1        x (0) 1 (2) 3 (4) 5 (6) 7 x
 * flip        0  1  0  1  0  1  0  1
 *
 * Second pass: using odd vertex array positions
 * vertex id   0  1  2  3  4  5  6  7
 * pos0   x 0 (1) 2 (3) 4 (5) 6 (7) x
 * pos1      x 0 (1) 2 (3) 4 (5) 6 (7) x
 * flip        0  1  0  1  0  1  0  1
 * </pre>
 * <p/>
 * Vertex layout:
 * [2 short] position,
 * [2 short] extrusion,
 * [1 short] line length
 * [1 short] unused
 * <p/>
 * indices, for two blocks:
 * 0, 1, 2,
 * 2, 1, 3,
 * 4, 5, 6,
 * 6, 5, 7,
 * <p/>
 * BIG NOTE: renderer assumes to be able to offset vertex array position
 * so that in the first pass 'pos1' offset will be < 0 if no data precedes
 * - in our case there is always the polygon fill array at start
 * - see addLine hack otherwise.
 */
public final class LineTexBucket extends LineBucket {

    static final Logger log = LoggerFactory.getLogger(LineTexBucket.class);

    public int evenQuads;
    public int oddQuads;

    private boolean evenSegment = true;

    LineTexBucket(int level) {
        super(TEXLINE, false, true);

        this.level = level;
        this.evenSegment = true;
    }

    public void addLine(GeometryBuffer geom) {
        addLine(geom.points, geom.index, -1, false);
    }

    @Override
    void addLine(double[] points, int[] index, int numPoints, boolean closed) {

        if (vertexItems.empty()) {
            /* The additional end vertex to make sure not to read outside
             * allocated memory */
            numVertices = 1;
        }
        VertexData vi = vertexItems;

        /* reset offset to last written position */
        if (!evenSegment)
            vi.seek(-12);

        int n;
        int length = 0;

        if (index == null) {
            n = 1;
            length = numPoints;
        } else {
            n = index.length;
        }

        for (int i = 0, pos = 0; i < n; i++) {
            if (index != null)
                length = index[i];

            /* check end-marker in indices */
            if (length < 0)
                break;

            /* need at least two points */
            if (length < 4) {
                pos += length;
                continue;
            }

            int end = pos + length;
            double x = points[pos++] * COORD_SCALE;
            double y = points[pos++] * COORD_SCALE;

            /* randomize a bit */
            double lineLength = line.randomOffset ? (x * x + y * y) % 80 : 0;

            while (pos < end) {
                double nx = points[pos++] * COORD_SCALE;
                double ny = points[pos++] * COORD_SCALE;

                /* Calculate triangle corners for the given width */
                double vx = nx - x;
                double vy = ny - y;

                //    /* normalize vector */
                double a = Math.sqrt(vx * vx + vy * vy);
                //    vx /= a;
                //    vy /= a;
                
                /* normalized perpendicular to line segment */
                short dx = (short) ((-vy / a) * DIR_SCALE);
                short dy = (short) ((vx / a) * DIR_SCALE);

                vi.add((short) x, (short) y, dx, dy, (short) lineLength, (short) 0);

                lineLength += a;

                vi.seek(6);
                vi.add((short) nx, (short) ny, dx, dy, (short) lineLength, (short) 0);

                x = nx;
                y = ny;

                if (evenSegment) {
                    /* go to second segment */
                    vi.seek(-12);
                    evenSegment = false;

                    /* vertex 0 and 2 were added */
                    numVertices += 3;
                    evenQuads++;
                } else {
                    /* go to next block */
                    evenSegment = true;

                    /* vertex 1 and 3 were added */
                    numVertices += 1;
                    oddQuads++;
                }
            }
        }

        /* advance offset to last written position */
        if (!evenSegment)
            vi.seek(12);
    }

    @Override
    protected void clear() {
        evenSegment = true;
        evenQuads = 0;
        oddQuads = 0;
        super.clear();
    }

    @Override
    protected void compile(ShortBuffer vboData, ShortBuffer iboData) {
        compileVertexItems(vboData);
        /* add additional vertex for interleaving, see TexLineLayer. */
        vboData.position(vboData.position() + 6);
    }

    static class Shader extends GLShader {
        int uMVP, uColor, uWidth, uBgColor, uScale, uMode;
        int uPatternWidth, uPatternScale;
        int aPos0, aPos1, aLen0, aLen1, aFlip;

        Shader(String shaderFile) {
            if (!create(shaderFile))
                return;

            uMVP = getUniform("u_mvp");

            uScale = getUniform("u_scale");
            uColor = getUniform("u_color");
            uWidth = getUniform("u_width");
            uBgColor = getUniform("u_bgcolor");
            uMode = getUniform("u_mode");

            uPatternWidth = getUniform("u_pwidth");
            uPatternScale = getUniform("u_pscale");

            aPos0 = getAttrib("a_pos0");
            aPos1 = getAttrib("a_pos1");
            aLen0 = getAttrib("a_len0");
            aLen1 = getAttrib("a_len1");
            aFlip = getAttrib("a_flip");
        }
    }

    public final static class Renderer {
        private static Shader shader;

        /* factor to normalize extrusion vector and scale to coord scale */
        private final static float COORD_SCALE_BY_DIR_SCALE =
                MapRenderer.COORD_SCALE / LineBucket.DIR_SCALE;

        private static int mVertexFlipID;

        public static void init() {

            shader = new Shader("linetex_layer_tex");
            //shader = new Shader("linetex_layer");

            int[] vboIds = GLUtils.glGenBuffers(1);
            mVertexFlipID = vboIds[0];

            /* bytes: 0, 1, 0, 1, 0, ... */
            byte[] flip = new byte[MapRenderer.MAX_QUADS * 4];
            for (int i = 0; i < flip.length; i++)
                flip[i] = (byte) (i % 2);

            ByteBuffer buf = ByteBuffer.allocateDirect(flip.length)
                    .order(ByteOrder.nativeOrder());
            buf.put(flip);
            buf.flip();

            ShortBuffer sbuf = buf.asShortBuffer();

            //GL.bindBuffer(GL20.ARRAY_BUFFER, mVertexFlipID);
            GLState.bindVertexBuffer(mVertexFlipID);
            gl.bufferData(GL.ARRAY_BUFFER, flip.length, sbuf,
                    GL.STATIC_DRAW);
            GLState.bindVertexBuffer(0);

            //    mTexID = new int[10];
            //    byte[] stipple = new byte[40];
            //    stipple[0] = 32;
            //    stipple[1] = 32;
            //    mTexID[0] = loadStippleTexture(stipple);

            //tex = new TextureItem(CanvasAdapter.getBitmapAsset("patterns/arrow.png"));
            //tex.mipmap = true;
        }

        //static TextureItem tex;

        public static int loadStippleTexture(byte[] stipple) {
            int sum = 0;
            for (byte flip : stipple)
                sum += flip;

            byte[] pixel = new byte[sum];

            boolean on = true;
            int pos = 0;
            for (byte flip : stipple) {
                float max = flip;

                for (int s = 0; s < flip; s++) {
                    float alpha = Math.abs(s / (max - 1) - 0.5f);
                    if (on)
                        alpha = 255 * (1 - alpha);
                    else
                        alpha = 255 * alpha;

                    pixel[pos + s] = FastMath.clampToByte((int) alpha);
                }
                on = !on;
                pos += flip;
            }

            return GLUtils.loadTexture(pixel, sum, 1, GL.ALPHA,
                    GL.LINEAR, GL.LINEAR,
                    GL.REPEAT, GL.REPEAT);
        }

        private final static int STRIDE = 12;
        private final static int LEN_OFFSET = 8;

        public static RenderBucket draw(RenderBucket b, GLViewport v,
                                        float div, RenderBuckets buckets) {

            GLState.blend(true);
            shader.useProgram();

            GLState.enableVertexArrays(-1, -1);

            int aLen0 = shader.aLen0;
            int aLen1 = shader.aLen1;
            int aPos0 = shader.aPos0;
            int aPos1 = shader.aPos1;
            int aFlip = shader.aFlip;

            gl.enableVertexAttribArray(aPos0);
            gl.enableVertexAttribArray(aPos1);
            gl.enableVertexAttribArray(aLen0);
            gl.enableVertexAttribArray(aLen1);
            gl.enableVertexAttribArray(aFlip);

            v.mvp.setAsUniform(shader.uMVP);

            bindQuadIndicesVBO();

            GLState.bindVertexBuffer(mVertexFlipID);
            gl.vertexAttribPointer(shader.aFlip, 1,
                    GL.BYTE, false, 0, 0);

            buckets.vbo.bind();

            float scale = (float) v.pos.getZoomScale();
            float s = scale / div;

            for (; b != null && b.type == TEXLINE; b = b.next) {
                LineTexBucket lb = (LineTexBucket) b;
                LineStyle line = lb.line.current();

                gl.uniform1f(shader.uMode, line.texture != null ? 1 : 0);

                if (line.texture != null)
                    line.texture.bind();

                GLUtils.setColor(shader.uColor, line.stippleColor, 1);
                GLUtils.setColor(shader.uBgColor, line.color, 1);

                float pScale;

                if (s >= 1) {
                    pScale = (line.stipple * s);
                    int cnt = (int) (pScale / line.stipple);
                    pScale = (float) line.stipple / (cnt + 1);
                } else {
                    pScale = line.stipple / s;
                    int cnt = (int) (pScale / line.stipple);
                    pScale = line.stipple * cnt;
                }

                //log.debug("pScale {} {}", pScale, s);

                gl.uniform1f(shader.uPatternScale, COORD_SCALE * pScale);

                gl.uniform1f(shader.uPatternWidth, line.stippleWidth);

                /* keep line width fixed */
                gl.uniform1f(shader.uWidth, (lb.scale * line.width) / s * COORD_SCALE_BY_DIR_SCALE);

                /* add offset vertex */
                int vOffset = -STRIDE;

                // TODO interleave 1. and 2. pass to improve vertex cache usage?
                /* first pass */
                int allIndices = (lb.evenQuads * 6);
                for (int i = 0; i < allIndices; i += MAX_INDICES) {
                    int numIndices = allIndices - i;
                    if (numIndices > MAX_INDICES)
                        numIndices = MAX_INDICES;

                    /* i / 6 * (24 shorts per block * 2 short bytes) */
                    int add = (b.vertexOffset + i * 8) + vOffset;

                    gl.vertexAttribPointer(aPos0, 4, GL.SHORT, false, STRIDE,
                            add + STRIDE);

                    gl.vertexAttribPointer(aLen0, 2, GL.SHORT, false, STRIDE,
                            add + STRIDE + LEN_OFFSET);

                    gl.vertexAttribPointer(aPos1, 4, GL.SHORT, false, STRIDE,
                            add);

                    gl.vertexAttribPointer(aLen1, 2, GL.SHORT, false, STRIDE,
                            add + LEN_OFFSET);

                    gl.drawElements(GL.TRIANGLES, numIndices,
                            GL.UNSIGNED_SHORT, 0);
                }

                /* second pass */
                allIndices = (lb.oddQuads * 6);
                for (int i = 0; i < allIndices; i += MAX_INDICES) {
                    int numIndices = allIndices - i;
                    if (numIndices > MAX_INDICES)
                        numIndices = MAX_INDICES;
                    /* i / 6 * (24 shorts per block * 2 short bytes) */
                    int add = (b.vertexOffset + i * 8) + vOffset;

                    gl.vertexAttribPointer(aPos0, 4, GL.SHORT, false, STRIDE,
                            add + 2 * STRIDE);

                    gl.vertexAttribPointer(aLen0, 2, GL.SHORT, false, STRIDE,
                            add + 2 * STRIDE + LEN_OFFSET);

                    gl.vertexAttribPointer(aPos1, 4, GL.SHORT, false, STRIDE,
                            add + STRIDE);

                    gl.vertexAttribPointer(aLen1, 2, GL.SHORT, false, STRIDE,
                            add + STRIDE + LEN_OFFSET);

                    gl.drawElements(GL.TRIANGLES, numIndices,
                            GL.UNSIGNED_SHORT, 0);
                }
            }

            gl.disableVertexAttribArray(aPos0);
            gl.disableVertexAttribArray(aPos1);
            gl.disableVertexAttribArray(aLen0);
            gl.disableVertexAttribArray(aLen1);
            gl.disableVertexAttribArray(aFlip);

            return b;
        }
    }
}
