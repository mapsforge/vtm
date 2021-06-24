/*
 * Copyright 2016-2020 devemux86
 * Copyright 2017 nebular
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

import android.graphics.BitmapFactory;
import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.android.cluster.ClusterMarkerRenderer;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Color;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.*;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClusterMarkerOverlayActivity extends MarkerOverlayActivity {

    private static final int COUNT = 5;
    private static final float STEP = 100f / 110000f; // roughly 100 meters

    @Override
    void createLayers() {
        // Map events receiver
        mMap.layers().add(new MapEventsReceiver(mMap));

        UrlTileSource tileSource = DefaultSources.OPENSTREETMAP
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();
        tileSource.setHttpRequestHeaders(Collections.singletonMap("User-Agent", "vtm-android-example"));
        mMap.layers().add(new BitmapTileLayer(mMap, tileSource));

        Bitmap bitmapPoi = new AndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.marker_poi));
        final MarkerSymbol symbol;
        if (BILLBOARDS)
            symbol = new MarkerSymbol(bitmapPoi, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);
        else
            symbol = new MarkerSymbol(bitmapPoi, MarkerSymbol.HotspotPlace.CENTER, false);

        MarkerRendererFactory markerRendererFactory = new MarkerRendererFactory() {
            @Override
            public MarkerRenderer create(MarkerLayer markerLayer) {
                return new ClusterMarkerRenderer(markerLayer, symbol, new ClusterMarkerRenderer.ClusterStyle(Color.WHITE, Color.BLUE)) {
                    @Override
                    protected Bitmap getClusterBitmap(int size) {
                        // Can customize cluster bitmap here
                        return super.getClusterBitmap(size);
                    }
                };
            }
        };
        mMarkerLayer = new ItemizedLayer(
                mMap,
                new ArrayList<MarkerInterface>(),
                markerRendererFactory,
                this);
        mMap.layers().add(mMarkerLayer);

        // Create some markers spaced STEP degrees
        List<MarkerInterface> pts = new ArrayList<>();
        mMap.setMapPosition(53.08, 8.83, 1 << 15);
        GeoPoint center = mMap.getMapPosition().getGeoPoint();
        for (int x = -COUNT; x < COUNT; x++) {
            for (int y = -COUNT; y < COUNT; y++) {
                double random = STEP * Math.random() * 2;
                MarkerItem item = new MarkerItem(y + ", " + x, "",
                        new GeoPoint(center.getLatitude() + y * STEP + random, center.getLongitude() + x * STEP + random)
                );
                pts.add(item);
            }
        }
        mMarkerLayer.addItems(pts);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* ignore saved position */
        mMap.setMapPosition(53.08, 8.83, 1 << 15);
    }
}
