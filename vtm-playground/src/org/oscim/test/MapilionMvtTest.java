/*
 * Copyright 2018-2019 devemux86
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

import org.oscim.core.MapPosition;
import org.oscim.gdx.lwjgl.GdxMapApp;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;
import org.oscim.tiling.source.mvt.MapilionMvtTileSource;

import java.io.File;
import java.util.UUID;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class MapilionMvtTest extends GdxMapApp {

    // Metered API key for demonstration purposes
    private static final String API_KEY = "3b3d8353-0fb8-4513-bfe0-d620b2d77c45";

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

        UrlTileSource tileSource = MapilionMvtTileSource.builder()
                .apiKey(API_KEY)
                .httpFactory(factory)
                //.locale("en")
                .build();

        VectorTileLayer l = mMap.setBaseMap(tileSource);
        mMap.setTheme(VtmThemes.OPENMAPTILES);

        // Hillshading
        UrlTileSource shadedTileSource = DefaultSources.MAPILION_HILLSHADE_2
                .apiKey(API_KEY)
                .httpFactory(factory)
                .build();
        mMap.layers().add(new BitmapTileLayer(mMap, shadedTileSource));

        mMap.layers().add(new BuildingLayer(mMap, l));
        mMap.layers().add(new LabelLayer(mMap, l));

        MapPosition pos = MapPreferences.getMapPosition();
        if (pos != null)
            mMap.setMapPosition(pos);
    }

    @Override
    public void dispose() {
        MapPreferences.saveMapPosition(mMap.getMapPosition());
        super.dispose();
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new MapilionMvtTest());
    }
}
