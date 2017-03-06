/*
 * Copyright 2016-2017 devemux86
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
import org.oscim.layers.TileGridLayer;
import org.oscim.layers.marker.ClusterMarkerRenderer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

import java.util.ArrayList;
import java.util.List;

public class ClusterMarkerLayerTest extends MarkerLayerTest {

    private static final int COUNT = 5;
    private static final float STEP = 100f / 110000f; // roughly 100 meters

    @Override
    public void createLayers() {
        // Map events receiver
        mMap.layers().add(new MapEventsReceiver(mMap));

        VectorTileLayer l = mMap.setBaseMap(new OSciMap4TileSource());
        mMap.layers().add(new BuildingLayer(mMap, l));
        mMap.layers().add(new LabelLayer(mMap, l));
        mMap.setTheme(VtmThemes.DEFAULT);

        mMap.setMapPosition(53.08, 8.83, 1 << 15);

        Bitmap bitmapPoi = CanvasAdapter.decodeBitmap(getClass().getResourceAsStream("/res/marker_poi.png"));
        MarkerSymbol symbol;
        if (BILLBOARDS)
            symbol = new MarkerSymbol(bitmapPoi, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);
        else
            symbol = new MarkerSymbol(bitmapPoi, MarkerSymbol.HotspotPlace.CENTER, false);

        mMarkerLayer = new ItemizedLayer<>(
                mMap,
                new ArrayList<MarkerItem>(),
                ClusterMarkerRenderer.factory(symbol, new ClusterMarkerRenderer.ClusterStyle(Color.WHITE, Color.BLUE)),
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

        mMap.layers().add(new TileGridLayer(mMap));
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new ClusterMarkerLayerTest());
    }
}
