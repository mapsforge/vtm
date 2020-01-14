/*
 * Copyright 2016-2018 devemux86
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
package org.oscim.test;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Color;
import org.oscim.core.GeoPoint;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.marker.*;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.bitmap.DefaultSources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClusterMarkerLayerTest extends MarkerLayerTest {

    private static final int COUNT = 5;
    private static final float STEP = 100f / 110000f; // roughly 100 meters

    @Override
    public void createLayers() {
        try {
            // Map events receiver
            mMap.layers().add(new MapEventsReceiver(mMap));

            TileSource tileSource = DefaultSources.OPENSTREETMAP
                    .httpFactory(new OkHttpEngine.OkHttpFactory())
                    .build();
            mMap.layers().add(new BitmapTileLayer(mMap, tileSource));

            mMap.setMapPosition(53.08, 8.83, 1 << 15);

            Bitmap bitmapPoi = CanvasAdapter.decodeBitmap(getClass().getResourceAsStream("/res/marker_poi.png"));
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
            mMarkerLayer = new ItemizedLayer<>(
                    mMap,
                    new ArrayList<MarkerItem>(),
                    markerRendererFactory,
                    this);
            mMap.layers().add(mMarkerLayer);

            // Create some markers spaced STEP degrees
            List<MarkerItem> pts = new ArrayList<>();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new ClusterMarkerLayerTest());
    }
}
