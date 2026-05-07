package org.oscim.layers.vector.geometries;

import org.locationtech.jts.geom.Geometry;
import org.oscim.core.GeoPoint;
import org.oscim.utils.geom.GeomBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Predefined class to draw polygons on the map.
 */
public class PolygonDrawable extends JtsDrawable {

    /**
     * Creates a polygon using a JTS geometry and a FillableGeometryStyle
     *
     * @param polygon
     * @param style
     */
    public PolygonDrawable(Geometry polygon, Style style) {
        super(style);

        if (polygon.getDimension() != 2)
            throw new IllegalArgumentException("Geometry not a Polygon");

        this.geometry = polygon;
    }

    /**
     * Creates a polygon using the coordinates provided in the List
     *
     * @param points
     */
    public PolygonDrawable(List<GeoPoint> points) {
        this(points, Style.defaultStyle());
    }

    /**
     * Create a polygon given the array of GeoPoints and a FillableGeometryStyle
     *
     * @param points
     * @param style
     */
    public PolygonDrawable(Style style, GeoPoint... points) {
        this(Arrays.asList(points), style);
    }

    /**
     * @param points
     * @param style
     */
    public PolygonDrawable(List<GeoPoint> points, Style style) {
        this(loadPoints(new GeomBuilder(), points).toPolygon(), style);
    }

    /**
     * Create a polygon given the array of GeoPoints for the boundary, the array
     * of GeoPoints for the hole and outline and fill color and alpha
     *
     * @param points
     * @param holePoints
     * @param lineWidth
     * @param lineColor
     * @param fillColor
     * @param fillAlpha
     */
    public PolygonDrawable(GeoPoint[] points, GeoPoint[] holePoints, float lineWidth,
                           int lineColor,
                           int fillColor, float fillAlpha) {
        this(Arrays.asList(points),
                Arrays.asList(holePoints),
                lineWidth, lineColor, fillColor, fillAlpha);
    }

    /**
     * Create a polygon using the Coordinates provided in the first List, with a
     * hole build from the Coordinates in the second List and outline and fill -
     * color and alpha
     *
     * @param points
     * @param holePoints
     * @param lineWidth
     * @param lineColor
     * @param fillColor
     * @param fillAlpha
     */
    public PolygonDrawable(List<GeoPoint> points, List<GeoPoint> holePoints,
                           float lineWidth, int lineColor, int fillColor, float fillAlpha) {
        this(points, holePoints, new Style.Builder()
                .strokeWidth(lineWidth)
                .strokeColor(lineColor)
                .fillColor(fillColor)
                .fillAlpha(fillAlpha)
                .build());
    }

    /**
     * Creates a polygon from a List of coordinates in the first List, with a
     * hole from coordinates in the second List and requires a
     * FillableGeometryStyle for the color information
     *
     * @param points
     * @param holePoints
     * @param style
     */
    public PolygonDrawable(List<GeoPoint> points, List<GeoPoint> holePoints, Style style) {
        super(style);
        GeomBuilder gb = new GeomBuilder();
        loadPoints(gb, points).ring();
        loadPoints(gb, holePoints).ring();
        this.geometry = gb.toPolygon();
        this.style = style;
    }
}
