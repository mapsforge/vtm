/*
 * Copyright 2017 devemux86
 * Copyright 2017 Gustl22
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
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.utils.math.MathUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class OpenmaptilesGeojsonTileSource extends GeojsonTileSource {

    private final static String DEFAULT_URL = "https://free.tilehosting.com/data/v3";
    private final static String DEFAULT_PATH = "/{Z}/{X}/{Y}.geojson";

    public static class Builder<T extends Builder<T>> extends UrlTileSource.Builder<T> {
        private String locale = "";

        public Builder() {
            super(DEFAULT_URL, DEFAULT_PATH, 1, 16);
            keyName("key");
        }

        public T locale(String locale) {
            this.locale = locale;
            return self();
        }

        public OpenmaptilesGeojsonTileSource build() {
            return new OpenmaptilesGeojsonTileSource(this);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Builder<?> builder() {
        return new Builder();
    }

    private static Map<String, Tag> mappings = new LinkedHashMap<>();

    private static Tag addMapping(String key, String val) {
        Tag tag = new Tag(key, val);
        mappings.put(key + "=" + val, tag);
        return tag;
    }

    private final String locale;

    public OpenmaptilesGeojsonTileSource(Builder<?> builder) {
        super(builder);
        this.locale = builder.locale;
    }

    public OpenmaptilesGeojsonTileSource() {
        this(builder());
    }

    public OpenmaptilesGeojsonTileSource(String urlString) {
        this(builder().url(urlString));
    }

    @Override
    public void decodeTags(MapElement mapElement, Map<String, Object> properties) {
        boolean hasName = false;
        String fallbackName = null;

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String val = (value instanceof String) ? (String) value : String.valueOf(value);

            if (key.startsWith(Tag.KEY_NAME)) {
                int len = key.length();
                if (len == 4) {
                    fallbackName = val;
                    continue;
                }
                if (len < 7)
                    continue;
                if (locale.equals(key.substring(5))) {
                    hasName = true;
                    mapElement.tags.add(new Tag(Tag.KEY_NAME, val, false));
                }
                continue;
            }

            Tag tag = mappings.get(key + "=" + val);
            if (tag == null)
                tag = addMapping(key, val);
            mapElement.tags.add(tag);
        }

        if (!hasName && fallbackName != null)
            mapElement.tags.add(new Tag(Tag.KEY_NAME, fallbackName, false));

        // Calculate height of building parts
        if (!properties.containsKey(Tag.KEY_HEIGHT)) {
            if (properties.containsKey(Tag.KEY_VOLUME) && properties.containsKey(Tag.KEY_AREA)) {
                Object volume = properties.get(Tag.KEY_VOLUME);
                String volumeStr = (volume instanceof String) ? (String) volume : String.valueOf(volume);
                Object area = properties.get(Tag.KEY_AREA);
                String areaStr = (area instanceof String) ? (String) area : String.valueOf(area);
                float height = Float.parseFloat(volumeStr) / Float.parseFloat(areaStr);
                String heightStr = String.valueOf(MathUtils.round2(height));
                mapElement.tags.add(new Tag(Tag.KEY_HEIGHT, heightStr, false));
            }
        }
    }
}
