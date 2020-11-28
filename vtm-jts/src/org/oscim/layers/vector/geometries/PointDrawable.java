package org.oscim.layers.vector.geometries;

import org.oscim.core.GeoPoint;
import org.oscim.utils.geom.GeomBuilder;

/**
 * Use this class to draw points and circles which size has to be specified in
 * screen units. Points (and circles resulting from this class) do not have
 * area,
 * however during rendering to make the point visible at varying zoom levels
 * a buffer area is built around it.
 * To give the point custom size, create a GeometryBuffer with, set buffer to
 * your value and assign the point the final style.
 * <p/>
 * Note that since points do not have any area, they are not generalized.
 * <p/>
 * Normally points retain their size in the screen units across all zoom levels
 * but this can be customized. Use setStartLevel on the point's style to specify
 * from which zoom level the point should "stick to the map" and not decrease in
 * size.
 */
public class PointDrawable extends JtsDrawable {

    public PointDrawable(GeoPoint point) {
        this(point, Style.defaultStyle());
    }

    public PointDrawable(GeoPoint point, Style style) {
        this(point.getLatitude(), point.getLongitude(), style);
    }

    public PointDrawable(double lat, double lon, Style style) {
        super(style);
        this.geometry = new GeomBuilder().points(lon, lat).toPoint();
    }
}
