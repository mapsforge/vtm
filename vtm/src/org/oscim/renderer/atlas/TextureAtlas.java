/*
 * Copyright 2013 Hannes Janetzek
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

// ported from:
/* ============================================================================
 * Freetype GL - A C OpenGL Freetype engine
 * Platform:    Any
 * WWW:         http://code.google.com/p/freetype-gl/
 * ----------------------------------------------------------------------------
 * Copyright 2011,2012 Nicolas P. Rougier. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY NICOLAS P. ROUGIER ''AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL NICOLAS P. ROUGIER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of Nicolas P. Rougier.
 * ============================================================================
 *
 * This source is based on the article by Jukka Jylanki :
 * "A Thousand Ways to Pack the Bin - A Practical Approach to
 * Two-Dimensional Rectangle Bin Packing", February 27, 2010.
 *
 * More precisely, this is an implementation of the Skyline Bottom-Left
 * algorithm based on C++ sources provided by Jukka Jylanki at:
 * http://clb.demon.fi/files/RectangleBinPack/
 *
 *  ============================================================================
 */
package org.oscim.renderer.atlas;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.renderer.bucket.TextureItem;
import org.oscim.utils.pool.Inlist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TextureAtlas extends Inlist<TextureAtlas> {
    static final Logger log = LoggerFactory.getLogger(TextureAtlas.class);

    /**
     * Allocated slots
     */
    public Slot mSlots;
    private Rect mRects;

    /**
     * Width (in pixels) of the underlying texture
     */
    final int mWidth;

    /**
     * Height (in pixels) of the underlying texture
     */
    final int mHeight;

    /** Depth (in bytes) of the underlying texture */

    /**
     * Allocated surface size
     */
    int mUsed;

    public TextureItem texture;

    public static class Slot extends Inlist<Slot> {
        public int x, y, w;

        public Slot(int x, int y, int w) {
            this.x = x;
            this.y = y;
            this.w = w;
        }
    }

    public static class Rect extends Inlist<Rect> {
        public Rect(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public int x, y, w, h;

        @Override
        public String toString() {
            return x + ":" + y + " " + w + "x" + h;
        }
    }

    public TextureAtlas(int width, int height) {
        mWidth = width;
        mHeight = height;
        mSlots = new Slot(1, 1, width - 2);
    }

    public TextureAtlas(Bitmap bitmap) {
        texture = new TextureItem(bitmap);
        mWidth = texture.width;
        mHeight = texture.height;

        mRegions = new HashMap<Object, TextureRegion>();
    }

    private HashMap<Object, TextureRegion> mRegions;

    public void addTextureRegion(Object key, Rect r) {

        mRegions.put(key, new TextureRegion(this.texture, r));

    }

    public TextureRegion getTextureRegion(Object key) {
        return mRegions.get(key);
    }

    public Rect getRegion(int width, int height) {
        int y, bestHeight, bestWidth;
        Slot slot, prev;
        Rect r = new Rect(0, 0, width, height);

        bestHeight = Integer.MAX_VALUE;
        bestWidth = Integer.MAX_VALUE;

        Slot bestSlot = null;

        for (slot = mSlots; slot != null; slot = slot.next) {
            // fit width
            if ((slot.x + width) > (mWidth - 1))
                continue;

            // fit height
            y = slot.y;
            int widthLeft = width;

            Slot fit = slot;
            while (widthLeft > 0) {
                if (fit.y > y)
                    y = fit.y;

                if ((y + height) > (mHeight - 1)) {
                    y = -1;
                    break;
                }
                widthLeft -= fit.w;

                fit = fit.next;
            }

            if (y < 0)
                continue;

            int h = y + height;
            if ((h < bestHeight) || ((h == bestHeight) && (slot.w < bestWidth))) {
                bestHeight = h;
                bestSlot = slot;
                bestWidth = slot.w;
                r.x = slot.x;
                r.y = y;
            }
        }

        if (bestSlot == null)
            return null;

        Slot curSlot = new Slot(r.x, r.y + height, width);
        mSlots = Inlist.prependRelative(mSlots, curSlot, bestSlot);

        // split
        for (prev = curSlot; prev.next != null; ) {
            slot = prev.next;

            int shrink = (prev.x + prev.w) - slot.x;

            if (shrink <= 0)
                break;

            slot.x += shrink;
            slot.w -= shrink;
            if (slot.w > 0)
                break;

            // erease slot
            prev.next = slot.next;
        }

        // merge
        for (slot = mSlots; slot.next != null; ) {
            Slot nextSlot = slot.next;

            if (slot.y == nextSlot.y) {
                slot.w += nextSlot.w;

                // erease 'next' slot
                slot.next = nextSlot.next;
            } else {
                slot = nextSlot;
            }
        }

        mUsed += width * height;

        mRects = Inlist.push(mRects, r);
        return r;
    }

    public Map<Object, TextureRegion> getRegions() {
        return mRegions;
    }

    public void clear() {
        mRects = null;
        mSlots = new Slot(1, 1, mWidth - 2);
    }

    public static TextureAtlas create(int width, int height, int depth) {
        if (!(depth == 1 || depth == 3 || depth == 4))
            throw new IllegalArgumentException("invalid depth");

        return new TextureAtlas(width, height);
    }

    //    /// FIXME
    //    @Override
    //    protected void finalize(){
    //        if (texture != null)
    //            TextureItem.releaseTexture(texture);
    //    }
}
