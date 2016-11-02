/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2012 Hannes Janetzek
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
package org.oscim.core;

import org.oscim.utils.FastMath;

/**
 * An implementation of the spherical Mercator projection.
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
     * Calculates the distance on the ground that is represented by a single
     * pixel on the map.
     *
     * @param latitude the latitude coordinate at which the resolution should be
     *                 calculated.
     * @param scale    the map scale at which the resolution should be calculated.
     * @return the ground resolution at the given latitude and zoom level.
     */
    public static double groundResolution(double latitude, double scale) {
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
     * Projects a latitude coordinate (in degrees) to the range [0.0,1.0]
     *
     * @param latitude the latitude coordinate that should be converted.
     * @return the position.
     */
    public static double latitudeToY(double latitude) {
        double sinLatitude = Math.sin(latitude * (Math.PI / 180));
        return FastMath.clamp(0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI), 0.0, 1.0);
    }

    public static double toLatitude(double y) {
        return 90 - 360 * Math.atan(Math.exp((y - 0.5) * (2 * Math.PI))) / Math.PI;
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

    public static double toLongitude(double x) {
        return 360.0 * (x - 0.5);
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

    public static double wrapLongitude(double longitude) {
        if (longitude < -180)
            return Math.max(Math.min(360 + longitude, LONGITUDE_MAX), LONGITUDE_MIN);
        else if (longitude > 180)
            return Math.max(Math.min(longitude - 360, LONGITUDE_MAX), LONGITUDE_MIN);

        return longitude;
    }

    private MercatorProjection() {
    }
}
