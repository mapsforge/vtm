/*
 * Copyright 2012 osmdroid authors:
 * Copyright 2012 Nicolas Gramlich
 * Copyright 2012 Theodore Hong
 * Copyright 2012 Fred Eisele
 *
 * Copyright 2014 Hannes Janetzek
 * Copyright 2016 devemux86
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
package org.oscim.layers.marker;

import org.oscim.core.GeoPoint;

/**
 * Immutable class describing a GeoPoint with a Title and a Description.
 */
public interface MarkerItem {
    /**
     * Indicates a hotspot for an area. This is where the origin (0,0) of a
     * point will be located relative to the area. In otherwords this acts as an
     * offset. NONE indicates that no adjustment should be made.
     */
    enum HotspotPlace {
        NONE, CENTER, BOTTOM_CENTER,
        TOP_CENTER, RIGHT_CENTER, LEFT_CENTER,
        UPPER_RIGHT_CORNER, LOWER_RIGHT_CORNER,
        UPPER_LEFT_CORNER, LOWER_LEFT_CORNER
    }

    GeoPoint getPoint();

    MarkerSymbol getMarker();
}
