/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2012 Hannes Janetzek
 * Copyright 2016 Andrey Novikov
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
 */
public class GeoPoint implements Comparable<GeoPoint> {
    /**
     * Conversion factor from degrees to microdegrees.
     */
    private static final double CONVERSION_FACTOR = 1000000d;

    /**
     * The latitude value of this GeoPoint in microdegrees (degrees * 10^6).
     */
    public final int latitudeE6;

    /**
     * The longitude value of this GeoPoint in microdegrees (degrees * 10^6).
     */
    public final int longitudeE6;

    /**
     * The hash code of this object.
     */
    private int hashCodeValue = 0;

    /**
     * @param lat the latitude in degrees, will be limited to the possible
     *            latitude range.
     * @param lon the longitude in degrees, will be limited to the possible
     *            longitude range.
     */
    public GeoPoint(double lat, double lon) {
        lat = FastMath.clamp(lat,
                MercatorProjection.LATITUDE_MIN,
                MercatorProjection.LATITUDE_MAX);
        this.latitudeE6 = (int) (lat * CONVERSION_FACTOR);
        lon = FastMath.clamp(lon,
                MercatorProjection.LONGITUDE_MIN,
                MercatorProjection.LONGITUDE_MAX);
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

    public void project(Point out) {
        out.x = MercatorProjection.longitudeToX(this.longitudeE6 / CONVERSION_FACTOR);
        out.y = MercatorProjection.latitudeToY(this.latitudeE6 / CONVERSION_FACTOR);
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
     * @return the hash code of this object.
     */
    private int calculateHashCode() {
        int result = 7;
        result = 31 * result + this.latitudeE6;
        result = 31 * result + this.longitudeE6;
        return result;
    }

    public static final float DEG2RAD = (float) (Math.PI / 180.0);
    public static final float RAD2DEG = (float) (180.0 / Math.PI);

    /**
     * Calculates the distance between two points on the surface of a spheroid.
     *
     * @param other point to calculate distance to
     * @return distance in meters
     */
    public double distanceTo(GeoPoint other) {
        return distance(latitudeE6 / 1E6,
                longitudeE6 / 1E6,
                other.latitudeE6 / 1E6,
                other.longitudeE6 / 1E6);
    }

    /**
     * Calculates the distance between two points on the surface of a spheroid using Vincenty's
     * inverse formula.
     *
     * @param lat1 latitude of point 1
     * @param lon1 longitude of point 1
     * @param lat2 latitude of point 2
     * @param lon2 longitude of point 2
     * @return distance in meters
     * @see "https://en.wikipedia.org/wiki/Vincenty%27s_formulae"
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        // WGS-84 ellipsoid
        double a = 6378137;
        double b = 6356752.314245;
        double f = 1 / 298.257223563;
        double L = DEG2RAD * (lon2 - lon1);
        double U1 = Math.atan((1 - f) * Math.tan(DEG2RAD * lat1));
        double U2 = Math.atan((1 - f) * Math.tan(DEG2RAD * lat2));
        double sinU1 = Math.sin(U1);
        double cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2);
        double cosU2 = Math.cos(U2);

        double lambda = L, lambdaP, iterLimit = 100;
        double sigma, cosSqAlpha, sinSigma, cosSigma, cos2SigmaM;

        do {
            double sinLambda = Math.sin(lambda);
            double cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
                    + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
                    * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
            if (sinSigma == 0) return 0;  // co-incident points
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            try {
                cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            } catch (ArithmeticException e) {
                cos2SigmaM = 0;  // equatorial line: cosSqAlpha=0
            }
            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = L + (1 - C) * f * sinAlpha * (sigma + C * sinSigma
                    * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        }
        while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0) return Double.NaN;  // formula failed to converge

        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4
                * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
                * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        return b * A * (sigma - deltaSigma);
    }

    public double bearingTo(GeoPoint other) {
        return bearing(latitudeE6 / 1E6,
                longitudeE6 / 1E6,
                other.latitudeE6 / 1E6,
                other.longitudeE6 / 1E6);
    }

    public static double bearing(double lat1, double lon1, double lat2, double lon2) {
        double deltaLon = DEG2RAD * (lon2 - lon1);

        double a1 = DEG2RAD * lat1;
        double b1 = DEG2RAD * lat2;

        double y = Math.sin(deltaLon) * Math.cos(b1);
        double x = Math.cos(a1) * Math.sin(b1) - Math.sin(a1) * Math.cos(b1) * Math.cos(deltaLon);
        double result = RAD2DEG * Math.atan2(y, x);
        return (result + 360.0) % 360.0;
    }
}
