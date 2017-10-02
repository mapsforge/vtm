/*
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

import org.oscim.core.GeoPoint;
import org.oscim.core.Point;

public final class GeoPointUtils {

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

    private GeoPointUtils() {
        throw new IllegalStateException();
    }
}
