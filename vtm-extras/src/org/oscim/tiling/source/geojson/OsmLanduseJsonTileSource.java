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

import org.oscim.core.GeometryBuffer.GeometryType;
import org.oscim.core.MapElement;
import org.oscim.core.Tag;

import java.util.LinkedHashMap;
import java.util.Map;

public class OsmLanduseJsonTileSource extends GeoJsonTileSource {

    public OsmLanduseJsonTileSource() {
        super("http://tile.openstreetmap.us/vectiles-land-usages");
    }

    private static LinkedHashMap<String, Tag> mappings =
            new LinkedHashMap<String, Tag>();

    static void addMapping(String key, String val) {
        mappings.put(val, new Tag(key, val));
    }

    static {
        addMapping("landuse", "residential");
        addMapping("landuse", "commercial");
        addMapping("landuse", "retail");
        addMapping("landuse", "railway");
        addMapping("landuse", "grass");
        addMapping("landuse", "meadow");
        addMapping("landuse", "forest");
        addMapping("landuse", "farm");
        addMapping("landuse", "allotments");
        addMapping("landuse", "cemetery");
        addMapping("landuse", "farmyard");
        addMapping("landuse", "farmland");
        addMapping("landuse", "quarry");
        addMapping("landuse", "military");
        addMapping("landuse", "industrial");
        addMapping("landuse", "greenfield");
        addMapping("landuse", "village_green");
        addMapping("landuse", "recreation_ground");
        addMapping("landuse", "conservation");
        addMapping("landuse", "landfill");
        addMapping("landuse", "construction");

        addMapping("leisure", "common");
        addMapping("leisure", "park");
        addMapping("leisure", "pitch");
        addMapping("leisure", "garden");
        addMapping("leisure", "sports_centre");
        addMapping("leisure", "playground");
        addMapping("leisure", "nature_reserve");
        addMapping("leisure", "golf_course");
        addMapping("leisure", "stadium");

        addMapping("amenity", "hospital");
        addMapping("amenity", "cinema");
        addMapping("amenity", "school");
        addMapping("amenity", "college");
        addMapping("amenity", "university");
        addMapping("amenity", "theatre");
        addMapping("amenity", "library");
        addMapping("amenity", "parking");
        addMapping("amenity", "place_of_worship");

        addMapping("highway", "pedestrian");
        addMapping("highway", "footway");
        addMapping("highway", "service");
        addMapping("highway", "street");

        addMapping("natural", "scrub");
        addMapping("natural", "wood");

        mappings.put("urban area", new Tag("landuse", "urban"));
        mappings.put("park or protected land", new Tag("leisure", "park"));
    }

    private static final Tag mTagArea = new Tag("area", "yes");

    @Override
    public void decodeTags(MapElement mapElement, Map<String, Object> properties) {

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String key = entry.getKey();

            if (!"kind".equals(key))
                continue;

            String value = (String) entry.getValue();

            Tag tag = mappings.get(value);
            if (tag == null) {
                System.out.println("unmatched " + value);
            } else {
                mapElement.tags.add(tag);
            }
            break;
        }
    }

    @Override
    public void postGeomHook(MapElement mapElement) {
        //if (mapElement.type != GeometryType.POLY) {
        mapElement.type = GeometryType.POLY;
        mapElement.tags.add(mTagArea);
        //}
    }
}
