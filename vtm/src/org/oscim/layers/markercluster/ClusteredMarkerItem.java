/*
 * Copyright 2012 osmdroid authors:
 * Copyright 2012 Nicolas Gramlich
 * Copyright 2012 Theodore Hong
 * Copyright 2012 Fred Eisele
 *
 * Copyright 2014 Hannes Janetzek
 * Copyright 2017 Rodolfo Lopez Pintor <rlp@nebular.tv>
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

package org.oscim.layers.markercluster;

import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerSymbol;

/**
 * Based on MarkerItem, but this class is not Immutable anymore to support
 * changing item positions.
 *
 * @author Rodolfo Lopez <rlp@nebular.tv>
 * NOTE: I wonder if the non-immutability breaks something important!!!
 */

public class ClusteredMarkerItem {

    public final Object uid;
    public final String title;
    public final String description;
    private GeoPoint geoPoint;
    protected MarkerSymbol mMarker;

    /** Whether the item is draggable */
    public final boolean isDraggable;

    /** Whether to avoid clustering this item (for important items) */
    public final boolean avoidCluster;

    /** Whether the position of this item has been changed. Will be used by by repopulate() on the layer */
    private boolean hasPositionChanged = false;


    /**
     * Creates a marker item that can be clustered, and cannot be dragged
     *
     * @param title       Marker title
     * @param description Marker Description
     * @param geoPoint    Marker Geo Point
     */

    public ClusteredMarkerItem(String title, String description, GeoPoint geoPoint) {
        this(title, description, geoPoint, false);
    }

    /**
     * Creates a marker item that can be clustered
     *
     * @param title       Marker title
     * @param description Marker Description
     * @param geoPoint    Marker Geo Point
     * @param isDraggable Whether the marker is draggable
     */

    public ClusteredMarkerItem(String title, String description, GeoPoint geoPoint, boolean isDraggable) {
        this(null, title, description, geoPoint, isDraggable, false);
    }

    /**
     * Creates a marker item
     *
     * @param uid          Marker Generic UID
     * @param title        Marker title
     * @param description  Marker Description
     * @param geoPoint     Marker Geo Point
     * @param isDraggable  Whether the marker is draggable
     * @param avoidCluster Whether to avoid clustering this marker
     */

    public ClusteredMarkerItem(Object uid, String title, String description, GeoPoint geoPoint, boolean isDraggable, boolean avoidCluster) {
        this.title = title;
        this.description = description;
        this.geoPoint = geoPoint;
        this.uid = uid;
        this.isDraggable = isDraggable;
        this.avoidCluster = avoidCluster;
    }

    public Object getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return description;
    }

    public GeoPoint getPoint() {
        return geoPoint;
    }

    public MarkerSymbol getMarker() {
        return mMarker;
    }

    /**
     * Changes item position. You need to call renderer.notifyPositionsChanged() for the
     * changes to be visible.
     *
     * @param point New position
     */

    public void setPoint(GeoPoint point) {
        geoPoint = point;
        hasPositionChanged = true;
    }

    /**
     * Checks wether position has changed (by an earlier call to setPoint)
     * and resets the flag.
     *
     * WARNING!!! This is intended to be called ONLY from the
     * cluster repopulation loop, or items will freeze randomly !!
     *
     * @return Wether position has changed
     */

    boolean checkPositionChanged() {
        boolean chg = hasPositionChanged;
        hasPositionChanged = false;
        return chg;
    }

    public void setMarker(MarkerSymbol marker) {
        mMarker = marker;
    }
}
