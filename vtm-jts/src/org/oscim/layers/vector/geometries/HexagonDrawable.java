package org.oscim.layers.vector.geometries;

import org.oscim.core.GeoPoint;
import org.oscim.utils.geom.GeomBuilder;

/**
 * Predefined class for drawing hexagons on the map.
 */
public class HexagonDrawable extends JtsDrawable {

    /**
     * @param center   GeoPoint - center of the hexagon
     * @param radiusKm Radius of the hexagon in kilometers. The size of the
     *                 hexagon may be distorted due to the Mercator projections
     *                 properties.
     */
    public HexagonDrawable(GeoPoint center, double radiusKm) {
        super(Style.DEFAULT_STYLE);
        GeomBuilder gb = new GeomBuilder();

        for (int i = 0; i < 6; i++) {
            GeoPoint point = findGeoPointWithGivenDistance(center, i * Math.PI / 3, radiusKm);
            gb.points(point.getLongitude(), point.getLatitude());
        }
        geometry = gb.toPolygon();
    }

    /**
     * @param center      GeoPoint - center of the hexagon
     * @param radiusKm    Radius of the hexagon in kilometers. The size of the
     *                    hexagon may be distorted due to the Mercator projections
     *                    properties.
     * @param rotationRad rotation of the hexagon in radians
     * @param style
     */
    public HexagonDrawable(GeoPoint center, double radiusKm, double rotationRad, Style style) {
        super(style);
        GeomBuilder gb = new GeomBuilder();

        for (int i = 0; i < 6; i++) {
            GeoPoint point = findGeoPointWithGivenDistance(center,
                    rotationRad + i * Math.PI / 3,
                    radiusKm);
            gb.points(point.getLongitude(), point.getLatitude());
        }
        geometry = gb.toPolygon();
    }

    /**
     * This function finds a GeoPoint offset by a distance in the direction
     * given in the bearing parameter. It is an approximation due to the
     * Mercator projections properties
     *
     * @param startPoint
     * @param initialBearingRadians
     * @param distanceKilometres
     * @return a new GeoPoint located distanceKilometers away from the
     * startPoint in the direction of the initialBearing
     */
    private static GeoPoint findGeoPointWithGivenDistance(GeoPoint startPoint,
                                                          double initialBearingRadians, double distanceKilometres) {
        double radiusEarthKilometres = 6371.01;
        double distRatio = distanceKilometres / radiusEarthKilometres;
        double distRatioSine = Math.sin(distRatio);
        double distRatioCosine = Math.cos(distRatio);

        double startLatRad = Math.toRadians(startPoint.getLatitude());
        double startLonRad = Math.toRadians(startPoint.getLongitude());

        double startLatCos = Math.cos(startLatRad);
        double startLatSin = Math.sin(startLatRad);

        double endLatRads = Math.asin((startLatSin * distRatioCosine)
                + (startLatCos * distRatioSine * Math.cos(initialBearingRadians)));

        double endLonRads = startLonRad
                + Math.atan2(
                Math.sin(initialBearingRadians) * distRatioSine * startLatCos,
                distRatioCosine - startLatSin * Math.sin(endLatRads));

        return new GeoPoint(Math.toDegrees(endLatRads), Math.toDegrees(endLonRads));
    }
}
