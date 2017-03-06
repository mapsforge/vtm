/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 Izumi Kawashima
 * Copyright 2017 Longri
 * Copyright 2017 devemux86
 * Copyright 2017 nebular
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

/**
 * The internal representation of a marker.
 */
class InternalItem {

    MarkerInterface item;
    boolean visible;
    boolean changes;
    float x, y;
    double px, py;
    float dy;

    @Override
    public String toString() {
        return "\n" + x + ":" + y + " / " + dy + " " + visible;
    }

    /**
     * Extension to the above class for clustered items. This could be a separate 1st level class,
     * but it is included here not to pollute the source tree with tiny new files.
     * It only adds a couple properties to InternalItem, and the semantics "InternalItem.Clustered"
     * are not bad.
     */
    static class Clustered extends InternalItem {
        /**
         * If this is >0, this item will be displayed as a cluster circle, with size clusterSize+1.
         */
        int clusterSize;

        /**
         * If this is true, this item is hidden (because it's represented by another InternalItem acting as cluster.
         */
        boolean clusteredOut;
    }
}
