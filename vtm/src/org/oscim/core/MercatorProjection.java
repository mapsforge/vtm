/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2012 Hannes Janetzek
 * Copyright 2014 Ludwig M Brinckmann
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

import org.oscim.utils.FastMath;

/**
 * An implementation of the spherical Mercator projection.
 * <p/>
 * There are generally two methods for each operation: one taking a byte zoom level and
 * a parallel one taking a double scale. The scale is Math.pow(2, zoomLevel)
 * for a given zoom level, but the operations take intermediate values as well.
 * The zoom level operation is a little faster as it can make use of shift operations,
 * the scale operation offers greater flexibility in computing the values for
 * intermediate zoom levels.
 */
public final class MercatorProjection {
    /**
     * The circumference of the earth at the equator in meters.
     */
    public static final double EARTH_CIRCUMFERENCE = 40075016.686;

    /**
     * Maximum possible latitude coordinate of the map.
     */
    public static final double LATITUDE_MAX = 85.05112877980659;

    /**
     * Minimum possible latitude coordinate of the map.
     */
    public static final double LATITUDE_MIN = -LATITUDE_MAX;

    /**
     * Maximum possible longitude coordinate of the map.
     */
    public static final double LONGITUDE_MAX = 180;

    /**
     * Minimum possible longitude coordinate of the map.
     */
    public static final double LONGITUDE_MIN = -LONGITUDE_MAX;

    /**
     * Get GeoPoint from Pixels.
     */
    public static GeoPoint fromPixelsWithScale(double pixelX, double pixelY, double scale) {
        return new GeoPoint(pixelYToLatitudeWithScale(pixelY, scale),
                pixelXToLongitudeWithScale(pixelX, scale));
    }

    /**
     * Get GeoPoint from Pixels.
     */
    public static GeoPoint fromPixels(double pixelX, double pixelY, long mapSize) {
        return new GeoPoint(pixelYToLatitude(pixelY, mapSize),
                pixelXToLongitude(pixelX, mapSize));
    }

    /**
     * @param scale the scale factor for which the size of the world map should be returned.
     * @return the horizontal and vertical size of the map in pixel at the given scale.
     * @throws IllegalArgumentException if the given scale factor is < 1
     */
    public static long getMapSizeWithScale(double scale) {
        if (scale < 1) {
            throw new IllegalArgumentException("scale factor must not < 1 " + scale);
        }
        return (long) (Tile.SIZE * (Math.pow(2, scaleToZoomLevel(scale))));
    }

    /**
     * @param zoomLevel the zoom level for which the size of the world map should be returned.
     * @return the horizontal and vertical size of the map in pixel at the given zoom level.
     * @throws IllegalArgumentException if the given zoom level is negative.
     */
    public static long getMapSize(byte zoomLevel) {
        if (zoomLevel < 0) {
            throw new IllegalArgumentException("zoom level must not be negative: " + zoomLevel);
        }
        return (long) Tile.SIZE << zoomLevel;
    }

    public static Point getPixelWithScale(GeoPoint geoPoint, double scale) {
        double pixelX = MercatorProjection.longitudeToPixelXWithScale(geoPoint.getLongitude(), scale);
        double pixelY = MercatorProjection.latitudeToPixelYWithScale(geoPoint.getLatitude(), scale);
        return new Point(pixelX, pixelY);
    }

    public static Point getPixel(GeoPoint geoPoint, long mapSize) {
        double pixelX = MercatorProjection.longitudeToPixelX(geoPoint.getLongitude(), mapSize);
        double pixelY = MercatorProjection.latitudeToPixelY(geoPoint.getLatitude(), mapSize);
        return new Point(pixelX, pixelY);
    }

    /**
     * Calculates the absolute pixel position for a map size and tile size
     *
     * @param geoPoint the geographic position.
     * @param mapSize  precomputed size of map.
     * @return the absolute pixel coordinates (for world)
     */

    public static Point getPixelAbsolute(GeoPoint geoPoint, long mapSize) {
        return getPixelRelative(geoPoint, mapSize, 0, 0);
    }

    /**
     * Calculates the absolute pixel position for a map size and tile size relative to origin
     *
     * @param geoPoint the geographic position.
     * @param mapSize  precomputed size of map.
     * @return the relative pixel position to the origin values (e.g. for a tile)
     */
    public static Point getPixelRelative(GeoPoint geoPoint, long mapSize, double x, double y) {
        double pixelX = MercatorProjection.longitudeToPixelX(geoPoint.getLongitude(), mapSize) - x;
        double pixelY = MercatorProjection.latitudeToPixelY(geoPoint.getLatitude(), mapSize) - y;
        return new Point(pixelX, pixelY);
    }


    /**
     * Calculates the absolute pixel position for a map size and tile size relative to origin
     *
     * @param geoPoint the geographic position.
     * @param mapSize  precomputed size of map.
     * @return the relative pixel position to the origin values (e.g. for a tile)
     */
    public static Point getPixelRelative(GeoPoint geoPoint, long mapSize, Point origin) {
        return getPixelRelative(geoPoint, mapSize, origin.x, origin.y);
    }

    /**
     * Calculates the absolute pixel position for a tile and tile size relative to origin
     *
     * @param geoPoint the geographic position.
     * @param tile     tile
     * @return the relative pixel position to the origin values (e.g. for a tile)
     */
    public static Point getPixelRelativeToTile(GeoPoint geoPoint, Tile tile) {
        return getPixelRelative(geoPoint, tile.mapSize, tile.getOrigin());
    }

    /**
     * Calculates the distance on the ground that is represented by a single
     * pixel on the map.
     *
     * @param latitude the latitude coordinate at which the resolution should be
     *                 calculated.
     * @param scale    the map scale at which the resolution should be calculated.
     * @return the ground resolution at the given latitude and scale.
     */
    public static double groundResolutionWithScale(double latitude, double scale) {
        return Math.cos(latitude * (Math.PI / 180)) * EARTH_CIRCUMFERENCE
                / (Tile.SIZE * scale);
    }

    public static float groundResolution(MapPosition pos) {
        double lat = MercatorProjection.toLatitude(pos.y);
        return (float) (Math.cos(lat * (Math.PI / 180))
                * MercatorProjection.EARTH_CIRCUMFERENCE
                / (Tile.SIZE * pos.scale));
    }

    /**
     * Calculates the distance on the ground that is represented by a single pixel on the map.
     *
     * @param latitude the latitude coordinate at which the resolution should be calculated.
     * @param mapSize  precomputed size of map.
     * @return the ground resolution at the given latitude and map size.
     */
    public static double groundResolution(double latitude, long mapSize) {
        return Math.cos(latitude * (Math.PI / 180)) * EARTH_CIRCUMFERENCE / mapSize;
    }

    /**
     * Converts a latitude coordinate (in degrees) to a pixel Y coordinate at a certain scale.
     *
     * @param latitude the latitude coordinate that should be converted.
     * @param scale    the scale factor at which the coordinate should be converted.
     * @return the pixel Y coordinate of the latitude value.
     */
    public static double latitudeToPixelYWithScale(double latitude, double scale) {
        double sinLatitude = Math.sin(latitude * (Math.PI / 180));
        long mapSize = getMapSizeWithScale(scale);
        // FIXME improve this formula so that it works correctly without the clipping
        double pixelY = (0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI)) * mapSize;
        return Math.min(Math.max(0, pixelY), mapSize);
    }

    /**
     * Converts a latitude coordinate (in degrees) to a pixel Y coordinate at a certain zoom level.
     *
     * @param latitude  the latitude coordinate that should be converted.
     * @param zoomLevel the zoom level at which the coordinate should be converted.
     * @return the pixel Y coordinate of the latitude value.
     */
    public static double latitudeToPixelY(double latitude, byte zoomLevel) {
        double sinLatitude = Math.sin(latitude * (Math.PI / 180));
        long mapSize = getMapSize(zoomLevel);
        // FIXME improve this formula so that it works correctly without the clipping
        double pixelY = (0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI)) * mapSize;
        return Math.min(Math.max(0, pixelY), mapSize);
    }

    /**
     * Converts a latitude coordinate (in degrees) to a pixel Y coordinate at a certain map size.
     *
     * @param latitude the latitude coordinate that should be converted.
     * @param mapSize  precomputed size of map.
     * @return the pixel Y coordinate of the latitude value.
     */
    public static double latitudeToPixelY(double latitude, long mapSize) {
        double sinLatitude = Math.sin(latitude * (Math.PI / 180));
        // FIXME improve this formula so that it works correctly without the clipping
        double pixelY = (0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI)) * mapSize;
        return Math.min(Math.max(0, pixelY), mapSize);
    }

    /**
     * Converts a latitude coordinate (in degrees) to a tile Y number at a certain scale.
     *
     * @param latitude the latitude coordinate that should be converted.
     * @param scale    the scale factor at which the coordinate should be converted.
     * @return the tile Y number of the latitude value.
     */
    public static int latitudeToTileYWithScale(double latitude, double scale) {
        return pixelYToTileYWithScale(latitudeToPixelYWithScale(latitude, scale), scale);
    }

    /**
     * Converts a latitude coordinate (in degrees) to a tile Y number at a certain zoom level.
     *
     * @param latitude  the latitude coordinate that should be converted.
     * @param zoomLevel the zoom level at which the coordinate should be converted.
     * @return the tile Y number of the latitude value.
     */
    public static int latitudeToTileY(double latitude, byte zoomLevel) {
        return pixelYToTileY(latitudeToPixelY(latitude, zoomLevel), zoomLevel);
    }

    /**
     * Projects a latitude coordinate (in degrees) to the range [0.0,1.0]
     *
     * @param latitude the latitude coordinate that should be converted.
     * @return the position.
     */
    public static double latitudeToY(double latitude) {
        double sinLatitude = Math.sin(latitude * (Math.PI / 180));
        return FastMath.clamp(0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI), 0.0, 1.0);
    }

    /**
     * @param latitude the latitude value which should be checked.
     * @return the given latitude value, limited to the possible latitude range.
     */
    public static double limitLatitude(double latitude) {
        return Math.max(Math.min(latitude, LATITUDE_MAX), LATITUDE_MIN);
    }

    /**
     * @param longitude the longitude value which should be checked.
     * @return the given longitude value, limited to the possible longitude
     * range.
     */
    public static double limitLongitude(double longitude) {
        return Math.max(Math.min(longitude, LONGITUDE_MAX), LONGITUDE_MIN);
    }

    /**
     * Converts a longitude coordinate (in degrees) to a pixel X coordinate at a certain scale factor.
     *
     * @param longitude the longitude coordinate that should be converted.
     * @param scale     the scale factor at which the coordinate should be converted.
     * @return the pixel X coordinate of the longitude value.
     */
    public static double longitudeToPixelXWithScale(double longitude, double scale) {
        long mapSize = getMapSizeWithScale(scale);
        return (longitude + 180) / 360 * mapSize;
    }

    /**
     * Converts a longitude coordinate (in degrees) to a pixel X coordinate at a certain zoom level.
     *
     * @param longitude the longitude coordinate that should be converted.
     * @param zoomLevel the zoom level at which the coordinate should be converted.
     * @return the pixel X coordinate of the longitude value.
     */
    public static double longitudeToPixelX(double longitude, byte zoomLevel) {
        long mapSize = getMapSize(zoomLevel);
        return (longitude + 180) / 360 * mapSize;
    }

    /**
     * Converts a longitude coordinate (in degrees) to a pixel X coordinate at a certain map size.
     *
     * @param longitude the longitude coordinate that should be converted.
     * @param mapSize   precomputed size of map.
     * @return the pixel X coordinate of the longitude value.
     */
    public static double longitudeToPixelX(double longitude, long mapSize) {
        return (longitude + 180) / 360 * mapSize;
    }

    /**
     * Converts a longitude coordinate (in degrees) to the tile X number at a certain scale factor.
     *
     * @param longitude the longitude coordinate that should be converted.
     * @param scale     the scale factor at which the coordinate should be converted.
     * @return the tile X number of the longitude value.
     */
    public static int longitudeToTileXWithScale(double longitude, double scale) {
        return pixelXToTileXWithScale(longitudeToPixelXWithScale(longitude, scale), scale);
    }

    /**
     * Converts a longitude coordinate (in degrees) to the tile X number at a certain zoom level.
     *
     * @param longitude the longitude coordinate that should be converted.
     * @param zoomLevel the zoom level at which the coordinate should be converted.
     * @return the tile X number of the longitude value.
     */
    public static int longitudeToTileX(double longitude, byte zoomLevel) {
        return pixelXToTileX(longitudeToPixelX(longitude, zoomLevel), zoomLevel);
    }

    /**
     * Projects a longitude coordinate (in degrees) to the range [0.0,1.0]
     *
     * @param longitude the longitude coordinate that should be converted.
     * @return the position.
     */
    public static double longitudeToX(double longitude) {
        return (longitude + 180.0) / 360.0;
    }

    /**
     * Converts meters to pixels at latitude for zoom-level.
     *
     * @param meters   the meters to convert
     * @param latitude the latitude for the conversion.
     * @param scale    the scale factor for the conversion.
     * @return pixels that represent the meters at the given zoom-level and latitude.
     */
    public static double metersToPixelsWithScale(float meters, double latitude, double scale) {
        return meters / MercatorProjection.groundResolutionWithScale(latitude, scale);
    }

    /**
     * Converts meters to pixels at latitude for zoom-level.
     *
     * @param meters   the meters to convert
     * @param latitude the latitude for the conversion.
     * @param mapSize  precomputed size of map.
     * @return pixels that represent the meters at the given zoom-level and latitude.
     */
    public static double metersToPixels(float meters, double latitude, long mapSize) {
        return meters / MercatorProjection.groundResolution(latitude, mapSize);
    }

    /**
     * Converts a pixel X coordinate at a certain scale to a longitude coordinate.
     *
     * @param pixelX the pixel X coordinate that should be converted.
     * @param scale  the scale factor at which the coordinate should be converted.
     * @return the longitude value of the pixel X coordinate.
     * @throws IllegalArgumentException if the given pixelX coordinate is invalid.
     */
    public static double pixelXToLongitudeWithScale(double pixelX, double scale) {
        long mapSize = getMapSizeWithScale(scale);
        if (pixelX < 0 || pixelX > mapSize) {
            throw new IllegalArgumentException("invalid pixelX coordinate at scale " + scale + ": " + pixelX);
        }
        return 360 * ((pixelX / mapSize) - 0.5);
    }

    /**
     * Converts a pixel X coordinate at a certain map size to a longitude coordinate.
     *
     * @param pixelX  the pixel X coordinate that should be converted.
     * @param mapSize precomputed size of map.
     * @return the longitude value of the pixel X coordinate.
     * @throws IllegalArgumentException if the given pixelX coordinate is invalid.
     */

    public static double pixelXToLongitude(double pixelX, long mapSize) {
        if (pixelX < 0 || pixelX > mapSize) {
            throw new IllegalArgumentException("invalid pixelX coordinate " + mapSize + ": " + pixelX);
        }
        return 360 * ((pixelX / mapSize) - 0.5);
    }

    /**
     * Converts a pixel X coordinate to the tile X number.
     *
     * @param pixelX the pixel X coordinate that should be converted.
     * @param scale  the scale factor at which the coordinate should be converted.
     * @return the tile X number.
     */
    public static int pixelXToTileXWithScale(double pixelX, double scale) {
        return (int) Math.min(Math.max(pixelX / Tile.SIZE, 0), scale - 1);
    }

    /**
     * Converts a pixel X coordinate to the tile X number.
     *
     * @param pixelX    the pixel X coordinate that should be converted.
     * @param zoomLevel the zoom level at which the coordinate should be converted.
     * @return the tile X number.
     */
    public static int pixelXToTileX(double pixelX, byte zoomLevel) {
        return (int) Math.min(Math.max(pixelX / Tile.SIZE, 0), Math.pow(2, zoomLevel) - 1);
    }

    /**
     * Converts a pixel Y coordinate at a certain scale to a latitude coordinate.
     *
     * @param pixelY the pixel Y coordinate that should be converted.
     * @param scale  the scale factor at which the coordinate should be converted.
     * @return the latitude value of the pixel Y coordinate.
     * @throws IllegalArgumentException if the given pixelY coordinate is invalid.
     */
    public static double pixelYToLatitudeWithScale(double pixelY, double scale) {
        long mapSize = getMapSizeWithScale(scale);
        if (pixelY < 0 || pixelY > mapSize) {
            throw new IllegalArgumentException("invalid pixelY coordinate at scale " + scale + ": " + pixelY);
        }
        double y = 0.5 - (pixelY / mapSize);
        return 90 - 360 * Math.atan(Math.exp(-y * (2 * Math.PI))) / Math.PI;
    }

    /**
     * Converts a pixel Y coordinate at a certain map size to a latitude coordinate.
     *
     * @param pixelY  the pixel Y coordinate that should be converted.
     * @param mapSize precomputed size of map.
     * @return the latitude value of the pixel Y coordinate.
     * @throws IllegalArgumentException if the given pixelY coordinate is invalid.
     */
    public static double pixelYToLatitude(double pixelY, long mapSize) {
        if (pixelY < 0 || pixelY > mapSize) {
            throw new IllegalArgumentException("invalid pixelY coordinate " + mapSize + ": " + pixelY);
        }
        double y = 0.5 - (pixelY / mapSize);
        return 90 - 360 * Math.atan(Math.exp(-y * (2 * Math.PI))) / Math.PI;
    }

    /**
     * Converts a pixel Y coordinate to the tile Y number.
     *
     * @param pixelY the pixel Y coordinate that should be converted.
     * @param scale  the scale factor at which the coordinate should be converted.
     * @return the tile Y number.
     */
    public static int pixelYToTileYWithScale(double pixelY, double scale) {
        return (int) Math.min(Math.max(pixelY / Tile.SIZE, 0), scale - 1);
    }

    /**
     * Converts a pixel Y coordinate to the tile Y number.
     *
     * @param pixelY    the pixel Y coordinate that should be converted.
     * @param zoomLevel the zoom level at which the coordinate should be converted.
     * @return the tile Y number.
     */
    public static int pixelYToTileY(double pixelY, byte zoomLevel) {
        return (int) Math.min(Math.max(pixelY / Tile.SIZE, 0), Math.pow(2, zoomLevel) - 1);
    }

    public static Point project(GeoPoint p, Point reuse) {
        if (reuse == null)
            reuse = new Point();

        reuse.x = ((p.longitudeE6 / 1E6) + 180.0) / 360.0;

        double sinLatitude = Math.sin((p.latitudeE6 / 1E6) * (Math.PI / 180.0));
        reuse.y = 0.5 - Math.log((1.0 + sinLatitude) / (1.0 - sinLatitude)) / (4.0 * Math.PI);

        return reuse;
    }

    public static void project(GeoPoint p, double[] out, int pos) {

        out[pos * 2] = ((p.longitudeE6 / 1E6) + 180.0) / 360.0;

        double sinLatitude = Math.sin((p.latitudeE6 / 1E6) * (Math.PI / 180.0));
        out[pos * 2 + 1] = 0.5 - Math.log((1.0 + sinLatitude) / (1.0 - sinLatitude))
                / (4.0 * Math.PI);
    }

    public static void project(double latitude, double longitude, double[] out, int pos) {

        out[pos * 2] = (longitude + 180.0) / 360.0;

        double sinLatitude = Math.sin(latitude * (Math.PI / 180.0));
        out[pos * 2 + 1] = 0.5 - Math.log((1.0 + sinLatitude) / (1.0 - sinLatitude))
                / (4.0 * Math.PI);
    }

    /**
     * Converts a scale factor to a zoomLevel.
     * Note that this will return a double, as the scale factors cover the
     * intermediate zoom levels as well.
     *
     * @param scale the scale factor to convert to a zoom level.
     * @return the zoom level.
     */
    public static double scaleToZoomLevel(double scale) {
        return FastMath.log2((int) scale);
    }

    /**
     * @param tileNumber the tile number that should be converted.
     * @return the pixel coordinate for the given tile number.
     */
    public static long tileToPixel(long tileNumber) {
        return tileNumber * Tile.SIZE;
    }

    /**
     * Converts a tile X number at a certain scale to a longitude coordinate.
     *
     * @param tileX the tile X number that should be converted.
     * @param scale the scale factor at which the number should be converted.
     * @return the longitude value of the tile X number.
     */
    public static double tileXToLongitudeWithScale(long tileX, double scale) {
        return pixelXToLongitudeWithScale(tileX * Tile.SIZE, scale);
    }

    /**
     * Converts a tile X number at a certain zoom level to a longitude coordinate.
     *
     * @param tileX     the tile X number that should be converted.
     * @param zoomLevel the zoom level at which the number should be converted.
     * @return the longitude value of the tile X number.
     */
    public static double tileXToLongitude(long tileX, byte zoomLevel) {
        return pixelXToLongitude(tileX * Tile.SIZE, getMapSize(zoomLevel));
    }

    /**
     * Converts a tile Y number at a certain scale to a latitude coordinate.
     *
     * @param tileY the tile Y number that should be converted.
     * @param scale the scale factor at which the number should be converted.
     * @return the latitude value of the tile Y number.
     */
    public static double tileYToLatitudeWithScale(long tileY, double scale) {
        return pixelYToLatitudeWithScale(tileY * Tile.SIZE, scale);
    }

    /**
     * Converts a tile Y number at a certain zoom level to a latitude coordinate.
     *
     * @param tileY     the tile Y number that should be converted.
     * @param zoomLevel the zoom level at which the number should be converted.
     * @return the latitude value of the tile Y number.
     */
    public static double tileYToLatitude(long tileY, byte zoomLevel) {
        return pixelYToLatitude(tileY * Tile.SIZE, getMapSize(zoomLevel));
    }

    public static double toLatitude(double y) {
        return 90 - 360 * Math.atan(Math.exp((y - 0.5) * (2 * Math.PI))) / Math.PI;
    }

    public static double toLongitude(double x) {
        return 360.0 * (x - 0.5);
    }

    public static double wrapLongitude(double longitude) {
        if (longitude < -180)
            return Math.max(Math.min(360 + longitude, LONGITUDE_MAX), LONGITUDE_MIN);
        else if (longitude > 180)
            return Math.max(Math.min(longitude - 360, LONGITUDE_MAX), LONGITUDE_MIN);

        return longitude;
    }

    /**
     * Converts a zoom level to a scale factor.
     *
     * @param zoomLevel the zoom level to convert.
     * @return the corresponding scale factor.
     */
    public static double zoomLevelToScale(byte zoomLevel) {
        return 1 << zoomLevel;
    }

    private MercatorProjection() {
    }
}
