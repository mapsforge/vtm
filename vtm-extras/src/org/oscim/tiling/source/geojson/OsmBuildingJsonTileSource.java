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

import java.util.Map;

public class OsmBuildingJsonTileSource extends GeoJsonTileSource {

    public OsmBuildingJsonTileSource() {
        super("http://tile.openstreetmap.us/vectiles-buildings");
    }

    Tag mTagBuilding = new Tag(Tag.KEY_BUILDING, Tag.VALUE_YES);

    @Override
    public void decodeTags(MapElement mapElement, Map<String, Object> properties) {

        mapElement.tags.add(mTagBuilding);

    }
}
