/*
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
package org.oscim.tiling.source.geojson;

import org.oscim.core.MapElement;
import org.oscim.core.Tag;

import java.util.Map;

public class OsmRoadLabelJsonTileSource extends GeoJsonTileSource {

    public OsmRoadLabelJsonTileSource() {
        super("http://tile.openstreetmap.us/vectiles-skeletron");
    }

    @Override
    public void decodeTags(MapElement mapElement, Map<String, Object> properties) {
        String highway = null;

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            //log.debug(key + " : " + String.valueOf(value));

            if (value == null)
                continue;

            if ("highway".equals(key) && value instanceof String) {
                highway = (String) entry.getValue();
            } else if ("name".equals(key) && value instanceof String) {
                mapElement.tags.add(new Tag("name", (String) value));
            }
        }

        if (highway == null)
            return;

        mapElement.tags.add(new Tag("highway", highway));
    }
}
