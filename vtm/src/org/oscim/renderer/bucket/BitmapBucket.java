/*
 * Copyright 2013 Hannes Janetzek
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
import org.oscim.backend.canvas.Bitmap;
import org.oscim.renderer.GLShader;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.bucket.TextureItem.TexturePool;

import java.nio.ShortBuffer;

import static org.oscim.backend.GLAdapter.gl;
import static org.oscim.renderer.MapRenderer.COORD_SCALE;
import static org.oscim.renderer.MapRenderer.MAX_INDICES;
import static org.oscim.renderer.MapRenderer.bindQuadIndicesVBO;

/**
 * Renderer for a single bitmap, width and height must be power of 2.
 */
public class BitmapBucket extends TextureBucket {
    // TODO share layers.vbo() between BitmapTileLayers

    //    static final Logger log = LoggerFactory.getLogger(BitmapLayer.class);
    private Bitmap mBitmap;
    private final boolean mReuseBitmap;
    private final short[] mVertices;
    private int mWidth, mHeight;

    /**
     * @param reuseBitmap false if the Bitmap should be disposed
     *                    after loading to texture.
     */
    public BitmapBucket(boolean reuseBitmap) {
        super(RenderBucket.BITMAP);

        mReuseBitmap = reuseBitmap;
        mVertices = new short[24];

        // used for size calculation of Layers buffer.
        numVertices = 4;
    }

    /**
     * w/h sets also target dimension to render the bitmap.
     */

    public void setBitmap(Bitmap bitmap, int w, int h) {
        setBitmap(bitmap, w, h, null);
    }

    public void setBitmap(Bitmap bitmap, int w, int h, TexturePool pool) {

        mWidth = w;
        mHeight = h;

        mBitmap = bitmap;
        if (textures == null) {
            if (pool == null)
                textures = new TextureItem(mBitmap);
            else {
                textures = pool.get(mBitmap);
            }
        }

        TextureItem t = textures;
        t.indices = TextureBucket.INDICES_PER_SPRITE;
    }

    private void setVertices(ShortBuffer vboData) {
        short[] buf = mVertices;
        short w = (short) (mWidth * COORD_SCALE);
        short h = (short) (mHeight * COORD_SCALE);

        short texMin = 0;
        short texMax = 1;

        //    putSprite(buf, pos, tx, ty, x1, y1, x2, y2, u1, v1, u2, v2);
        int pos = 0;

        // top-left
        buf[pos++] = 0;
        buf[pos++] = 0;
        buf[pos++] = -1;
        buf[pos++] = -1;
        buf[pos++] = texMin;
        buf[pos++] = texMin;
        // bot-left
        buf[pos++] = 0;
        buf[pos++] = h;
        buf[pos++] = -1;
        buf[pos++] = -1;
        buf[pos++] = texMin;
        buf[pos++] = texMax;
        // top-right
        buf[pos++] = w;
        buf[pos++] = 0;
        buf[pos++] = -1;
        buf[pos++] = -1;
        buf[pos++] = texMax;
        buf[pos++] = texMin;
        // bot-right
        buf[pos++] = w;
        buf[pos++] = h;
        buf[pos++] = -1;
        buf[pos++] = -1;
        buf[pos++] = texMax;
        buf[pos++] = texMax;

        this.vertexOffset = vboData.position() * RenderBuckets.SHORT_BYTES;
        vboData.put(buf);
    }

    @Override
    protected void compile(ShortBuffer vboData, ShortBuffer iboData) {

        if (mBitmap == null)
            return;

        setVertices(vboData);

        textures.upload();

        if (!mReuseBitmap) {
            mBitmap.recycle();
            mBitmap = null;
            textures.bitmap = null;
        }
    }

    @Override
    protected void clear() {

        // release textures and vertexItems
        super.clear();

        if (mBitmap == null)
            return;

        if (!mReuseBitmap)
            mBitmap.recycle();

        mBitmap = null;

        //textures.bitmap = null;
        //textures.dispose();
        //TextureItem.pool.releaseTexture(textures);
        //textures = null;
    }

    static class Shader extends GLShader {
        int uMVP, uAlpha, aPos, aTexCoord;

        Shader(String shaderFile) {
            if (!create(shaderFile))
                return;
            uMVP = getUniform("u_mvp");
            uAlpha = getUniform("u_alpha");
            aPos = getAttrib("a_pos");
            aTexCoord = getAttrib("a_tex_coord");
        }

        @Override
        public boolean useProgram() {
            if (super.useProgram()) {
                GLState.enableVertexArrays(aPos, aTexCoord);
                return true;
            }
            return false;
        }
    }

    public static final class Renderer {

        public static final int INDICES_PER_SPRITE = 6;
        static final int VERTICES_PER_SPRITE = 4;
        static final int SHORTS_PER_VERTICE = 6;
        static Shader shader;

        static void init() {
            shader = new Shader("texture_alpha");
        }

        public static RenderBucket draw(RenderBucket b, GLViewport v,
                                        float scale, float alpha) {

            GLState.blend(true);
            Shader s = shader;
            s.useProgram();

            TextureBucket tb = (TextureBucket) b;

            gl.uniform1f(s.uAlpha, alpha);
            v.mvp.setAsUniform(s.uMVP);

            bindQuadIndicesVBO();

            for (TextureItem t = tb.textures; t != null; t = t.next) {
                t.bind();

                for (int i = 0; i < t.indices; i += MAX_INDICES) {
                    /* to.offset * (24(shorts) *
                     * 2(short-bytes) / 6(indices) == 8) */
                    int off = (t.offset + i) * 8 + tb.vertexOffset;

                    gl.vertexAttribPointer(s.aPos, 2,
                            GL.SHORT, false, RenderBuckets.SHORT_BYTES * 6, off);

                    gl.vertexAttribPointer(s.aTexCoord, 2,
                            GL.SHORT, false, RenderBuckets.SHORT_BYTES * 6, off + RenderBuckets.SHORT_BYTES * 4);

                    int numIndices = t.indices - i;
                    if (numIndices > MAX_INDICES)
                        numIndices = MAX_INDICES;

                    gl.drawElements(GL.TRIANGLES, numIndices,
                            GL.UNSIGNED_SHORT, 0);
                }
            }

            return b.next;
        }
    }
}
