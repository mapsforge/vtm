/*
 * Copyright 2017 nebular
 * Copyright 2017 devemux86
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
package org.oscim.android.cluster;

import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerItem;

/**
 * If a MarkerItem is created using this convenience class instead of MarkerItem,
 * this specific item will not be clusterable.
 */
public class NonClusterable extends MarkerItem {
    public NonClusterable(String title, String description, GeoPoint geoPoint) {
        super(null, title, description, geoPoint);
    }

    public NonClusterable(Object uid, String title, String description, GeoPoint geoPoint) {
        super(uid, title, description, geoPoint);
    }
}
