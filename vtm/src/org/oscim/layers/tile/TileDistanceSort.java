/*
 * Copyright 2013 Hannes Janetzek
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
package org.oscim.layers.tile;

import org.oscim.utils.TimSort;

import java.util.Comparator;

public class TileDistanceSort extends TimSort<MapTile> {

    static TileDistanceSort INSTANCE = new TileDistanceSort();

    private TileDistanceSort() {
        super();
    }

    /**
     * Sort tiles by it's current {@link MapTile#distance} from map center.
     *
     * @param a  the tiles to be sorted.
     * @param lo the start index.
     * @param hi the end index (excluded).
     */
    public static void sort(MapTile[] a, int lo, int hi) {
        int nRemaining = hi - lo;
        if (nRemaining < 2) {
            return;
        }

        synchronized (INSTANCE) {
            INSTANCE.doSort(a, DistanceComparator, lo, hi);
        }
    }

    static final Comparator<MapTile> DistanceComparator = new Comparator<MapTile>() {
        @Override
        public int compare(MapTile a, MapTile b) {
            if (a == null) {
                if (b == null)
                    return 0;

                return 1;
            }
            if (b == null)
                return -1;

            if (a.distance < b.distance) {
                return -1;
            }
            if (a.distance > b.distance) {
                return 1;
            }
            return 0;
        }
    };
}
