/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2018 devemux86
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
package org.oscim.layers.tile.vector;

import org.oscim.core.Tag;
import org.oscim.core.TagSet;
import org.oscim.layers.tile.TileLoader;
import org.oscim.map.Map;
import org.oscim.utils.Utils;

public class OsmTileLayer extends VectorTileLayer {

    private static final int CACHE_LIMIT = 150;

    public OsmTileLayer(Map map) {
        this(map, map.viewport().getMinZoomLevel(), map.viewport().getMaxZoomLevel());
    }

    public OsmTileLayer(Map map, int zoomMin, int zoomMax) {
        super(map, CACHE_LIMIT);
        mTileManager.setZoomLevel(zoomMin, zoomMax);
    }

    @Override
    protected TileLoader createLoader() {
        return new OsmTileLoader(this);
    }

    private static class OsmTileLoader extends VectorTileLoader {
        private final TagSet mFilteredTags;

        OsmTileLoader(VectorTileLayer tileLayer) {
            super(tileLayer);
            mFilteredTags = new TagSet();
        }

        /* Replace tags that should only be matched by key in RenderTheme
         * to avoid caching RenderInstructions for each way of the same type
         * only with different name.
         * Maybe this should be done within RenderTheme, also allowing
         * to set these replacement rules in theme file. */
        private static final TagReplacement[] mTagReplacement = {
                new TagReplacement(Tag.KEY_NAME),
                new TagReplacement(Tag.KEY_HOUSE_NUMBER),
                new TagReplacement(Tag.KEY_REF),
                new TagReplacement(Tag.KEY_HEIGHT),
                new TagReplacement(Tag.KEY_MIN_HEIGHT)
        };

        protected TagSet filterTags(TagSet tagSet) {
            Tag[] tags = tagSet.getTags();

            mFilteredTags.clear();

            O:
            for (int i = 0, n = tagSet.size(); i < n; i++) {
                Tag t = tags[i];

                for (TagReplacement replacement : mTagReplacement) {
                    if (Utils.equals(t.key, replacement.key)) {
                        mFilteredTags.add(replacement.tag);
                        continue O;
                    }
                }

                mFilteredTags.add(t);
            }

            return mFilteredTags;
        }
    }
}
