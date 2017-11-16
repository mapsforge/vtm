/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2014 Hannes Janetzek
 * Copyright 2016-2017 devemux86
 * Copyright 2017 Luca Osten
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

import java.util.List;

/**
 * A BoundingBox represents an immutable set of two latitude and two longitude
 * coordinates.
 */
public class BoundingBox {
    /**
     * Conversion factor from degrees to microdegrees.
     */
    private static final double CONVERSION_FACTOR = 1000000d;

    /**
     * The maximum latitude value of this BoundingBox in microdegrees (degrees *
     * 10^6).
     */
    public int maxLatitudeE6;

    /**
     * The maximum longitude value of this BoundingBox in microdegrees (degrees
     * * 10^6).
     */
    public int maxLongitudeE6;

    /**
     * The minimum latitude value of this BoundingBox in microdegrees (degrees *
     * 10^6).
     */
    public int minLatitudeE6;

    /**
     * The minimum longitude value of this BoundingBox in microdegrees (degrees
     * * 10^6).
     */
    public int minLongitudeE6;

    /**
     * @param minLatitudeE6  the minimum latitude in microdegrees (degrees * 10^6).
     * @param minLongitudeE6 the minimum longitude in microdegrees (degrees * 10^6).
     * @param maxLatitudeE6  the maximum latitude in microdegrees (degrees * 10^6).
     * @param maxLongitudeE6 the maximum longitude in microdegrees (degrees * 10^6).
     */
    public BoundingBox(int minLatitudeE6, int minLongitudeE6, int maxLatitudeE6, int maxLongitudeE6) {
        this.minLatitudeE6 = minLatitudeE6;
        this.minLongitudeE6 = minLongitudeE6;
        this.maxLatitudeE6 = maxLatitudeE6;
        this.maxLongitudeE6 = maxLongitudeE6;
    }

    /**
     * @param minLatitude  the minimum latitude coordinate in degrees.
     * @param minLongitude the minimum longitude coordinate in degrees.
     * @param maxLatitude  the maximum latitude coordinate in degrees.
     * @param maxLongitude the maximum longitude coordinate in degrees.
     */
    public BoundingBox(double minLatitude, double minLongitude, double maxLatitude, double maxLongitude) {
        this.minLatitudeE6 = (int) (minLatitude * 1E6);
        this.minLongitudeE6 = (int) (minLongitude * 1E6);
        this.maxLatitudeE6 = (int) (maxLatitude * 1E6);
        this.maxLongitudeE6 = (int) (maxLongitude * 1E6);
    }

    /**
     * @param geoPoints the coordinates list.
     */
    public BoundingBox(List<GeoPoint> geoPoints) {
        int minLat = Integer.MAX_VALUE;
        int minLon = Integer.MAX_VALUE;
        int maxLat = Integer.MIN_VALUE;
        int maxLon = Integer.MIN_VALUE;
        for (GeoPoint geoPoint : geoPoints) {
            minLat = Math.min(minLat, geoPoint.latitudeE6);
            minLon = Math.min(minLon, geoPoint.longitudeE6);
            maxLat = Math.max(maxLat, geoPoint.latitudeE6);
            maxLon = Math.max(maxLon, geoPoint.longitudeE6);
        }

        this.minLatitudeE6 = minLat;
        this.minLongitudeE6 = minLon;
        this.maxLatitudeE6 = maxLat;
        this.maxLongitudeE6 = maxLon;
    }

    /**
     * @param geoPoint the point whose coordinates should be checked.
     * @return true if this BoundingBox contains the given GeoPoint, false
     * otherwise.
     */
    public boolean contains(GeoPoint geoPoint) {
        return geoPoint.latitudeE6 <= maxLatitudeE6
                && geoPoint.latitudeE6 >= minLatitudeE6
                && geoPoint.longitudeE6 <= maxLongitudeE6
                && geoPoint.longitudeE6 >= minLongitudeE6;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof BoundingBox)) {
            return false;
        }
        BoundingBox other = (BoundingBox) obj;
        if (maxLatitudeE6 != other.maxLatitudeE6) {
            return false;
        } else if (maxLongitudeE6 != other.maxLongitudeE6) {
            return false;
        } else if (minLatitudeE6 != other.minLatitudeE6) {
            return false;
        } else if (minLongitudeE6 != other.minLongitudeE6) {
            return false;
        }
        return true;
    }

    /**
     * @param boundingBox the BoundingBox which this BoundingBox should be extended if it is larger
     * @return a BoundingBox that covers this BoundingBox and the given BoundingBox.
     */
    public BoundingBox extendBoundingBox(BoundingBox boundingBox) {
        return new BoundingBox(Math.min(this.minLatitudeE6, boundingBox.minLatitudeE6),
                Math.min(this.minLongitudeE6, boundingBox.minLongitudeE6),
                Math.max(this.maxLatitudeE6, boundingBox.maxLatitudeE6),
                Math.max(this.maxLongitudeE6, boundingBox.maxLongitudeE6));
    }

    /**
     * Creates a BoundingBox extended up to <code>GeoPoint</code> (but does not cross date line/poles).
     *
     * @param geoPoint coordinates up to the extension
     * @return an extended BoundingBox or this (if contains coordinates)
     */
    public BoundingBox extendCoordinates(GeoPoint geoPoint) {
        if (contains(geoPoint)) {
            return this;
        }

        double minLat = Math.max(MercatorProjection.LATITUDE_MIN, Math.min(getMinLatitude(), geoPoint.getLatitude()));
        double minLon = Math.max(MercatorProjection.LONGITUDE_MIN, Math.min(getMinLongitude(), geoPoint.getLongitude()));
        double maxLat = Math.min(MercatorProjection.LATITUDE_MAX, Math.max(getMaxLatitude(), geoPoint.getLatitude()));
        double maxLon = Math.min(MercatorProjection.LONGITUDE_MAX, Math.max(getMaxLongitude(), geoPoint.getLongitude()));

        return new BoundingBox(minLat, minLon, maxLat, maxLon);
    }

    /**
     * Creates a BoundingBox that is a fixed degree amount larger on all sides (but does not cross date line/poles).
     *
     * @param verticalExpansion   degree extension (must be >= 0)
     * @param horizontalExpansion degree extension (must be >= 0)
     * @return an extended BoundingBox or this (if degrees == 0)
     */
    public BoundingBox extendDegrees(double verticalExpansion, double horizontalExpansion) {
        if (verticalExpansion == 0 && horizontalExpansion == 0) {
            return this;
        } else if (verticalExpansion < 0 || horizontalExpansion < 0) {
            throw new IllegalArgumentException("BoundingBox extend operation does not accept negative values");
        }

        double minLat = Math.max(MercatorProjection.LATITUDE_MIN, getMinLatitude() - verticalExpansion);
        double minLon = Math.max(MercatorProjection.LONGITUDE_MIN, getMinLongitude() - horizontalExpansion);
        double maxLat = Math.min(MercatorProjection.LATITUDE_MAX, getMaxLatitude() + verticalExpansion);
        double maxLon = Math.min(MercatorProjection.LONGITUDE_MAX, getMaxLongitude() + horizontalExpansion);

        return new BoundingBox(minLat, minLon, maxLat, maxLon);
    }

    /**
     * Creates a BoundingBox that is a fixed margin factor larger on all sides (but does not cross date line/poles).
     *
     * @param margin extension (must be > 0)
     * @return an extended BoundingBox or this (if margin == 1)
     */
    public BoundingBox extendMargin(float margin) {
        if (margin == 1) {
            return this;
        } else if (margin <= 0) {
            throw new IllegalArgumentException("BoundingBox extend operation does not accept negative or zero values");
        }

        double verticalExpansion = (getLatitudeSpan() * margin - getLatitudeSpan()) * 0.5;
        double horizontalExpansion = (getLongitudeSpan() * margin - getLongitudeSpan()) * 0.5;

        double minLat = Math.max(MercatorProjection.LATITUDE_MIN, getMinLatitude() - verticalExpansion);
        double minLon = Math.max(MercatorProjection.LONGITUDE_MIN, getMinLongitude() - horizontalExpansion);
        double maxLat = Math.min(MercatorProjection.LATITUDE_MAX, getMaxLatitude() + verticalExpansion);
        double maxLon = Math.min(MercatorProjection.LONGITUDE_MAX, getMaxLongitude() + horizontalExpansion);

        return new BoundingBox(minLat, minLon, maxLat, maxLon);
    }

    /**
     * Creates a BoundingBox that is a fixed meter amount larger on all sides (but does not cross date line/poles).
     *
     * @param meters extension (must be >= 0)
     * @return an extended BoundingBox or this (if meters == 0)
     */
    public BoundingBox extendMeters(int meters) {
        if (meters == 0) {
            return this;
        } else if (meters < 0) {
            throw new IllegalArgumentException("BoundingBox extend operation does not accept negative values");
        }

        double verticalExpansion = GeoPoint.latitudeDistance(meters);
        double horizontalExpansion = GeoPoint.longitudeDistance(meters, Math.max(Math.abs(getMinLatitude()), Math.abs(getMaxLatitude())));

        double minLat = Math.max(MercatorProjection.LATITUDE_MIN, getMinLatitude() - verticalExpansion);
        double minLon = Math.max(MercatorProjection.LONGITUDE_MIN, getMinLongitude() - horizontalExpansion);
        double maxLat = Math.min(MercatorProjection.LATITUDE_MAX, getMaxLatitude() + verticalExpansion);
        double maxLon = Math.min(MercatorProjection.LONGITUDE_MAX, getMaxLongitude() + horizontalExpansion);

        return new BoundingBox(minLat, minLon, maxLat, maxLon);
    }

    public String format() {
        return new StringBuilder()
                .append(minLatitudeE6 / CONVERSION_FACTOR)
                .append(',')
                .append(minLongitudeE6 / CONVERSION_FACTOR)
                .append(',')
                .append(maxLatitudeE6 / CONVERSION_FACTOR)
                .append(',')
                .append(maxLongitudeE6 / CONVERSION_FACTOR)
                .toString();
    }

    /**
     * @return the GeoPoint at the horizontal and vertical center of this
     * BoundingBox.
     */
    public GeoPoint getCenterPoint() {
        int latitudeOffset = (maxLatitudeE6 - minLatitudeE6) / 2;
        int longitudeOffset = (maxLongitudeE6 - minLongitudeE6) / 2;
        return new GeoPoint(minLatitudeE6 + latitudeOffset, minLongitudeE6
                + longitudeOffset);
    }

    /**
     * @return the latitude span of this BoundingBox in degrees.
     */
    public double getLatitudeSpan() {
        return getMaxLatitude() - getMinLatitude();
    }

    /**
     * @return the longitude span of this BoundingBox in degrees.
     */
    public double getLongitudeSpan() {
        return getMaxLongitude() - getMinLongitude();
    }

    /**
     * @return the maximum latitude value of this BoundingBox in degrees.
     */
    public double getMaxLatitude() {
        return maxLatitudeE6 / CONVERSION_FACTOR;
    }

    /**
     * @return the maximum longitude value of this BoundingBox in degrees.
     */
    public double getMaxLongitude() {
        return maxLongitudeE6 / CONVERSION_FACTOR;
    }

    /**
     * @return the minimum latitude value of this BoundingBox in degrees.
     */
    public double getMinLatitude() {
        return minLatitudeE6 / CONVERSION_FACTOR;
    }

    /**
     * @return the minimum longitude value of this BoundingBox in degrees.
     */
    public double getMinLongitude() {
        return minLongitudeE6 / CONVERSION_FACTOR;
    }

    @Override
    public int hashCode() {
        int result = 7;
        result = 31 * result + maxLatitudeE6;
        result = 31 * result + maxLongitudeE6;
        result = 31 * result + minLatitudeE6;
        result = 31 * result + minLongitudeE6;
        return result;
    }

    /**
     * @param boundingBox the BoundingBox which should be checked for intersection with this BoundingBox.
     * @return true if this BoundingBox intersects with the given BoundingBox, false otherwise.
     */
    public boolean intersects(BoundingBox boundingBox) {
        if (this == boundingBox) {
            return true;
        }

        return getMaxLatitude() >= boundingBox.getMinLatitude() && getMaxLongitude() >= boundingBox.getMinLongitude()
                && getMinLatitude() <= boundingBox.getMaxLatitude() && getMinLongitude() <= boundingBox.getMaxLongitude();
    }

    /**
     * Returns if an area built from the geoPoints intersects with a bias towards
     * returning true.
     * The method returns fast if any of the points lie within the bbox. If none of the points
     * lie inside the box, it constructs the outer bbox for all the points and tests for intersection
     * (so it is possible that the area defined by the points does not actually intersect)
     *
     * @param geoPoints the points that define an area
     * @return false if there is no intersection, true if there could be an intersection
     */
    public boolean intersectsArea(GeoPoint[][] geoPoints) {
        if (geoPoints.length == 0 || geoPoints[0].length == 0) {
            return false;
        }
        for (GeoPoint[] outer : geoPoints) {
            for (GeoPoint geoPoint : outer) {
                if (this.contains(geoPoint)) {
                    // if any of the points is inside the bbox return early
                    return true;
                }
            }
        }

        // no fast solution, so accumulate boundary points
        double tmpMinLat = geoPoints[0][0].getLatitude();
        double tmpMinLon = geoPoints[0][0].getLongitude();
        double tmpMaxLat = geoPoints[0][0].getLatitude();
        double tmpMaxLon = geoPoints[0][0].getLongitude();

        for (GeoPoint[] outer : geoPoints) {
            for (GeoPoint geoPoint : outer) {
                tmpMinLat = Math.min(tmpMinLat, geoPoint.getLatitude());
                tmpMaxLat = Math.max(tmpMaxLat, geoPoint.getLatitude());
                tmpMinLon = Math.min(tmpMinLon, geoPoint.getLongitude());
                tmpMaxLon = Math.max(tmpMaxLon, geoPoint.getLongitude());
            }
        }
        return this.intersects(new BoundingBox(tmpMinLat, tmpMinLon, tmpMaxLat, tmpMaxLon));
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("BoundingBox [minLat=")
                .append(getMinLatitude())
                .append(", minLon=")
                .append(getMinLongitude())
                .append(", maxLat=")
                .append(getMaxLatitude())
                .append(", maxLon=")
                .append(getMaxLongitude())
                .append("]")
                .toString();
    }
}
