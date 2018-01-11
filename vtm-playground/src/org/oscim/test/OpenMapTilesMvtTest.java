/*
 * Copyright 2017 devemux86
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
package org.oscim.test;

import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.geojson.OpenMapTilesGeojsonTileSource;
import org.oscim.tiling.source.mvt.OpenMapTilesMvtTileSource;

import java.io.File;
import java.util.UUID;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class OpenMapTilesMvtTest extends GdxMapApp {

    private static final boolean USE_CACHE = false;

    @Override
    public void createLayers() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (USE_CACHE) {
            // Cache the tiles into file system
            File cacheDirectory = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
            int cacheSize = 10 * 1024 * 1024; // 10 MB
            Cache cache = new Cache(cacheDirectory, cacheSize);
            builder.cache(cache);
        }
        OkHttpEngine.OkHttpFactory factory = new OkHttpEngine.OkHttpFactory(builder);

        UrlTileSource tileSource = OpenMapTilesMvtTileSource.builder()
                .apiKey("xxxxxx") // Put a proper API key
                .httpFactory(factory)
                .locale("en")
                .build();

        VectorTileLayer l = mMap.setBaseMap(tileSource);
        mMap.setTheme(VtmThemes.OPENMAPTILES);

        mMap.layers().add(new BuildingLayer(mMap, l));
        mMap.layers().add(new LabelLayer(mMap, l));
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new OpenMapTilesMvtTest());
    }
}
