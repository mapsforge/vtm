package org.oscim.layers.vector.geometries;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;
import org.oscim.core.GeoPoint;
import org.oscim.utils.geom.GeomBuilder;

import java.util.List;

public class JtsDrawable implements Drawable {

    public static final PackedCoordinateSequenceFactory coordFactory;
    public static final GeometryFactory geomFactory;

    static {
        coordFactory = new PackedCoordinateSequenceFactory();
        geomFactory = new GeometryFactory(coordFactory);
    }

    protected Style style;
    protected Geometry geometry;
    protected int priority = 0;

    public JtsDrawable(Style style) {
        this.style = style;
    }

    public JtsDrawable(Geometry geometry, Style style) {
        this(geometry, style, 0);
    }

    public JtsDrawable(Geometry geometry, Style style, int priority) {
        this.geometry = geometry;
        this.style = style;
        this.priority = priority;
    }

    /**
     * @param style
     */
    public void setStyle(Style style) {
        this.style = style;
    }

    /* (non-Javadoc)
     *
     * @see org.oscim.core.geometries.Drawable#getStyle() */
    @Override
    public Style getStyle() {
        return style;
    }

    /* (non-Javadoc)
     *
     * @see org.oscim.core.geometries.Drawable#getGeometry() */
    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    protected static GeomBuilder loadPoints(GeomBuilder gb, List<GeoPoint> points) {
        for (GeoPoint point : points) {
            gb.point(point.getLongitude(),
                    point.getLatitude());
        }
        return gb;
    }

}
