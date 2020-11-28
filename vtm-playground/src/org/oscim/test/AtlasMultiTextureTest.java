/*
 * Copyright 2016-2020 devemux86
 * Copyright 2017 Longri
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
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint;
import org.oscim.core.GeoPoint;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerInterface;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.renderer.atlas.TextureAtlas;
import org.oscim.renderer.atlas.TextureRegion;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;
import org.oscim.utils.TextureAtlasUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static org.oscim.layers.marker.MarkerSymbol.HotspotPlace;

public class AtlasMultiTextureTest extends MarkerLayerTest {

    @Override
    public void createLayers() {
        // Map events receiver
        mMap.layers().add(new MapEventsReceiver(mMap));

        UrlTileSource tileSource = DefaultSources.OPENSTREETMAP
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();
        tileSource.setHttpRequestHeaders(Collections.singletonMap("User-Agent", "vtm-playground"));
        mMap.layers().add(new BitmapTileLayer(mMap, tileSource));

        mMap.setMapPosition(0, 0, 1 << 2);

        // Create Atlas from Bitmaps
        java.util.Map<Object, Bitmap> inputMap = new LinkedHashMap<>();
        java.util.Map<Object, TextureRegion> regionsMap = new LinkedHashMap<>();
        List<TextureAtlas> atlasList = new ArrayList<>();

        Canvas canvas = CanvasAdapter.newCanvas();
        Paint paint = CanvasAdapter.newPaint();
        paint.setTypeface(Paint.FontFamily.DEFAULT, Paint.FontStyle.NORMAL);
        paint.setTextSize(12);
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        List<MarkerInterface> pts = new ArrayList<>();
        for (double lat = -90; lat <= 90; lat += 5) {
            for (double lon = -180; lon <= 180; lon += 5) {
                String title = lat + "/" + lon;
                pts.add(new MarkerItem(title, "", new GeoPoint(lat, lon)));

                Bitmap bmp = CanvasAdapter.newBitmap(40, 40, 0);
                canvas.setBitmap(bmp);
                canvas.fillColor(Color.GREEN);

                canvas.drawText(Double.toString(lat), 3, 17, paint);
                canvas.drawText(Double.toString(lon), 3, 35, paint);
                inputMap.put(title, bmp);
            }
        }

        // Bitmaps will never used any more
        // With iOS we must flip the Y-Axis
        TextureAtlasUtils.createTextureRegions(inputMap, regionsMap, atlasList, true, false);

        mMarkerLayer = new ItemizedLayer(mMap, new ArrayList<MarkerInterface>(), (MarkerSymbol) null, this);
        mMap.layers().add(mMarkerLayer);

        mMarkerLayer.addItems(pts);

        // set all markers
        for (MarkerInterface item : pts) {
            MarkerItem markerItem = (MarkerItem) item;
            MarkerSymbol markerSymbol = new MarkerSymbol(regionsMap.get(markerItem.getTitle()), HotspotPlace.BOTTOM_CENTER);
            markerItem.setMarker(markerSymbol);
        }

        System.out.println("Atlas count: " + atlasList.size());
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerInterface item) {
        MarkerItem markerItem = (MarkerItem) item;
        System.out.println("Marker tap " + markerItem.getTitle());
        return true;
    }

    @Override
    public boolean onItemLongPress(int index, MarkerInterface item) {
        MarkerItem markerItem = (MarkerItem) item;
        System.out.println("Marker long press " + markerItem.getTitle());
        return true;
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new AtlasMultiTextureTest());
    }
}
