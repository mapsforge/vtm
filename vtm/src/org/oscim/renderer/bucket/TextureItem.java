/*
 * Copyright 2012, 2013 Hannes Janetzek
 * Copyright 2016 devemux86
 * Copyright 2017 Longri
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

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.GL;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Color;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLUtils;
import org.oscim.utils.pool.Inlist;
import org.oscim.utils.pool.SyncPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import javax.annotation.CheckReturnValue;

import static org.oscim.backend.GLAdapter.gl;

public class TextureItem extends Inlist<TextureItem> {
    static final Logger log = LoggerFactory.getLogger(TextureItem.class);

    static final boolean dbg = false;

    /**
     * texture ID
     */
    int id;

    /**
     * current settings
     */
    public final int width;
    public final int height;
    public final boolean repeat;

    /**
     * vertex offset from which this texture is referenced
     */
    /* FIXME dont put this here! */
    public int offset;
    public int indices;

    /**
     * temporary Bitmap
     */
    public Bitmap bitmap;

    /**
     * do not release the texture when TextureItem is released.
     */
    private TextureItem ref;
    private int used = 0;

    /**
     * texture data is ready
     */
    boolean loaded;

    final TexturePool pool;

    public boolean mipmap;

    private TextureItem(TexturePool pool, int id) {
        this(pool, id, pool.mWidth, pool.mHeight, false);
    }

    public TextureItem(Bitmap bitmap) {
        this(bitmap, false);
    }

    public TextureItem(Bitmap bitmap, boolean repeat) {
        this(NOPOOL, -1, bitmap.getWidth(), bitmap.getHeight(), repeat);
        this.bitmap = bitmap;
    }

    private TextureItem(TexturePool pool, int id, int width, int height, boolean repeat) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.pool = pool;
        this.repeat = repeat;
    }

    public static TextureItem clone(TextureItem ti) {

        TextureItem clone = new TextureItem(NOPOOL, ti.id, ti.width, ti.height, ti.repeat);
        clone.id = ti.id;
        clone.ref = (ti.ref == null) ? ti : ti.ref;
        clone.loaded = ti.loaded;

        clone.ref.used++;

        return clone;
    }

    /**
     * Upload Image to Texture
     * [on GL-Thread]
     */
    public void upload() {
        if (loaded)
            return;

        if (ref == null) {
            pool.uploadTexture(this);

        } else {
            /* load referenced texture */
            ref.upload();
            id = ref.id;

        }
        loaded = true;
    }

    /**
     * Bind Texture for rendering
     * [on GL-Thread]
     */
    public void bind() {
        if (loaded)
            GLState.bindTex2D(id);
        else
            upload();
    }

    /**
     * Dispose TextureItem
     * [Threadsafe]
     *
     * @return this.next
     */
    @CheckReturnValue
    public TextureItem dispose() {
        TextureItem n = this.next;
        this.next = null;
        pool.release(this);
        return n;
    }

    public static class TexturePool extends SyncPool<TextureItem> {
        private final ArrayList<Bitmap> mBitmaps = new ArrayList<Bitmap>(10);

        private final int mHeight;
        private final int mWidth;
        private final boolean mUseBitmapPool;
        private final boolean mMipmaps;

        //private final int mBitmapFormat;
        //private final int mBitmapType;

        protected int mTexCnt = 0;

        public TexturePool(int maxFill, int width, int height, boolean mipmap) {
            super(maxFill);
            mWidth = width;
            mHeight = height;
            mUseBitmapPool = true;
            mMipmaps = mipmap;
        }

        public TexturePool(int maxFill) {
            super(maxFill);
            mWidth = 0;
            mHeight = 0;
            mUseBitmapPool = false;
            mMipmaps = false;
        }

        @Override
        public TextureItem releaseAll(TextureItem t) {
            throw new RuntimeException("use TextureItem.dispose()");
        }

        /**
         * Retrieve a TextureItem from pool.
         */
        public synchronized TextureItem get() {
            TextureItem t = super.get();

            if (!mUseBitmapPool)
                return t;

            synchronized (mBitmaps) {
                int size = mBitmaps.size();
                if (size == 0)
                    t.bitmap = CanvasAdapter.newBitmap(mWidth, mHeight, 0);
                else {
                    t.bitmap = mBitmaps.remove(size - 1);
                    t.bitmap.eraseColor(Color.TRANSPARENT);
                }
            }

            return t;
        }

        public synchronized TextureItem get(Bitmap bitmap) {
            TextureItem t = super.get();
            t.bitmap = bitmap;

            return t;
        }

        @Override
        protected TextureItem createItem() {
            return new TextureItem(this, -1);
        }

        @Override
        protected boolean clearItem(TextureItem t) {

            if (t.used > 0)
                return false;

            if (t.ref != null) {
                /* dispose texture if this clone holds the last handle */
                if (t.ref.used == 0) {
                    t.ref.dispose();
                    return false;
                }
                t.ref.used--;
                return false;
            }

            t.loaded = false;

            if (mUseBitmapPool)
                releaseBitmap(t);

            return t.id >= 0;
        }

        @Override
        protected void freeItem(TextureItem t) {

            if (t.ref == null && t.used == 0 && t.id >= 0) {
                mTexCnt--;
                synchronized (disposedTextures) {
                    disposedTextures.add(Integer.valueOf(t.id));
                    t.id = -1;
                }
            }
        }

        protected void releaseBitmap(TextureItem t) {

            if (t.bitmap == null)
                return;

            synchronized (mBitmaps) {
                mBitmaps.add(t.bitmap);
                t.bitmap = null;
            }
        }

        private void uploadTexture(TextureItem t) {

            if (t.bitmap == null)
                throw new RuntimeException("Missing bitmap for texture");

            if (t.id < 0) {
                int[] textureIds = GLUtils.glGenTextures(1);
                t.id = textureIds[0];

                t.mipmap |= mMipmaps;

                initTexture(t);

                if (dbg)
                    log.debug("fill:" + getFill()
                            + " count:" + mTexCnt
                            + " new texture " + t.id);

                mTexCnt++;

                t.bitmap.uploadToTexture(false);
            } else {
                GLState.bindTex2D(t.id);

                /* use faster subimage upload */
                t.bitmap.uploadToTexture(true);
            }

            if (t.mipmap)
                gl.generateMipmap(GL.TEXTURE_2D);

            if (dbg)
                GLUtils.checkGlError(TextureItem.class.getName());

            if (mUseBitmapPool)
                releaseBitmap(t);
        }

        protected void initTexture(TextureItem t) {
            GLState.bindTex2D(t.id);

            if (t.mipmap) {
                gl.texParameterf(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER,
                        GL.LINEAR_MIPMAP_LINEAR);
            } else {
                gl.texParameterf(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER,
                        GL.LINEAR);
            }

            gl.texParameterf(GL.TEXTURE_2D, GL.TEXTURE_MAG_FILTER,
                    GL.LINEAR);

            if (t.repeat) {
                gl.texParameterf(GL.TEXTURE_2D, GL.TEXTURE_WRAP_S,
                        GL.REPEAT);
                gl.texParameterf(GL.TEXTURE_2D, GL.TEXTURE_WRAP_T,
                        GL.REPEAT);
            } else {
                gl.texParameterf(GL.TEXTURE_2D, GL.TEXTURE_WRAP_S,
                        GL.CLAMP_TO_EDGE);
                gl.texParameterf(GL.TEXTURE_2D, GL.TEXTURE_WRAP_T,
                        GL.CLAMP_TO_EDGE);
            }
        }
    }

    /* Pool for not-pooled textures. Disposed items will only be released
     * on the GL-Thread and will not be put back in any pool. */
    final static TexturePool NOPOOL = new TexturePool(0);
    final static ArrayList<Integer> disposedTextures = new ArrayList<Integer>();

    /**
     * Disposed textures are released by MapRenderer after each frame
     */
    public static void disposeTextures() {
        synchronized (disposedTextures) {

            int size = disposedTextures.size();
            if (size > 0) {
                int[] tmp = new int[size];
                for (int i = 0; i < size; i++)
                    tmp[i] = disposedTextures.get(i).intValue();

                disposedTextures.clear();
                GLUtils.glDeleteTextures(size, tmp);
            }
        }
    }
}
