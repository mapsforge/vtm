/*
 * Copyright 2018 Gustl22
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
package org.oscim.layers.tile;

public class ZoomLimiter {

    private int mMaxZoom;
    private int mMinZoom;

    /**
     * Indicates that layer isn't processed again over zoom limit
     */
    private int mZoomLimit;

    private TileManager mTileManager;

    /**
     * A layer which avoid rendering tiles over a specific zoom limit
     */
    public ZoomLimiter(TileManager tileManager, int minZoom, int maxZoom, int zoomLimit) {
        if (zoomLimit < minZoom || zoomLimit > maxZoom) {
            throw new IllegalArgumentException("Zoom limit is out of range");
        }
        mTileManager = tileManager;
        mZoomLimit = zoomLimit;
        mMinZoom = minZoom;
        mMaxZoom = maxZoom;
    }

    public void addZoomLimit() {
        if (mZoomLimit < mMaxZoom && mZoomLimit >= mMinZoom) {
            // Request tiles of mZoomLimit.
            mTileManager.addZoomLimit(mZoomLimit);
        }
    }

    public void removeZoomLimit() {
        if (mZoomLimit < mMaxZoom && mZoomLimit >= mMinZoom) {
            mTileManager.removeZoomLimit(mZoomLimit);
        }
    }

    /**
     * Get tile of zoom limit if zoomLevel is larger than limit
     */
    public MapTile getTile(MapTile t) {
        if (t.zoomLevel > mZoomLimit && t.zoomLevel <= mMaxZoom) {
            int diff = t.zoomLevel - mZoomLimit;
            return mTileManager.getTile(t.tileX >> diff, t.tileY >> diff, mZoomLimit);
        }
        return t;
    }

    public int getMaxZoom() {
        return mMaxZoom;
    }

    public int getMinZoom() {
        return mMinZoom;
    }

    /*public TileManager getTileManager() {
        return mTileManager;
    }*/

    public int getZoomLimit() {
        return mZoomLimit;
    }

    public interface IZoomLimiter {
        /**
         * Sets the ZoomLimiter
         */
        void setZoomLimiter(ZoomLimiter zoomLimiter);

        /**
         * Add zoom limit to tile manager to load these tiles
         */
        void addZoomLimit();

        /**
         * Remove zoom limit
         */
        void removeZoomLimit();

    }
}
