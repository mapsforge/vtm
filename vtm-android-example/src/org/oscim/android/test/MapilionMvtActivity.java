/*
 * Copyright 2018 devemux86
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
package org.oscim.android.test;

import android.os.Bundle;

import org.oscim.android.cache.TileCache;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;
import org.oscim.tiling.source.mvt.MapilionMvtTileSource;

public class MapilionMvtActivity extends MapActivity {

    // Metered API key for demonstration purposes
    private static final String API_KEY = "3b3d8353-0fb8-4513-bfe0-d620b2d77c45";

    private static final boolean USE_CACHE = false;

    private TileCache mCache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OkHttpEngine.OkHttpFactory factory = new OkHttpEngine.OkHttpFactory();

        UrlTileSource tileSource = MapilionMvtTileSource.builder()
                .apiKey(API_KEY)
                .httpFactory(factory)
                //.locale("en")
                .build();

        if (USE_CACHE) {
            // Cache the tiles into a local SQLite database
            mCache = new TileCache(this, null, "tile.db");
            mCache.setCacheSize(512 * (1 << 10));
            tileSource.setCache(mCache);
        }

        VectorTileLayer l = mMap.setBaseMap(tileSource);
        mMap.setTheme(VtmThemes.OPENMAPTILES);

        // Hillshading
        UrlTileSource shadedTileSource = DefaultSources.MAPILION_HILLSHADE
                .apiKey(API_KEY)
                .httpFactory(factory)
                .build();
        mMap.layers().add(new BitmapTileLayer(mMap, shadedTileSource));

        mMap.layers().add(new BuildingLayer(mMap, l));
        mMap.layers().add(new LabelLayer(mMap, l));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCache != null)
            mCache.dispose();
    }
}
