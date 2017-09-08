/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2017 devemux86
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
package org.oscim.core;

/**
 * A tile represents a rectangular part of the world map. All tiles can be
 * identified by their X and Y number together with their zoom level. The actual
 * area that a tile covers on a map depends on the underlying map projection.
 */
public class Tile {

    /**
     * Default tile size in pixels.
     */
    private static final int DEFAULT_TILE_SIZE = 256;

    /**
     * Width and height of a map tile in pixels.
     */
    public static int SIZE = 512;

    /**
     * Tile size multiple in pixels.
     */
    public static int TILE_SIZE_MULTIPLE = 64;

    /**
     * the map size implied by zoom level and tileSize, to avoid multiple computations.
     */
    public final long mapSize;

    /**
     * The X number of this tile.
     */
    public final int tileX;

    /**
     * The Y number of this tile.
     */
    public final int tileY;

    /**
     * The Zoom level of this tile.
     */
    public final byte zoomLevel;

    private BoundingBox boundingBox;
    private Point origin;

    /**
     * @param tileX     the X number of the tile.
     * @param tileY     the Y number of the tile.
     * @param zoomLevel the zoom level of the tile.
     */
    public Tile(int tileX, int tileY, byte zoomLevel) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.zoomLevel = zoomLevel;
        this.mapSize = MercatorProjection.getMapSize(zoomLevel);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("[X:")
                .append(this.tileX)
                .append(", Y:")
                .append(this.tileY)
                .append(", Z:")
                .append(this.zoomLevel)
                .append("]")
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof Tile))
            return false;

        Tile o = (Tile) obj;

        if (o.tileX == this.tileX && o.tileY == this.tileY
                && o.zoomLevel == this.zoomLevel)
            return true;

        return false;
    }

    private int mHash = 0;

    @Override
    public int hashCode() {
        if (mHash == 0) {
            int result = 7;
            result = 31 * result + this.tileX;
            result = 31 * result + this.tileY;
            result = 31 * result + this.zoomLevel;
            mHash = result;
        }
        return mHash;
    }

    /**
     * Calculate tile size (256px) with a scale factor.
     * Clamp tile size to a preset multiple, e.g. 64px.
     */
    public static int calculateTileSize(float scaleFactor) {
        float scaled = DEFAULT_TILE_SIZE * scaleFactor;
        return Math.max(TILE_SIZE_MULTIPLE,
                Math.round(scaled / TILE_SIZE_MULTIPLE) * TILE_SIZE_MULTIPLE);
    }

    /**
     * Gets the geographic extend of this Tile as a BoundingBox.
     *
     * @return boundaries of this tile.
     */
    public BoundingBox getBoundingBox() {
        if (this.boundingBox == null) {
            double minLatitude = Math.max(MercatorProjection.LATITUDE_MIN, MercatorProjection.tileYToLatitude(tileY + 1, zoomLevel));
            double minLongitude = Math.max(-180, MercatorProjection.tileXToLongitude(this.tileX, zoomLevel));
            double maxLatitude = Math.min(MercatorProjection.LATITUDE_MAX, MercatorProjection.tileYToLatitude(this.tileY, zoomLevel));
            double maxLongitude = Math.min(180, MercatorProjection.tileXToLongitude(tileX + 1, zoomLevel));
            if (maxLongitude == -180) {
                // fix for dateline crossing, where the right tile starts at -180 and causes an invalid bbox
                maxLongitude = 180;
            }
            this.boundingBox = new BoundingBox(minLatitude, minLongitude, maxLatitude, maxLongitude);
        }
        return this.boundingBox;
    }

    /**
     * Return the BoundingBox of a rectangle of tiles defined by upper left and lower right tile.
     *
     * @param upperLeft  tile in upper left corner.
     * @param lowerRight tile in lower right corner.
     * @return BoundingBox defined by the area around upperLeft and lowerRight Tile.
     */
    public static BoundingBox getBoundingBox(Tile upperLeft, Tile lowerRight) {
        BoundingBox ul = upperLeft.getBoundingBox();
        BoundingBox lr = lowerRight.getBoundingBox();
        return ul.extendBoundingBox(lr);
    }

    /**
     * @return the maximum valid tile number for the given zoom level, 2<sup>zoomLevel</sup> -1.
     */
    public static int getMaxTileNumber(byte zoomLevel) {
        if (zoomLevel < 0) {
            throw new IllegalArgumentException("zoomLevel must not be negative: " + zoomLevel);
        } else if (zoomLevel == 0) {
            return 0;
        }
        return (2 << zoomLevel - 1) - 1;
    }

    /**
     * Returns the top-left point of this tile in absolute coordinates.
     *
     * @return the top-left point
     */
    public Point getOrigin() {
        if (this.origin == null) {
            double x = MercatorProjection.tileToPixel(this.tileX);
            double y = MercatorProjection.tileToPixel(this.tileY);
            this.origin = new Point(x, y);
        }
        return this.origin;
    }
}
