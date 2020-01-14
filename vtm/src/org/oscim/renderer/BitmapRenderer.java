/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 Andrey Novikov
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
package org.oscim.renderer;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.renderer.bucket.BitmapBucket;

import static org.oscim.renderer.MapRenderer.COORD_SCALE;

/**
 * RenderLayer to draw a custom Bitmap.
 * NOTE: Only modify the Bitmap within a synchronized block!
 * synchronized(bitmap){} Then call updateBitmap().
 */
public class BitmapRenderer extends BucketRenderer {

    private Bitmap mBitmap;
    private int mWidth;
    private int mHeight;
    private boolean mUpdateBitmap;
    private GLViewport.Position position = GLViewport.Position.TOP_LEFT;
    private float xOffset, yOffset;

    /**
     * @param bitmap with dimension being power of two
     * @param width  width used
     * @param height height used
     */
    public synchronized void setBitmap(Bitmap bitmap, int width, int height) {
        mBitmap = bitmap;
        mWidth = width;
        mHeight = height;
        mInitialized = false;
    }

    public synchronized void setPosition(GLViewport.Position position) {
        this.position = position;
    }

    public synchronized void setOffset(float xOffset, float yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public synchronized void updateBitmap() {
        mUpdateBitmap = true;
    }

    @Override
    public synchronized void update(GLViewport v) {
        if (!mInitialized) {
            buckets.clear();

            BitmapBucket l = new BitmapBucket(true);
            l.setBitmap(mBitmap, mWidth, mHeight);
            buckets.set(l);

            mUpdateBitmap = true;
        }

        if (mUpdateBitmap) {
            mUpdateBitmap = false;
            compile();
        }
    }

    @Override
    protected synchronized void compile() {
        if (mBitmap == null)
            return;

        synchronized (mBitmap) {
            super.compile();
        }
    }

    @Override
    public synchronized void render(GLViewport v) {
        v.useScreenCoordinates(mWidth, mHeight, position, xOffset, yOffset, COORD_SCALE);
        BitmapBucket.Renderer.draw(buckets.get(), v, 1, 1);
    }
}
