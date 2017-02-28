/*
 * Copyright 2016-2017 devemux86
 * Copyright 2017 Mathieu De Brito
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
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.mvt.MapboxTileSource;

public class MapboxMapActivity extends MapActivity {

    private static final boolean USE_CACHE = true;

    private TileCache mCache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UrlTileSource tileSource = MapboxTileSource.builder()
                .apiKey("mapzen-xxxxxxx") // Put a proper API key
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                //.locale("en")
                .build();

        if (USE_CACHE) {
            // Cache the tiles into a local SQLite database
            mCache = new TileCache(this, null, "tile.db");
            mCache.setCacheSize(512 * (1 << 10));
            tileSource.setCache(mCache);
        }

        VectorTileLayer l = mMap.setBaseMap(tileSource);
        mMap.setTheme(VtmThemes.MAPZEN);

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
