/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 * Copyright 2015-2020 devemux86
 * Copyright 2015-2016 lincomatic
 * Copyright 2020 Meibes
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
package org.oscim.tiling.source.mapfile;

import org.oscim.core.MapElement;
import org.oscim.core.Tag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class OSMUtils {

    private static final Set<String> areaKeys = new HashSet<>(Arrays.asList(
            "area",
            "aeroway", "building", "landuse", "leisure", "natural", "amenity",
            "highway", "barrier",
            "railway"
    ));

    /**
     * Heuristic to determine from attributes if a map element is likely to be an area.
     * Precondition for this call is that the first and last node of a map element are the
     * same, so that this method should only return false if it is known that the
     * feature should not be an area even if the geometry is a polygon.
     * <p/>
     * Determining what is an area is neigh impossible in OSM, this method inspects tag elements
     * to give a likely answer. See http://wiki.openstreetmap.org/wiki/The_Future_of_Areas and
     * http://wiki.openstreetmap.org/wiki/Way
     * <p>
     * The order in which the if-clauses are checked is determined with the help from
     * https://taginfo.openstreetmap.org/ - last accessed 2020-12-23
     * <p>
     * key statistics:
     * 429 422 565 building
     * 188 142 457 highway
     * 44 369 957 natural
     * 31 354 174 landuse
     * 17 740 976 amenity
     * 14 581 922 barrier
     * 6 366 976 leisure
     * 5 361 889 railway
     * 1 467 189 area
     * 550 597 aeroway
     * <p>
     * railway statistics:
     * 2 138 877 rail
     * 93 926 tram
     * 60 396 subway
     * 45 182 narrow_gauge
     * 30 668 light_rail
     * 24 203 construction
     * 8 937 preserved
     * 3 138 monorail
     *
     * @param mapElement the map element (which is assumed to be closed and have enough nodes to be an area)
     * @return true if tags indicate this is an area, otherwise false.
     */
    public static boolean isArea(MapElement mapElement) {
        boolean result = true;
        for (int i = 0; i < mapElement.tags.size(); i++) {
            Tag tag = mapElement.tags.get(i);
            String key = tag.key.toLowerCase(Locale.ENGLISH);
            if (!areaKeys.contains(key)) {
                continue;
            }
            if ("building".equals(key) || "natural".equals(key) || "landuse".equals(key) || "amenity".equals(key) || "leisure".equals(key) || "aeroway".equals(key)) {
                // as specified by http://wiki.openstreetmap.org/wiki/Key:area
                return true;
            } else if ("highway".equals(key) || "barrier".equals(key)) {
                // false unless something else overrides this.
                result = false;
            } else if ("railway".equals(key)) {
                String value = tag.value.toLowerCase(Locale.ENGLISH);
                // there is more to the railway tag then just rails, this excludes the
                // most common railway lines from being detected as areas if they are closed.
                // Since this method is only called if the first and last node are the same
                // this should be safe
                if ("rail".equals(value) || "tram".equals(value) || "subway".equals(value)
                        || "narrow_gauge".equals(value) || "light_rail".equals(value)
                        || "construction".equals(value) || "preserved".equals(value)
                        || "monorail".equals(value)) {
                    result = false;
                }
            } else if ("area".equals(key)) {
                String value = tag.value.toLowerCase(Locale.ENGLISH);
                if (("yes").equals(value) || ("y").equals(value) || ("true").equals(value)) {
                    return true;
                }
                if (("no").equals(value) || ("n").equals(value) || ("false").equals(value)) {
                    return false;
                }
            }
        }
        return result;
    }

    private OSMUtils() {
    }
}
