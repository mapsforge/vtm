/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2017-2020 devemux86
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
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import org.oscim.core.MapPosition;
import org.oscim.core.MercatorProjection;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.renderer.MapRenderer;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.bitmap.BitmapTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;

import java.io.File;
import java.util.Collections;

public class BitmapTileActivity extends MapActivity {

    private static final boolean USE_CACHE = false;

    private final BitmapTileSource mTileSource;
    protected BitmapTileLayer mBitmapLayer;

    public BitmapTileActivity() {
        this(DefaultSources.OPENSTREETMAP.build());
    }

    public BitmapTileActivity(BitmapTileSource tileSource) {
        super(R.layout.activity_map);
        mTileSource = tileSource;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapRenderer.setBackgroundColor(0xff777777);

        if (mTileSource == null)
            return;

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (USE_CACHE) {
            // Cache the tiles into file system
            File cacheDirectory = new File(getExternalCacheDir(), "tiles");
            int cacheSize = 10 * 1024 * 1024; // 10 MB
            Cache cache = new Cache(cacheDirectory, cacheSize);
            builder.cache(cache);
        }

        mTileSource.setHttpEngine(new OkHttpEngine.OkHttpFactory(builder));
        mTileSource.setHttpRequestHeaders(Collections.singletonMap("User-Agent", "vtm-android-example"));

        mBitmapLayer = new BitmapTileLayer(mMap, mTileSource);
        mMap.layers().add(mBitmapLayer);

        //loooop(1);
    }

    // Stress testing
    void loooop(final int i) {
        final long time = (long) (500 + Math.random() * 1000);
        mMapView.postDelayed(new Runnable() {
            @Override
            public void run() {

                MapPosition p = new MapPosition();
                if (i == 1) {
                    mMapView.map().getMapPosition(p);
                    p.setScale(4);
                    mMapView.map().animator().animateTo(time, p);
                } else {
                    //mMapView.map().setMapPosition(p);
                    p.setScale(2 + (1 << (int) (Math.random() * 13)));
                    //    p.setX((p.getX() + (Math.random() * 4 - 2) / p.getScale()));
                    //    p.setY((p.getY() + (Math.random() * 4 - 2) / p.getScale()));
                    p.setX(MercatorProjection.longitudeToX(Math.random() * 180));
                    p.setY(MercatorProjection.latitudeToY(Math.random() * 60));

                    p.setTilt((float) (Math.random() * 60));
                    p.setBearing((float) (Math.random() * 360));
                    p.setRoll((float) (Math.random() * 360));
                    //mMapView.map().setMapPosition(p);

                    mMapView.map().animator().animateTo(time, p);
                }
                loooop((i + 1) % 2);

            }
        }, time);
    }
}
