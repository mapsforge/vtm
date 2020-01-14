/*
 * Copyright 2012 Hannes Janetzek
 * Copyright 2016 Longri
 * Copyright 2016 devemux86
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
import org.oscim.backend.canvas.Color;
import org.oscim.core.GeometryBuffer;
import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.renderer.*;
import org.oscim.theme.styles.AreaStyle;
import org.oscim.utils.ArrayUtils;
import org.oscim.utils.geom.LineClipper;
import org.oscim.utils.math.Interpolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ShortBuffer;

import static org.oscim.backend.GLAdapter.gl;
import static org.oscim.renderer.MapRenderer.COORD_SCALE;
import static org.oscim.utils.FastMath.clamp;

/**
 * Special Renderer for drawing tile polygons using the stencil buffer method
 */
public final class PolygonBucket extends RenderBucket {

    static final Logger log = LoggerFactory.getLogger(PolygonBucket.class);

    public static final int CLIP_STENCIL = 1;
    public static final int CLIP_DEPTH = 2;
    public static final int CLIP_TEST_DEPTH = 3;

    public static boolean enableTexture = true;

    public AreaStyle area;

    PolygonBucket(int layer) {
        super(RenderBucket.POLYGON, true, false);
        level = layer;
    }

    public void addPolygon(GeometryBuffer geom) {
        addPolygon(geom.points, geom.index);
    }

    float xmin = Short.MAX_VALUE;
    float ymin = Short.MAX_VALUE;
    float xmax = Short.MIN_VALUE;
    float ymax = Short.MIN_VALUE;

    final float[] bbox = new float[8];

    public void addPolygon(float[] points, int[] index) {
        short center = (short) ((Tile.SIZE >> 1) * COORD_SCALE);

        boolean outline = area.strokeWidth > 0;

        for (int i = 0, pos = 0, n = index.length; i < n; i++) {
            int length = index[i];
            if (length < 0)
                break;

            /* need at least three points */
            if (length < 6) {
                pos += length;
                continue;
            }

            vertexItems.add(center, center);
            numVertices++;

            int inPos = pos;

            for (int j = 0; j < length; j += 2) {
                float x = (points[inPos++] * COORD_SCALE);
                float y = (points[inPos++] * COORD_SCALE);
                xmax = Math.max(xmax, x);
                xmin = Math.min(xmin, x);
                ymax = Math.max(ymax, y);
                ymin = Math.min(ymin, y);

                if (outline) {
                    indiceItems.add((short) numVertices);
                    numIndices++;
                }

                vertexItems.add((short) x, (short) y);
                numVertices++;

                if (outline) {
                    indiceItems.add((short) numVertices);
                    numIndices++;
                }
            }

            vertexItems.add((short) (points[pos + 0] * COORD_SCALE),
                    (short) (points[pos + 1] * COORD_SCALE));
            numVertices++;

            pos += length;
        }
    }

    @Override
    protected void prepare() {
        ArrayUtils.setBox2D(bbox, xmin, ymin, xmax, ymax);
    }

    @Override
    protected void compile(ShortBuffer vboData, ShortBuffer iboData) {
        if (area.strokeWidth == 0) {
            /* add vertices to shared VBO */
            compileVertexItems(vboData);
        } else {
            /* compile with indexed outline */
            super.compile(vboData, iboData);
        }
    }

    static class Shader extends GLShader {
        int uMVP, uColor, uScale, aPos;

        Shader(String shaderFile) {
            if (!create(shaderFile))
                return;

            uMVP = getUniform("u_mvp");
            aPos = getAttrib("a_pos");
            uColor = getUniform("u_color");
            if ("polygon_layer_tex".equals(shaderFile))
                uScale = getUniform("u_scale");
        }
    }

    public static final class Renderer {

        private static final int STENCIL_BITS = 8;
        public static final int CLIP_BIT = 0x80;

        private static PolygonBucket[] mAreaLayer;

        private static Shader polyShader;
        private static Shader texShader;

        static boolean init() {
            polyShader = new Shader("base_shader");
            texShader = new Shader("polygon_layer_tex");

            mAreaLayer = new PolygonBucket[STENCIL_BITS];

            return true;
        }

        /**
         * Draw tile filling quad.
         */
        private static void fillPolygons(GLViewport v, int start, int end,
                                         MapPosition pos, float div) {

            /* draw to framebuffer */
            gl.colorMask(true, true, true, true);

            /* do not modify stencil buffer */
            gl.stencilMask(0x00);
            Shader s;

            for (int i = start; i < end; i++) {
                PolygonBucket l = mAreaLayer[i];
                AreaStyle a = l.area.current();

                if (enableTexture && (a.texture != null)) {
                    s = setShader(texShader, v.mvp, false);
                    float num = clamp((Tile.SIZE / a.texture.width) >> 1, 1, Tile.SIZE);

                    float scale = (float) pos.getZoomScale();
                    float transition = clamp(scale - 1, 0, 1);
                    transition = Interpolation.exp5.apply(transition);

                    gl.uniform2f(s.uScale, transition, div / num);
                    a.texture.bind();

                } else {
                    s = setShader(polyShader, v.mvp, false);
                }

                float fade = a.getFade(pos.scale);
                float blendFill = a.getBlend(pos.scale);
                boolean blend = (s == texShader || fade < 1.0);

                if (fade < 1.0) {
                    GLUtils.setColor(s.uColor, a.color, fade);
                } else if (blendFill > 0.0f) {
                    if (blendFill == 1.0f) {
                        GLUtils.setColor(s.uColor, a.blendColor, 1);
                    } else {
                        GLUtils.setColorBlend(s.uColor, a.color,
                                a.blendColor, blendFill);
                    }
                } else {
                    blend |= !Color.isOpaque(a.color);
                    GLUtils.setColor(s.uColor, a.color, fade);
                }

                GLState.blend(blend);

                /* set stencil buffer mask used to draw this layer
                 * also check that clip bit is set to avoid overdraw
                 * of other tiles */
                gl.stencilFunc(GL.EQUAL, 0xff, CLIP_BIT | 1 << i);

                /* draw tile fill coordinates */
                gl.drawArrays(GL.TRIANGLE_STRIP, 0, RenderBuckets.TILE_FILL_VERTICES);

                if (a.strokeWidth <= 0)
                    continue;

                gl.stencilFunc(GL.EQUAL, CLIP_BIT, CLIP_BIT);

                GLState.blend(true);

                HairLineBucket.Renderer.shader.set(v);

                GLUtils.setColor(HairLineBucket.Renderer.shader.uColor,
                        l.area.strokeColor, 1);

                gl.vertexAttribPointer(HairLineBucket.Renderer.shader.aPos,
                        2, GL.SHORT, false, 0,
                        // 4 bytes per vertex
                        l.vertexOffset << 2);

                gl.uniform1f(HairLineBucket.Renderer.shader.uWidth,
                        a.strokeWidth);

                gl.drawElements(GL.LINES,
                        l.numIndices,
                        GL.UNSIGNED_SHORT,
                        l.indiceOffset);
                gl.lineWidth(1);

                ///* disable texture shader */
                //if (s != polyShader)
                //    s = setShader(polyShader, v.mvp, false);
            }
        }

        /**
         * current layer to fill (0 - STENCIL_BITS-1)
         */
        private static int mCount;
        /**
         * must clear stencil for next draw
         */
        private static boolean mClear;

        private static Shader setShader(Shader shader, GLMatrix mvp, boolean first) {
            if (shader.useProgram() || first) {
                GLState.enableVertexArrays(shader.aPos, GLState.DISABLED);

                gl.vertexAttribPointer(shader.aPos, 2,
                        GL.SHORT, false, 0, 0);

                mvp.setAsUniform(shader.uMVP);
            }
            return shader;
        }

        static float[] mBBox = new float[8];
        static LineClipper mScreenClip = new LineClipper(-1, -1, 1, 1);

        /**
         * draw polygon buckets (until bucket.next is not polygon bucket)
         * using stencil buffer method
         *
         * @param buckets layer to draw (referencing vertices in current vbo)
         * @param v       GLViewport
         * @param div     scale relative to 'base scale' of the tile
         * @param first   pass true to clear stencil buffer region
         * @return next layer
         */
        public static RenderBucket draw(RenderBucket buckets, GLViewport v,
                                        float div, boolean first) {

            GLState.test(false, true);

            setShader(polyShader, v.mvp, first);

            int zoom = v.pos.zoomLevel;

            int cur = mCount;
            int start = mCount;

            /* draw to stencil buffer */
            gl.colorMask(false, false, false, false);

            /* op for stencil method polygon drawing */
            gl.stencilOp(GL.KEEP, GL.KEEP, GL.INVERT);

            boolean drawn = false;

            byte stencilMask = 0;

            float[] box = mBBox;

            RenderBucket b = buckets;
            for (; b != null && b.type == POLYGON; b = b.next) {
                PolygonBucket pb = (PolygonBucket) b;
                AreaStyle area = pb.area.current();

                /* fade out polygon bucket (set in RenderTheme) */
                if (area.fadeScale > 0 && area.fadeScale > zoom)
                    continue;

                if (div > 0.5) {
                    /* project bbox of polygon to screen */
                    v.mvp.prj2D(pb.bbox, 0, box, 0, 4);

                    int out = LineClipper.INSIDE;
                    for (int i = 0; i < 8; i += 2) {
                        int o = mScreenClip.outcode(box[i], box[i + 1]);

                        if (o == LineClipper.INSIDE) {
                            /* at least one corner is inside */
                            out = LineClipper.INSIDE;
                            break;
                        }
                        out |= o;
                    }
                    /* Check if any polygon-bucket edge intersects the screen.
                     * Also check the very unlikely case where the view might
                     * be completely contained within box */
                    if ((out != LineClipper.INSIDE) && (out != LineClipper.OUTSIDE)) {
                        mScreenClip.clipStart(box[6], box[7]);
                        out = LineClipper.OUTSIDE;
                        for (int i = 0; i < 8 && out == LineClipper.OUTSIDE; i += 2)
                            out = mScreenClip.clipNext(box[i], box[i + 1]);

                        if (out == LineClipper.OUTSIDE) {
                            //log.debug("out {}\n {}\n {}", out, Arrays.toString(pb.bbox), Arrays.toString(box));

                            //    log.debug("outside {} {} {}", out,
                            //              Arrays.toString(box),
                            //              Arrays.toString(pb.bbox));
                            continue;
                        }
                    }
                }
                if (mClear) {
                    clearStencilRegion();
                    /* op for stencil method polygon drawing */
                    gl.stencilOp(GL.KEEP, GL.KEEP, GL.INVERT);

                    start = cur = 0;
                }

                mAreaLayer[cur] = pb;

                /* set stencil mask to draw to */
                int stencil = 1 << cur++;

                if (area.hasAlpha(zoom)) {
                    gl.stencilMask(stencil);
                    stencilMask |= stencil;
                } else {
                    stencilMask |= stencil;
                    gl.stencilMask(stencilMask);
                }

                gl.drawArrays(GL.TRIANGLE_FAN, b.vertexOffset, b.numVertices);

                /* draw up to 7 buckets into stencil buffer */
                if (cur == STENCIL_BITS - 1) {
                    fillPolygons(v, start, cur, v.pos, div);
                    drawn = true;

                    mClear = true;
                    start = cur = 0;

                    if (b.next != null && b.next.type == POLYGON) {
                        setShader(polyShader, v.mvp, false);
                        stencilMask = 0;
                    }
                }
            }

            if (cur > 0) {
                fillPolygons(v, start, cur, v.pos, div);
                drawn = true;
            }

            if (!drawn) {
                /* fillPolygons would re-enable color-mask
                 * but it's possible that all polygon buckets
                 * were skipped */
                gl.colorMask(true, true, true, true);
                gl.stencilMask(0x00);
            }

            mCount = cur;
            return b;
        }

        public static void clip(GLMatrix mvp, int clipMode) {
            setShader(polyShader, mvp, true);

            drawStencilRegion(clipMode);

            /* disable writes to stencil buffer */
            gl.stencilMask(0x00);

            /* enable writes to color buffer */
            gl.colorMask(true, true, true, true);
        }

        /**
         * Draw a tile filling rectangle to set stencil- and depth buffer
         * appropriately
         *
         * @param clipMode clip to first quad in current vbo
         *                 using CLIP_STENCIL / CLIP_DEPTH
         */
        static void drawStencilRegion(int clipMode) {
            //log.debug("draw stencil {}", clipMode);
            mCount = 0;
            mClear = false;

            /* disable drawing to color buffer */
            gl.colorMask(false, false, false, false);

            /* write to all stencil bits */
            gl.stencilMask(0xFF);

            /* Draw clip-region into depth and stencil buffer.
             * This is used for tile line and polygon buckets.
             *
             * Together with depth test (GL20.LESS) this ensures to
             * only draw where no other tile has drawn yet. */

            if (clipMode == CLIP_DEPTH) {
                /* tests GL20.LESS/GL20.ALWAYS and */
                /* write tile region to depth buffer */
                GLState.test(true, true);
                gl.depthMask(true);
            } else {
                GLState.test(false, true);
            }

            /* always pass stencil test and set clip bit */
            gl.stencilFunc(GL.ALWAYS, CLIP_BIT, 0x00);

            /* set clip bit (0x80) for draw region */
            gl.stencilOp(GL.KEEP, GL.KEEP, GL.REPLACE);

            /* draw a quad for the tile region */
            gl.drawArrays(GL.TRIANGLE_STRIP, 0, RenderBuckets.TILE_FILL_VERTICES);

            if (clipMode == CLIP_DEPTH) {
                /* dont modify depth buffer */
                gl.depthMask(false);
                GLState.test(false, true);
            }
            gl.stencilFunc(GL.EQUAL, CLIP_BIT, CLIP_BIT);
        }

        static void clearStencilRegion() {

            mCount = 0;
            mClear = false;

            /* disable drawing to color buffer */
            gl.colorMask(false, false, false, false);

            /* write to all stencil bits except clip bit */
            gl.stencilMask(0xFF);

            /* use clip bit from stencil buffer to clear stencil
             * 'layer-bits' (0x7f) */
            gl.stencilFunc(GL.EQUAL, CLIP_BIT, CLIP_BIT);

            /* set clip bit (0x80) for draw region */
            gl.stencilOp(GL.KEEP, GL.KEEP, GL.REPLACE);

            /* draw a quad for the tile region */
            gl.drawArrays(GL.TRIANGLE_STRIP, 0, RenderBuckets.TILE_FILL_VERTICES);
        }

        /**
         * Clear stencilbuffer for a tile region by drawing
         * a quad with func 'always' and op 'zero'. Using 'color'
         * and 'alpha' to fake a fade effect.
         */
        public static void drawOver(GLMatrix mvp, int color, float alpha) {
            /* TODO true could be avoided when same shader and vbo */
            setShader(polyShader, mvp, true);

            if (color == 0) {
                gl.colorMask(false, false, false, false);
            } else {
                GLUtils.setColor(polyShader.uColor, color, alpha);
                GLState.blend(true);
            }

            // TODO always pass stencil test: <-- only if not proxy?
            //GL.stencilFunc(GL20.ALWAYS, 0x00, 0x00);

            gl.stencilFunc(GL.EQUAL, CLIP_BIT, CLIP_BIT);

            /* write to all bits */
            gl.stencilMask(0xFF);

            // FIXME uneeded probably
            GLState.test(false, true);

            /* zero out area to draw to */
            gl.stencilOp(GL.KEEP, GL.KEEP, GL.ZERO);

            gl.drawArrays(GL.TRIANGLE_STRIP, 0, RenderBuckets.TILE_FILL_VERTICES);

            if (color == 0)
                gl.colorMask(true, true, true, true);
        }

        private Renderer() {
            /* Singleton */
        }
    }
}
