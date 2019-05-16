/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 * Copyright 2017 devemux86
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
package org.oscim.utils;

import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.core.MercatorProjection;
import org.oscim.core.Point;
import org.oscim.core.Tile;

/**
 * The coordinate validation functions come from Mapsforge <a href="https://github.com/mapsforge/mapsforge/blob/master/mapsforge-core/src/main/java/org/mapsforge/core/util/LatLongUtils.java">LatLongUtils</a> class.
 */
public final class GeoPointUtils {

    /**
     * Maximum possible latitude coordinate.
     */
    public static final double LATITUDE_MAX = 90;

    /**
     * Minimum possible latitude coordinate.
     */
    public static final double LATITUDE_MIN = -LATITUDE_MAX;

    /**
     * Maximum possible longitude coordinate.
     */
    public static final double LONGITUDE_MAX = 180;

    /**
     * Minimum possible longitude coordinate.
     */
    public static final double LONGITUDE_MIN = -LONGITUDE_MAX;

    /**
     * Find if the given point lies within this polygon.
     * <p>
     * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
     *
     * @return true if this polygon contains the given point, false otherwise.
     */
    public static boolean contains(GeoPoint[] geoPoints, GeoPoint geoPoint) {
        boolean result = false;
        for (int i = 0, j = geoPoints.length - 1; i < geoPoints.length; j = i++) {
            if ((geoPoints[i].getLatitude() > geoPoint.getLatitude()) != (geoPoints[j].getLatitude() > geoPoint.getLatitude())
                    && (geoPoint.getLongitude() < (geoPoints[j].getLongitude() - geoPoints[i].getLongitude()) * (geoPoint.getLatitude() - geoPoints[i].getLatitude())
                    / (geoPoints[j].getLatitude() - geoPoints[i].getLatitude()) + geoPoints[i].getLongitude())) {
                result = !result;
            }
        }
        return result;
    }

    /**
     * Returns the distance between the given segment and point.
     * <p>
     * libGDX (Apache 2.0)
     */
    public static double distanceSegmentPoint(double startX, double startY, double endX, double endY, double pointX, double pointY) {
        Point nearest = nearestSegmentPoint(startX, startY, endX, endY, pointX, pointY);
        return Math.hypot(nearest.x - pointX, nearest.y - pointY);
    }

    /**
     * Find if this way is closed.
     *
     * @return true if this way is closed, false otherwise.
     */
    public static boolean isClosedWay(GeoPoint[] geoPoints) {
        return geoPoints[0].distance(geoPoints[geoPoints.length - 1]) < 0.000000001;
    }

    /**
     * Returns a point on the segment nearest to the specified point.
     * <p>
     * libGDX (Apache 2.0)
     */
    public static Point nearestSegmentPoint(double startX, double startY, double endX, double endY, double pointX, double pointY) {
        double xDiff = endX - startX;
        double yDiff = endY - startY;
        double length2 = xDiff * xDiff + yDiff * yDiff;
        if (length2 == 0) return new Point(startX, startY);
        double t = ((pointX - startX) * (endX - startX) + (pointY - startY) * (endY - startY)) / length2;
        if (t < 0) return new Point(startX, startY);
        if (t > 1) return new Point(endX, endY);
        return new Point(startX + t * (endX - startX), startY + t * (endY - startY));
    }

    /**
     * Calculates the scale that allows to display the {@link BoundingBox} on a view with width and
     * height.
     *
     * @param bbox       the {@link BoundingBox} to display.
     * @param viewWidth  the width of the view.
     * @param viewHeight the height of the view.
     * @return the scale that allows to display the {@link BoundingBox} on a view with width and
     * height.
     */
    public static double scaleForBounds(BoundingBox bbox, int viewWidth, int viewHeight) {
        double minx = MercatorProjection.longitudeToX(bbox.getMinLongitude());
        double miny = MercatorProjection.latitudeToY(bbox.getMaxLatitude());

        double dx = Math.abs(MercatorProjection.longitudeToX(bbox.getMaxLongitude()) - minx);
        double dy = Math.abs(MercatorProjection.latitudeToY(bbox.getMinLatitude()) - miny);
        double zx = viewWidth / (dx * Tile.SIZE);
        double zy = viewHeight / (dy * Tile.SIZE);

        return Math.min(zx, zy);
    }

    /**
     * @param latitude the latitude coordinate in degrees which should be validated.
     * @return the latitude value
     * @throws IllegalArgumentException if the latitude coordinate is invalid or {@link Double#NaN}.
     */
    public static double validateLatitude(double latitude) {
        if (Double.isNaN(latitude) || latitude < LATITUDE_MIN || latitude > LATITUDE_MAX) {
            throw new IllegalArgumentException("invalid latitude: " + latitude);
        }
        return latitude;
    }

    /**
     * @param longitude the longitude coordinate in degrees which should be validated.
     * @return the longitude value
     * @throws IllegalArgumentException if the longitude coordinate is invalid or {@link Double#NaN}.
     */
    public static double validateLongitude(double longitude) {
        if (Double.isNaN(longitude) || longitude < LONGITUDE_MIN || longitude > LONGITUDE_MAX) {
            throw new IllegalArgumentException("invalid longitude: " + longitude);
        }
        return longitude;
    }

    private GeoPointUtils() {
        throw new IllegalStateException();
    }
}
