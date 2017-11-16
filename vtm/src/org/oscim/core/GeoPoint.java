/*
 * Copyright 2010-2017 mapsforge.org
 * Copyright 2012 Hannes Janetzek
 * Copyright 2016 Andrey Novikov
 * Copyright 2016 devemux86
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

import org.oscim.utils.FastMath;

/**
 * A GeoPoint represents an immutable pair of latitude and longitude
 * coordinates.
 * <p>
 * The coordinate validation functions were inspired by the mapsforge LatLongUtils class.
 * <br>
 * @see https://github.com/mapsforge/mapsforge/blob/master/mapsforge-core/src/main/java/org/mapsforge/core/util/LatLongUtils.java
 */
public class GeoPoint implements Comparable<GeoPoint> {
    /**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = 8965378345755560352L;

	/**
     * Conversion factor from degrees to microdegrees.
     */
    private static final double CONVERSION_FACTOR = 1000000d;

    /**
     * The equatorial radius as defined by the <a href="http://en.wikipedia.org/wiki/World_Geodetic_System">WGS84
     * ellipsoid</a>. WGS84 is the reference coordinate system used by the Global Positioning System.
     */
    private static final double EQUATORIAL_RADIUS = 6378137.0;

    /**
     * The flattening factor of the earth's ellipsoid is required for distance computation.
     */
    private static final double INVERSE_FLATTENING = 298.257223563;

    /**
     * Polar radius of earth is required for distance computation.
     */
    private static final double POLAR_RADIUS = 6356752.3142;

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

    /**
     * The hash code of this object.
     */
    private int hashCodeValue = 0;

    /**
     * The latitude value of this GeoPoint in microdegrees (degrees * 10^6).
     */
    public final int latitudeE6;

    /**
     * The longitude value of this GeoPoint in microdegrees (degrees * 10^6).
     */
    public final int longitudeE6;

    /**
     * @param lat the latitude in degrees, will be limited to the possible
     *            latitude range.
     * @param lon the longitude in degrees, will be limited to the possible
     *            longitude range.
     */
    public GeoPoint(double lat, double lon) {
        lat = FastMath.clamp(lat, MercatorProjection.LATITUDE_MIN, MercatorProjection.LATITUDE_MAX);
        this.latitudeE6 = (int) (lat * CONVERSION_FACTOR);
        lon = FastMath.clamp(lon, MercatorProjection.LONGITUDE_MIN, MercatorProjection.LONGITUDE_MAX);
        this.longitudeE6 = (int) (lon * CONVERSION_FACTOR);
    }

    /**
     * @param latitudeE6  the latitude in microdegrees (degrees * 10^6), will be limited
     *                    to the possible latitude range.
     * @param longitudeE6 the longitude in microdegrees (degrees * 10^6), will be
     *                    limited to the possible longitude range.
     */
    public GeoPoint(int latitudeE6, int longitudeE6) {
        this(latitudeE6 / CONVERSION_FACTOR, longitudeE6 / CONVERSION_FACTOR);
    }

    public double bearingTo(GeoPoint other) {
        double deltaLon = Math.toRadians(other.getLongitude() - getLongitude());

        double a1 = Math.toRadians(getLatitude());
        double b1 = Math.toRadians(other.getLatitude());

        double y = Math.sin(deltaLon) * Math.cos(b1);
        double x = Math.cos(a1) * Math.sin(b1) - Math.sin(a1) * Math.cos(b1) * Math.cos(deltaLon);
        double result = Math.toDegrees(Math.atan2(y, x));
        return (result + 360.0) % 360.0;
    }

    /**
     * @return the hash code of this object.
     */
    private int calculateHashCode() {
        int result = 7;
        result = 31 * result + this.latitudeE6;
        result = 31 * result + this.longitudeE6;
        return result;
    }

    @Override
    public int compareTo(GeoPoint geoPoint) {
        if (this.longitudeE6 > geoPoint.longitudeE6) {
            return 1;
        } else if (this.longitudeE6 < geoPoint.longitudeE6) {
            return -1;
        } else if (this.latitudeE6 > geoPoint.latitudeE6) {
            return 1;
        } else if (this.latitudeE6 < geoPoint.latitudeE6) {
            return -1;
        }
        return 0;
    }

    /**
     * Returns the destination point from this point having travelled the given distance on the
     * given initial bearing (bearing normally varies around path followed).
     *
     * @param distance the distance travelled, in same units as earth radius (default: meters)
     * @param bearing  the initial bearing in degrees from north
     * @return the destination point
     * @see <a href="http://www.movable-type.co.uk/scripts/latlon.js">latlon.js</a>
     */
    public GeoPoint destinationPoint(final double distance, final float bearing) {
        double theta = Math.toRadians(bearing);
        double delta = distance / EQUATORIAL_RADIUS; // angular distance in radians

        double phi1 = Math.toRadians(getLatitude());
        double lambda1 = Math.toRadians(getLongitude());

        double phi2 = Math.asin(Math.sin(phi1) * Math.cos(delta)
                + Math.cos(phi1) * Math.sin(delta) * Math.cos(theta));
        double lambda2 = lambda1 + Math.atan2(Math.sin(theta) * Math.sin(delta) * Math.cos(phi1),
                Math.cos(delta) - Math.sin(phi1) * Math.sin(phi2));

        return new GeoPoint(Math.toDegrees(phi2), Math.toDegrees(lambda2));
    }

    /**
     * Calculate the Euclidean distance from this point to another using the Pythagorean theorem.
     *
     * @param other The point to calculate the distance to
     * @return the distance in degrees as a double
     */
    public double distance(GeoPoint other) {
        return Math.hypot(getLongitude() - other.getLongitude(), getLatitude() - other.getLatitude());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof GeoPoint)) {
            return false;
        }
        GeoPoint other = (GeoPoint) obj;
        if (this.latitudeE6 != other.latitudeE6) {
            return false;
        } else if (this.longitudeE6 != other.longitudeE6) {
            return false;
        }
        return true;
    }

    /**
     * @return the latitude value of this GeoPoint in degrees.
     */
    public double getLatitude() {
        return this.latitudeE6 / CONVERSION_FACTOR;
    }

    /**
     * @return the longitude value of this GeoPoint in degrees.
     */
    public double getLongitude() {
        return this.longitudeE6 / CONVERSION_FACTOR;
    }

    @Override
    public int hashCode() {
        if (this.hashCodeValue == 0)
            this.hashCodeValue = calculateHashCode();

        return this.hashCodeValue;
    }

    /**
     * Calculates the amount of degrees of latitude for a given distance in meters.
     *
     * @param meters distance in meters
     * @return latitude degrees
     */
    public static double latitudeDistance(int meters) {
        return (meters * 360) / (2 * Math.PI * EQUATORIAL_RADIUS);
    }

    /**
     * Calculates the amount of degrees of longitude for a given distance in meters.
     *
     * @param meters   distance in meters
     * @param latitude the latitude at which the calculation should be performed
     * @return longitude degrees
     */
    public static double longitudeDistance(int meters, double latitude) {
        return (meters * 360) / (2 * Math.PI * EQUATORIAL_RADIUS * Math.cos(Math.toRadians(latitude)));
    }

    public void project(Point out) {
        out.x = MercatorProjection.longitudeToX(this.longitudeE6 / CONVERSION_FACTOR);
        out.y = MercatorProjection.latitudeToY(this.latitudeE6 / CONVERSION_FACTOR);
    }

    /**
     * Calculate the spherical distance from this point to another using the Haversine formula.
     * <p/>
     * This calculation is done using the assumption, that the earth is a sphere, it is not
     * though. If you need a higher precision and can afford a longer execution time you might
     * want to use vincentyDistance.
     *
     * @param other The point to calculate the distance to
     * @return the distance in meters as a double
     */
    public double sphericalDistance(GeoPoint other) {
        double dLat = Math.toRadians(other.getLatitude() - getLatitude());
        double dLon = Math.toRadians(other.getLongitude() - getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(getLatitude()))
                * Math.cos(Math.toRadians(other.getLatitude())) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return c * EQUATORIAL_RADIUS;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("[lat=")
                .append(this.getLatitude())
                .append(",lon=")
                .append(this.getLongitude())
                .append("]")
                .toString();
    }

    /**
     * Calculate the spherical distance from this point to another using Vincenty inverse formula
     * for ellipsoids. This is very accurate but consumes more resources and time than the
     * sphericalDistance method.
     * <p/>
     * Adaptation of Chriss Veness' JavaScript Code on
     * http://www.movable-type.co.uk/scripts/latlong-vincenty.html
     * <p/>
     * Paper: Vincenty inverse formula - T Vincenty, "Direct and Inverse Solutions of Geodesics
     * on the Ellipsoid with application of nested equations", Survey Review, vol XXII no 176,
     * 1975 (http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf)
     *
     * @param other The point to calculate the distance to
     * @return the distance in meters as a double
     */
    public double vincentyDistance(GeoPoint other) {
        double f = 1 / INVERSE_FLATTENING;
        double L = Math.toRadians(other.getLongitude() - getLongitude());
        double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(getLatitude())));
        double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(other.getLatitude())));
        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

        double lambda = L, lambdaP, iterLimit = 100;

        double cosSqAlpha = 0, sinSigma = 0, cosSigma = 0, cos2SigmaM = 0, sigma = 0, sinLambda = 0, sinAlpha = 0, cosLambda = 0;
        do {
            sinLambda = Math.sin(lambda);
            cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
                    + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
                    * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
            if (sinSigma == 0)
                return 0; // co-incident points
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            if (cosSqAlpha != 0) {
                cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            } else {
                cos2SigmaM = 0;
            }
            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = L
                    + (1 - C)
                    * f
                    * sinAlpha
                    * (sigma + C * sinSigma
                    * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0)
            return 0; // formula failed to converge

        double uSq = cosSqAlpha
                * (Math.pow(EQUATORIAL_RADIUS, 2) - Math.pow(POLAR_RADIUS, 2))
                / Math.pow(POLAR_RADIUS, 2);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma = B
                * sinSigma
                * (cos2SigmaM + B
                / 4
                * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
                * (-3 + 4 * sinSigma * sinSigma)
                * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        double s = POLAR_RADIUS * A * (sigma - deltaSigma);

        return s;
    }
}
