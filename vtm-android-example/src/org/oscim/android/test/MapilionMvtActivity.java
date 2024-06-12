/*
 * Copyright 2018-2020 devemux86
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

import android.os.Build;
import android.os.Bundle;
import okhttp3.Cache;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.internal.VtmThemes;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;
import org.oscim.tiling.source.mvt.MapilionMvtTileSource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapilionMvtActivity extends MapActivity {

    // Metered API key for demonstration purposes
    private static final String API_KEY = "3b3d8353-0fb8-4513-bfe0-d620b2d77c45";

    private static final boolean USE_CACHE = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (USE_CACHE) {
            // Cache the tiles into file system
            File cacheDirectory = new File(getExternalCacheDir(), "tiles");
            int cacheSize = 10 * 1024 * 1024; // 10 MB
            Cache cache = new Cache(cacheDirectory, cacheSize);
            builder.cache(cache);
        }

        // https://github.com/square/okhttp/issues/4053
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            List<CipherSuite> cipherSuites = new ArrayList<>();
            List<CipherSuite> modernTlsCipherSuites = ConnectionSpec.MODERN_TLS.cipherSuites();
            if (modernTlsCipherSuites != null)
                cipherSuites.addAll(modernTlsCipherSuites);
            cipherSuites.add(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA);
            cipherSuites.add(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA);
            ConnectionSpec legacyTls = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .cipherSuites(cipherSuites.toArray(new CipherSuite[0]))
                    .build();
            builder.connectionSpecs(Arrays.asList(legacyTls, ConnectionSpec.CLEARTEXT));
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
    }
}
