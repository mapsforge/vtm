package org.oscim.layers.vector.geometries;

import org.locationtech.jts.geom.Geometry;

public interface Drawable {

    /**
     * @return
     */
    public Style getStyle();

    /**
     * @return
     */
    public Geometry getGeometry();

    /**
     * priority for drawable, The larger the value, the higher it will appear when drawn in the Vectorlayer
     * @see org.oscim.layers.vector.VectorLayer draw() method
     * */
    public int getPriority();
}
