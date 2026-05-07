package org.oscim.layers.vector.geometries;

import org.oscim.core.GeoPoint;
import org.oscim.utils.geom.GeomBuilder;

/**
 * Predefined class to draw rectangles on the map
 */
public class RectangleDrawable extends JtsDrawable {

    /**
     * Creates a Rectangle given the top-left and the bottom-right coordinate of
     * it
     *
     * @param topLeft
     * @param bottomRight
     */
    public RectangleDrawable(GeoPoint topLeft, GeoPoint bottomRight) {
        this(topLeft, bottomRight, Style.defaultStyle());
    }

    /**
     * Creates a Rectangle given the top-left and the bottom-right coordinate of
     * it
     *
     * @param topLeft
     * @param bottomRight
     */
    public RectangleDrawable(GeoPoint topLeft, GeoPoint bottomRight, Style style) {
        super(style);
        geometry = new GeomBuilder()
                .point(topLeft.getLongitude(), topLeft.getLatitude())
                .point(bottomRight.getLongitude(), topLeft.getLatitude())
                .point(bottomRight.getLongitude(), bottomRight.getLatitude())
                .point(topLeft.getLongitude(), bottomRight.getLatitude())
                .toPolygon();
    }

    /**
     * Creates a Rectangle given the top-left and the bottom-right coordinate of
     * it
     *
     * @param minLat
     * @param minLon
     * @param maxLat
     * @param maxLon
     * @param style
     */
    public RectangleDrawable(double minLat, double minLon, double maxLat, double maxLon, Style style) {
        super(style);
        geometry = new GeomBuilder()
                .point(minLon, minLat)
                .point(minLon, maxLat)
                .point(maxLon, maxLat)
                .point(maxLon, minLat)
                .toPolygon();
    }
}
