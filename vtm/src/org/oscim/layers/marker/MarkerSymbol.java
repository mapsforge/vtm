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
package org.oscim.layers.marker;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.PointF;

public class MarkerSymbol {
    /**
     * Indicates a hotspot for an area. This is where the origin (0,0) of a
     * point will be located relative to the area. In otherwords this acts as an
     * offset. NONE indicates that no adjustment should be made.
     */
    public enum HotspotPlace {
        NONE, CENTER, BOTTOM_CENTER,
        TOP_CENTER, RIGHT_CENTER, LEFT_CENTER,
        UPPER_RIGHT_CORNER, LOWER_RIGHT_CORNER,
        UPPER_LEFT_CORNER, LOWER_LEFT_CORNER
    }

    final Bitmap mBitmap;
    /**
     * Hotspot offset
     */
    final PointF mOffset;
    final boolean mBillboard;

    public MarkerSymbol(Bitmap bitmap, float relX, float relY) {
        this(bitmap, relX, relY, true);
    }

    public MarkerSymbol(Bitmap bitmap, float relX, float relY, boolean billboard) {
        mBitmap = bitmap;
        mOffset = new PointF(relX, relY);
        mBillboard = billboard;
    }

    public MarkerSymbol(Bitmap bitmap, HotspotPlace hotspot) {
        this(bitmap, hotspot, true);
    }

    public MarkerSymbol(Bitmap bitmap, HotspotPlace hotspot, boolean billboard) {

        switch (hotspot) {
            case BOTTOM_CENTER:
                mOffset = new PointF(0.5f, 1);
                break;
            case TOP_CENTER:
                mOffset = new PointF(0.5f, 0);
                break;
            case RIGHT_CENTER:
                mOffset = new PointF(1, 0.5f);
                break;
            case LEFT_CENTER:
                mOffset = new PointF(0, 0.5f);
                break;
            case UPPER_RIGHT_CORNER:
                mOffset = new PointF(1, 0);
                break;
            case LOWER_RIGHT_CORNER:
                mOffset = new PointF(1, 1);
                break;
            case UPPER_LEFT_CORNER:
                mOffset = new PointF(0, 0);
                break;
            case LOWER_LEFT_CORNER:
                mOffset = new PointF(0, 1);
                break;
            default:
                mOffset = new PointF(0.5f, 0.5f);
        }

        mBitmap = bitmap;
        mBillboard = billboard;
    }

    public boolean isBillboard() {
        return mBillboard;
    }

    public PointF getHotspot() {
        return mOffset;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public boolean isInside(float dx, float dy) {
        /* TODO handle no-billboard */
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float ox = -w * mOffset.x;
        float oy = -h * (1 - mOffset.y);

        return dx >= ox && dy >= oy && dx <= ox + w && dy <= oy + h;
    }
}
