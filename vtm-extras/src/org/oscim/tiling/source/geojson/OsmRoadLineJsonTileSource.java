/*
 * Copyright 2014 Hannes Janetzek
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class OsmRoadLineJsonTileSource extends GeoJsonTileSource {

    static final Logger log = LoggerFactory.getLogger(OsmRoadLineJsonTileSource.class);

    Tag mTagTunnel = new Tag("tunnel", Tag.VALUE_YES);
    Tag mTagBridge = new Tag("bridge", Tag.VALUE_YES);

    public OsmRoadLineJsonTileSource() {
        super("http://tile.openstreetmap.us/vectiles-highroad");
    }

    @Override
    public void decodeTags(MapElement mapElement, Map<String, Object> properties) {
        String highway = null;
        boolean isLink = false;

        mapElement.layer = 5;

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            //log.debug(key + " : " + String.valueOf(value));

            if (value == null)
                continue;

            if ("no".equals(value))
                continue;

            if ("highway".equals(key) && value instanceof String) {
                highway = (String) entry.getValue();
            } else if ("is_link".equals(key)) {
                isLink = "yes".equals(value);
            } else if ("is_tunnel".equals(key)) {
                mapElement.tags.add(mTagTunnel);
            } else if ("is_bridge".equals(key)) {
                mapElement.tags.add(mTagBridge);
            } else if ("sort_key".equals(key)) {
                if (value instanceof Integer)
                    mapElement.layer = 5 + (Integer) value;
            } else if ("railway".equals(key) && value instanceof String) {
                mapElement.tags.add(new Tag("railway", (String) value));
            }
        }

        if (highway == null)
            return;

        if (isLink)
            highway += "_link";

        mapElement.tags.add(new Tag("highway", highway));

    }

    @Override
    public Tag rewriteTag(String key, Object value) {
        if ("kind".equals(key))
            return null;

        if (value == null)
            return null;

        String val = (value instanceof String) ? (String) value : String.valueOf(value);

        return new Tag(key, val);
    }
}
