/*
 * Copyright 2014 Hannes Janetzek
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
package org.oscim.android.test;

import android.os.Bundle;

import org.oscim.android.cache.TileCache;
import org.oscim.core.MapPosition;
import org.oscim.core.MercatorProjection;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.renderer.MapRenderer;
import org.oscim.tiling.source.bitmap.BitmapTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitmapTileMapActivity extends MapActivity {

    static final Logger log = LoggerFactory.getLogger(BitmapTileMapActivity.class);

    private final static boolean USE_CACHE = true;

    private final BitmapTileSource mTileSource;
    protected BitmapTileLayer mBitmapLayer;

    public BitmapTileMapActivity() {
        this(DefaultSources.OPENSTREETMAP.build());
    }

    public BitmapTileMapActivity(BitmapTileSource tileSource) {
        super(R.layout.activity_map);
        mTileSource = tileSource;
    }

    private TileCache mCache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapRenderer.setBackgroundColor(0xff777777);

        if (mTileSource == null)
            return;

        if (USE_CACHE) {
            String cacheFile = mTileSource.getUrl()
                    .toString()
                    .replaceFirst("https?://", "")
                    .replaceAll("/", "-");

            log.debug("use bitmap cache {}", cacheFile);
            mCache = new TileCache(this, null, cacheFile);
            mCache.setCacheSize(512 * (1 << 10));
            mTileSource.setCache(mCache);
        }

        mBitmapLayer = new BitmapTileLayer(mMap, mTileSource);
        mMap.layers().add(mBitmapLayer);

        //loooop(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCache != null)
            mCache.dispose();
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
                    //mMapView.map().setMapPosition(p);

                    mMapView.map().animator().animateTo(time, p);
                }
                loooop((i + 1) % 2);

            }
        }, time);
    }
}
