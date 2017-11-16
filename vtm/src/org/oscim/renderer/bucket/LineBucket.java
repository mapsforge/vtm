/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2017 devemux86
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General License for more details.
 *
 * You should have received a copy of the GNU Lesser General License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.renderer.bucket;

import org.oscim.backend.GL;
import org.oscim.backend.GLAdapter;
import org.oscim.backend.canvas.Paint.Cap;
import org.oscim.core.GeometryBuffer;
import org.oscim.core.MercatorProjection;
import org.oscim.renderer.GLShader;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLUtils;
import org.oscim.renderer.GLViewport;
import org.oscim.theme.styles.LineStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.oscim.backend.GLAdapter.gl;
import static org.oscim.renderer.MapRenderer.COORD_SCALE;

/**
 * Note:
 * Coordinates must be in range [-4096..4096] and the maximum
 * resolution for coordinates is 0.25 as points will be converted
 * to fixed point values.
 */
public class LineBucket extends RenderBucket {
    static final Logger log = LoggerFactory.getLogger(LineBucket.class);

    /**
     * scale factor mapping extrusion vector to short values
     */
    public static final float DIR_SCALE = 2048;

    /**
     * maximal resolution
     */
    private static final float MIN_DIST = 1 / 8f;

    /**
     * not quite right.. need to go back so that additional
     * bevel vertices are at least MIN_DIST apart
     */
    private static final float BEVEL_MIN = MIN_DIST * 4;

    /**
     * mask for packing last two bits of extrusion vector with texture
     * coordinates
     */
    private static final int DIR_MASK = 0xFFFFFFFC;

    /* lines referenced by this outline layer */
    public LineBucket outlines;
    public LineStyle line;
    public float scale = 1;

    public boolean roundCap;
    private float mMinDist = MIN_DIST;

    public float heightOffset;

    private int tmin = Integer.MIN_VALUE, tmax = Integer.MAX_VALUE;

    public LineBucket(int layer) {
        super(RenderBucket.LINE, false, false);
        this.level = layer;
    }

    LineBucket(byte type, boolean indexed, boolean quads) {
        super(type, indexed, quads);
    }

    public void addOutline(LineBucket link) {
        for (LineBucket l = outlines; l != null; l = l.outlines)
            if (link == l)
                return;

        link.outlines = outlines;
        outlines = link;
    }

    public void setExtents(int min, int max) {
        tmin = min;
        tmax = max;
    }

    /**
     * For point reduction by minimal distance. Default is 1/8.
     */
    public void setDropDistance(float minDist) {
        mMinDist = Math.max(minDist, MIN_DIST);
    }

    public void addLine(GeometryBuffer geom) {
        if (geom.isPoly())
            addLine(geom.points, geom.index, -1, true);
        else if (geom.isLine())
            addLine(geom.points, geom.index, -1, false);
        else
            log.debug("geometry must be LINE or POLYGON");
    }

    public void addLine(float[] points, int numPoints, boolean closed) {
        if (numPoints >= 4)
            addLine(points, null, numPoints, closed);
    }

    void addLine(float[] points, int[] index, int numPoints, boolean closed) {

        boolean rounded = false;
        boolean squared = false;

        if (line.cap == Cap.ROUND)
            rounded = true;
        else if (line.cap == Cap.SQUARE)
            squared = true;

        /* Note: just a hack to save some vertices, when there are
         * more than 200 lines per type. FIXME make optional! */
        if (rounded && index != null) {
            int cnt = 0;
            for (int i = 0, n = index.length; i < n; i++, cnt++) {
                if (index[i] < 0)
                    break;
                if (cnt > 400) {
                    rounded = false;
                    break;
                }
            }
        }
        roundCap = rounded;

        int n;
        int length = 0;

        if (index == null) {
            n = 1;
            if (numPoints > 0) {
                length = numPoints;
            } else {
                length = points.length;
            }
        } else {
            n = index.length;
        }

        for (int i = 0, pos = 0; i < n; i++) {
            if (index != null)
                length = index[i];

            /* check end-marker in indices */
            if (length < 0)
                break;

            int ipos = pos;
            pos += length;

            /* need at least two points */
            if (length < 4)
                continue;

            /* start an enpoint are equal */
            if (length == 4 &&
                    points[ipos] == points[ipos + 2] &&
                    points[ipos + 1] == points[ipos + 3])
                continue;

            /* avoid simple 180 degree angles */
            if (length == 6 &&
                    points[ipos] == points[ipos + 4] &&
                    points[ipos + 1] == points[ipos + 5])
                length -= 2;

            addLine(vertexItems, points, ipos, length, rounded, squared, closed);

        }
    }

    private void addVertex(VertexData vi,
                           float x, float y,
                           float vNextX, float vNextY,
                           float vPrevX, float vPrevY) {

        float ux = vNextX + vPrevX;
        float uy = vNextY + vPrevY;

        /* vPrev times perpendicular of sum(vNext, vPrev) */
        double a = uy * vPrevX - ux * vPrevY;

        if (a < 0.01 && a > -0.01) {
            ux = -vPrevY;
            uy = vPrevX;
        } else {
            ux /= a;
            uy /= a;
        }

        short ox = (short) (x * COORD_SCALE);
        short oy = (short) (y * COORD_SCALE);

        int ddx = (int) (ux * DIR_SCALE);
        int ddy = (int) (uy * DIR_SCALE);

        vi.add(ox, oy,
                (short) (0 | ddx & DIR_MASK),
                (short) (1 | ddy & DIR_MASK));

        vi.add(ox, oy,
                (short) (2 | -ddx & DIR_MASK),
                (short) (1 | -ddy & DIR_MASK));
    }

    private void addLine(VertexData vertices, float[] points, int start, int length,
                         boolean rounded, boolean squared, boolean closed) {

        float ux, uy;
        float vPrevX, vPrevY;
        float vNextX, vNextY;
        float curX, curY;
        float nextX, nextY;
        double a;

        /* amount of vertices used
         * + 2 for drawing triangle-strip
         * + 4 for round caps
         * + 2 for closing polygons */
        numVertices += length + (rounded ? 6 : 2) + (closed ? 2 : 0);

        int ipos = start;

        curX = points[ipos++];
        curY = points[ipos++];
        nextX = points[ipos++];
        nextY = points[ipos++];

        /* Unit vector to next node */
        vPrevX = nextX - curX;
        vPrevY = nextY - curY;
        a = (float) Math.sqrt(vPrevX * vPrevX + vPrevY * vPrevY);
        vPrevX /= a;
        vPrevY /= a;

        /* perpendicular on the first segment */
        ux = -vPrevY;
        uy = vPrevX;

        int ddx, ddy;

        /* vertex point coordinate */
        short ox = (short) (curX * COORD_SCALE);
        short oy = (short) (curY * COORD_SCALE);

        /* vertex extrusion vector, last two bit
         * encode texture coord. */
        short dx, dy;

        /* when the endpoint is outside the tile region omit round caps. */
        boolean outside = (curX < tmin || curX > tmax || curY < tmin || curY > tmax);

        if (rounded && !outside) {
            ddx = (int) ((ux - vPrevX) * DIR_SCALE);
            ddy = (int) ((uy - vPrevY) * DIR_SCALE);
            dx = (short) (0 | ddx & DIR_MASK);
            dy = (short) (2 | ddy & DIR_MASK);

            vertices.add(ox, oy, (short) dx, (short) dy);
            vertices.add(ox, oy, (short) dx, (short) dy);

            ddx = (int) (-(ux + vPrevX) * DIR_SCALE);
            ddy = (int) (-(uy + vPrevY) * DIR_SCALE);

            vertices.add(ox, oy,
                    (short) (2 | ddx & DIR_MASK),
                    (short) (2 | ddy & DIR_MASK));

            /* Start of line */
            ddx = (int) (ux * DIR_SCALE);
            ddy = (int) (uy * DIR_SCALE);

            vertices.add(ox, oy,
                    (short) (0 | ddx & DIR_MASK),
                    (short) (1 | ddy & DIR_MASK));

            vertices.add(ox, oy,
                    (short) (2 | -ddx & DIR_MASK),
                    (short) (1 | -ddy & DIR_MASK));
        } else {
            /* outside means line is probably clipped
             * TODO should align ending with tile boundary
             * for now, just extend the line a little */
            float tx = vPrevX;
            float ty = vPrevY;

            if (!rounded && !squared) {
                tx = 0;
                ty = 0;
            } else if (rounded) {
                tx *= 0.5;
                ty *= 0.5;
            }

            if (rounded)
                numVertices -= 2;

            /* add first vertex twice */
            ddx = (int) ((ux - tx) * DIR_SCALE);
            ddy = (int) ((uy - ty) * DIR_SCALE);
            dx = (short) (0 | ddx & DIR_MASK);
            dy = (short) (1 | ddy & DIR_MASK);

            vertices.add(ox, oy, (short) dx, (short) dy);
            vertices.add(ox, oy, (short) dx, (short) dy);

            ddx = (int) (-(ux + tx) * DIR_SCALE);
            ddy = (int) (-(uy + ty) * DIR_SCALE);

            vertices.add(ox, oy,
                    (short) (2 | ddx & DIR_MASK),
                    (short) (1 | ddy & DIR_MASK));
        }

        curX = nextX;
        curY = nextY;

        /* Unit vector pointing back to previous node */
        vPrevX *= -1;
        vPrevY *= -1;

        //        vertexItem.used = opos + 4;

        for (int end = start + length; ; ) {

            if (ipos < end) {
                nextX = points[ipos++];
                nextY = points[ipos++];
            } else if (closed && ipos < end + 2) {
                /* add startpoint == endpoint */
                nextX = points[start];
                nextY = points[start + 1];
                ipos += 2;
            } else
                break;

            /* unit vector pointing forward to next node */
            vNextX = nextX - curX;
            vNextY = nextY - curY;
            a = Math.sqrt(vNextX * vNextX + vNextY * vNextY);
            /* skip too short segmets */
            if (a < mMinDist) {
                numVertices -= 2;
                continue;
            }
            vNextX /= a;
            vNextY /= a;

            double dotp = (vNextX * vPrevX + vNextY * vPrevY);

            //log.debug("acos " + dotp);
            if (dotp > 0.65) {
                /* add bevel join to avoid miter going to infinity */
                numVertices += 2;

                //dotp = FastMath.clamp(dotp, -1, 1);
                //double cos = Math.acos(dotp);
                //log.debug("cos " + Math.toDegrees(cos));
                //log.debug("back " + (mMinDist * 2 / Math.sin(cos + Math.PI / 2)));

                float px, py;
                if (dotp > 0.999) {
                    /* 360 degree angle, set points aside */
                    ux = vPrevX + vNextX;
                    uy = vPrevY + vNextY;
                    a = vNextX * uy - vNextY * ux;
                    if (a < 0.1 && a > -0.1) {
                        /* Almost straight */
                        ux = -vNextY;
                        uy = vNextX;
                    } else {
                        ux /= a;
                        uy /= a;
                    }
                    //log.debug("aside " + a + " " + ux + " " + uy);
                    px = curX - ux * BEVEL_MIN;
                    py = curY - uy * BEVEL_MIN;
                    curX = curX + ux * BEVEL_MIN;
                    curY = curY + uy * BEVEL_MIN;
                } else {
                    //log.debug("back");
                    /* go back by min dist */
                    px = curX + vPrevX * BEVEL_MIN;
                    py = curY + vPrevY * BEVEL_MIN;
                    /* go forward by min dist */
                    curX = curX + vNextX * BEVEL_MIN;
                    curY = curY + vNextY * BEVEL_MIN;
                }

                /* unit vector pointing forward to next node */
                vNextX = curX - px;
                vNextY = curY - py;
                a = Math.sqrt(vNextX * vNextX + vNextY * vNextY);
                vNextX /= a;
                vNextY /= a;

                addVertex(vertices, px, py, vPrevX, vPrevY, vNextX, vNextY);

                /* flip unit vector to point back */
                vPrevX = -vNextX;
                vPrevY = -vNextY;

                /* unit vector pointing forward to next node */
                vNextX = nextX - curX;
                vNextY = nextY - curY;
                a = Math.sqrt(vNextX * vNextX + vNextY * vNextY);
                vNextX /= a;
                vNextY /= a;
            }

            addVertex(vertices, curX, curY, vPrevX, vPrevY, vNextX, vNextY);

            curX = nextX;
            curY = nextY;

            /* flip vector to point back */
            vPrevX = -vNextX;
            vPrevY = -vNextY;
        }

        ux = vPrevY;
        uy = -vPrevX;

        outside = (curX < tmin || curX > tmax || curY < tmin || curY > tmax);

        ox = (short) (curX * COORD_SCALE);
        oy = (short) (curY * COORD_SCALE);

        if (rounded && !outside) {
            ddx = (int) (ux * DIR_SCALE);
            ddy = (int) (uy * DIR_SCALE);

            vertices.add(ox, oy,
                    (short) (0 | ddx & DIR_MASK),
                    (short) (1 | ddy & DIR_MASK));

            vertices.add(ox, oy,
                    (short) (2 | -ddx & DIR_MASK),
                    (short) (1 | -ddy & DIR_MASK));

            /* For rounded line edges */
            ddx = (int) ((ux - vPrevX) * DIR_SCALE);
            ddy = (int) ((uy - vPrevY) * DIR_SCALE);

            vertices.add(ox, oy,
                    (short) (0 | ddx & DIR_MASK),
                    (short) (0 | ddy & DIR_MASK));

            /* last vertex */
            ddx = (int) (-(ux + vPrevX) * DIR_SCALE);
            ddy = (int) (-(uy + vPrevY) * DIR_SCALE);
            dx = (short) (2 | ddx & DIR_MASK);
            dy = (short) (0 | ddy & DIR_MASK);

        } else {
            if (!rounded && !squared) {
                vPrevX = 0;
                vPrevY = 0;
            } else if (rounded) {
                vPrevX *= 0.5;
                vPrevY *= 0.5;
            }

            if (rounded)
                numVertices -= 2;

            ddx = (int) ((ux - vPrevX) * DIR_SCALE);
            ddy = (int) ((uy - vPrevY) * DIR_SCALE);

            vertices.add(ox, oy,
                    (short) (0 | ddx & DIR_MASK),
                    (short) (1 | ddy & DIR_MASK));

            /* last vertex */
            ddx = (int) (-(ux + vPrevX) * DIR_SCALE);
            ddy = (int) (-(uy + vPrevY) * DIR_SCALE);
            dx = (short) (2 | ddx & DIR_MASK);
            dy = (short) (1 | ddy & DIR_MASK);
        }

        /* add last vertex twice */
        vertices.add(ox, oy, (short) dx, (short) dy);
        vertices.add(ox, oy, (short) dx, (short) dy);
    }

    static class Shader extends GLShader {
        int uMVP, uFade, uWidth, uColor, uMode, uHeight, aPos;

        Shader(String shaderFile) {
            if (!create(shaderFile))
                return;
            uMVP = getUniform("u_mvp");
            uFade = getUniform("u_fade");
            uWidth = getUniform("u_width");
            uColor = getUniform("u_color");
            uMode = getUniform("u_mode");
            uHeight = getUniform("u_height");
            aPos = getAttrib("a_pos");
        }

        @Override
        public boolean useProgram() {
            if (super.useProgram()) {
                GLState.enableVertexArrays(aPos, -1);
                return true;
            }
            return false;
        }
    }

    public static final class Renderer {
        /* TODO:
         * http://http.developer.nvidia.com/GPUGems2/gpugems2_chapter22.html */

        /* factor to normalize extrusion vector and scale to coord scale */
        private final static float COORD_SCALE_BY_DIR_SCALE =
                COORD_SCALE / LineBucket.DIR_SCALE;

        private final static int CAP_THIN = 0;
        private final static int CAP_BUTT = 1;
        private final static int CAP_ROUND = 2;

        private final static int SHADER_FLAT = 1;
        private final static int SHADER_PROJ = 0;

        public static int mTexID;
        private static Shader[] shaders = {null, null};

        static boolean init() {

            shaders[0] = new Shader("line_aa_proj");
            shaders[1] = new Shader("line_aa");

            /* create lookup table as texture for 'length(0..1,0..1)'
             * using mirrored wrap mode for 'length(-1..1,-1..1)' */
            byte[] pixel = new byte[128 * 128];

            for (int x = 0; x < 128; x++) {
                float xx = x * x;
                for (int y = 0; y < 128; y++) {
                    float yy = y * y;
                    int color = (int) (Math.sqrt(xx + yy) * 2);
                    if (color > 255)
                        color = 255;
                    pixel[x + y * 128] = (byte) color;
                }
            }

            mTexID = GLUtils.loadTexture(pixel, 128, 128, GL.ALPHA,
                    GL.NEAREST, GL.NEAREST,
                    GL.MIRRORED_REPEAT,
                    GL.MIRRORED_REPEAT);
            return true;
        }

        public static RenderBucket draw(RenderBucket b, GLViewport v,
                                        float scale, RenderBuckets buckets) {

            /* simple line shader does not take forward shortening into
             * account. only used when tilt is 0. */
            int mode = v.pos.tilt < 1 ? 1 : 0;

            Shader s = shaders[mode];
            s.useProgram();

            GLState.blend(true);

            /* Somehow we loose the texture after an indefinite
             * time, when label/symbol textures are used.
             * Debugging gl on Desktop is most fun imaginable,
             * so for now: */
            if (!GLAdapter.GDX_DESKTOP_QUIRKS)
                GLState.bindTex2D(mTexID);

            int uLineFade = s.uFade;
            int uLineMode = s.uMode;
            int uLineColor = s.uColor;
            int uLineWidth = s.uWidth;
            int uLineHeight = s.uHeight;

            gl.vertexAttribPointer(s.aPos, 4, GL.SHORT, false, 0,
                    buckets.offset[LINE]);

            v.mvp.setAsUniform(s.uMVP);

            /* Line scale factor for non fixed lines: Within a zoom-
             * level lines would be scaled by the factor 2 by view-matrix.
             * Though lines should only scale by sqrt(2). This is achieved
             * by inverting scaling of extrusion vector with: width/sqrt(s). */
            double variableScale = Math.sqrt(scale);

            /* scale factor to map one pixel on tile to one pixel on screen:
             * used with orthographic projection, (shader mode == 1) */
            double pixel = (mode == SHADER_PROJ) ? 0.0001 : 1.5 / scale;

            gl.uniform1f(uLineFade, (float) pixel);

            int capMode = 0;
            gl.uniform1f(uLineMode, capMode);

            boolean blur = false;
            double width;

            float heightOffset = 0;
            gl.uniform1f(uLineHeight, heightOffset);

            //    if (1 == 1)
            //        return b.next;
            //
            for (; b != null && b.type == RenderBucket.LINE; b = b.next) {
                LineBucket lb = (LineBucket) b;
                LineStyle line = lb.line.current();

                if (line.heightOffset != lb.heightOffset)
                    lb.heightOffset = line.heightOffset;
                if (lb.heightOffset != heightOffset) {
                    heightOffset = lb.heightOffset;

                    gl.uniform1f(uLineHeight, heightOffset /
                            MercatorProjection.groundResolution(v.pos));
                }

                if (line.fadeScale < v.pos.zoomLevel) {
                    GLUtils.setColor(uLineColor, line.color, 1);
                } else if (line.fadeScale > v.pos.zoomLevel) {
                    continue;
                } else {
                    float alpha = (float) (scale > 1.2 ? scale : 1.2) - 1;
                    GLUtils.setColor(uLineColor, line.color, alpha);
                }

                if (mode == SHADER_PROJ && blur && line.blur == 0) {
                    gl.uniform1f(uLineFade, (float) pixel);
                    blur = false;
                }

                /* draw LineLayer */
                if (!line.outline) {
                    /* invert scaling of extrusion vectors so that line
                     * width stays the same. */
                    if (line.fixed) {
                        width = Math.max(line.width, 1) / scale;
                    } else {
                        width = lb.scale * line.width / variableScale;
                    }

                    gl.uniform1f(uLineWidth,
                            (float) (width * COORD_SCALE_BY_DIR_SCALE));

                    /* Line-edge fade */
                    if (line.blur > 0) {
                        gl.uniform1f(uLineFade, line.blur);
                        blur = true;
                    } else if (mode == SHADER_FLAT) {
                        gl.uniform1f(uLineFade, (float) (pixel / width));
                        //GL.uniform1f(uLineScale, (float)(pixel / (ll.width / s)));
                    }

                    /* Cap mode */
                    if (lb.scale < 1.5/* || lb.line.fixed*/) {
                        if (capMode != CAP_THIN) {
                            capMode = CAP_THIN;
                            gl.uniform1f(uLineMode, capMode);
                        }
                    } else if (lb.roundCap) {
                        if (capMode != CAP_ROUND) {
                            capMode = CAP_ROUND;
                            gl.uniform1f(uLineMode, capMode);
                        }
                    } else if (capMode != CAP_BUTT) {
                        capMode = CAP_BUTT;
                        gl.uniform1f(uLineMode, capMode);
                    }

                    gl.drawArrays(GL.TRIANGLE_STRIP,
                            b.vertexOffset, b.numVertices);

                    continue;
                }

                /* draw LineLayers references by this outline */

                for (LineBucket ref = lb.outlines; ref != null; ref = ref.outlines) {
                    LineStyle core = ref.line.current();

                    // core width
                    if (core.fixed) {
                        width = Math.max(core.width, 1) / scale;
                    } else {
                        width = ref.scale * core.width / variableScale;
                    }
                    // add outline width
                    if (line.fixed) {
                        width += line.width / scale;
                    } else {
                        width += lb.scale * line.width / variableScale;
                    }

                    gl.uniform1f(uLineWidth,
                            (float) (width * COORD_SCALE_BY_DIR_SCALE));

                    /* Line-edge fade */
                    if (line.blur > 0) {
                        gl.uniform1f(uLineFade, line.blur);
                        blur = true;
                    } else if (mode == SHADER_FLAT) {
                        gl.uniform1f(uLineFade, (float) (pixel / width));
                    }

                    /* Cap mode */
                    if (ref.roundCap) {
                        if (capMode != CAP_ROUND) {
                            capMode = CAP_ROUND;
                            gl.uniform1f(uLineMode, capMode);
                        }
                    } else if (capMode != CAP_BUTT) {
                        capMode = CAP_BUTT;
                        gl.uniform1f(uLineMode, capMode);
                    }

                    gl.drawArrays(GL.TRIANGLE_STRIP,
                            ref.vertexOffset, ref.numVertices);
                }
            }

            return b;
        }
    }
}
