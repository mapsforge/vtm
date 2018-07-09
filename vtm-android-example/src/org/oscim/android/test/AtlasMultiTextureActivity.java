/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2016-2018 devemux86
 * Copyright 2017-2018 Longri
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
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

import android.widget.Toast;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint;
import org.oscim.core.GeoPoint;
import org.oscim.event.Event;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.marker.MarkerSymbol.HotspotPlace;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.renderer.GLState;
import org.oscim.renderer.atlas.TextureAtlas;
import org.oscim.renderer.atlas.TextureRegion;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.bitmap.DefaultSources;
import org.oscim.utils.TextureAtlasUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AtlasMultiTextureActivity extends MarkerOverlayActivity {

    @Override
    void createLayers() {
        GLState.event.bind(new GLState.Listener() {
            @Override
            public void onGLStateInitEvent(Event event) {
                createAfterGlInit();
            }
        });
    }

    public void createAfterGlInit(){
        // Map events receiver
        mMap.layers().add(new MapEventsReceiver(mMap));

        TileSource tileSource = DefaultSources.OPENSTREETMAP
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();
        mMap.layers().add(new BitmapTileLayer(mMap, tileSource));

        // Create Atlas from Bitmaps
        java.util.Map<Object, Bitmap> inputMap = new LinkedHashMap<>();
        java.util.Map<Object, TextureRegion> regionsMap = new LinkedHashMap<>();
        final List<TextureAtlas> atlasList = new ArrayList<>();

        Canvas canvas = CanvasAdapter.newCanvas();
        Paint paint = CanvasAdapter.newPaint();
        paint.setTypeface(Paint.FontFamily.DEFAULT, Paint.FontStyle.NORMAL);
        paint.setTextSize(12 * CanvasAdapter.getScale());
        paint.setStrokeWidth(2 * CanvasAdapter.getScale());
        paint.setColor(Color.BLACK);
        List<MarkerItem> pts = new ArrayList<>();
        for (double lat = -90; lat <= 90; lat += 10) {
            for (double lon = -180; lon <= 180; lon += 10) {
                String title = lat + "/" + lon;
                pts.add(new MarkerItem(title, "", new GeoPoint(lat, lon)));

                Bitmap bmp = CanvasAdapter.newBitmap((int) (40 * CanvasAdapter.getScale()), (int) (40 * CanvasAdapter.getScale()), 0);
                canvas.setBitmap(bmp);
                canvas.fillColor(Color.GREEN);

                canvas.drawText(Double.toString(lat), 3 * CanvasAdapter.getScale(), 17 * CanvasAdapter.getScale(), paint);
                canvas.drawText(Double.toString(lon), 3 * CanvasAdapter.getScale(), 35 * CanvasAdapter.getScale(), paint);
                inputMap.put(title, bmp);
            }
        }

        // Bitmaps will never used any more
        // With iOS we must flip the Y-Axis
        TextureAtlasUtils.createTextureRegions(inputMap, regionsMap, atlasList, true, false);

        mMarkerLayer = new ItemizedLayer<>(mMap, new ArrayList<MarkerItem>(), (MarkerSymbol) null, this);
        mMap.layers().add(mMarkerLayer);

        mMarkerLayer.addItems(pts);

        // set all markers
        for (MarkerItem item : pts) {
            MarkerSymbol markerSymbol = new MarkerSymbol(regionsMap.get(item.getTitle()), HotspotPlace.BOTTOM_CENTER);
            item.setMarker(markerSymbol);
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AtlasMultiTextureActivity.this, "Atlas count: " + atlasList.size(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerItem item) {
        Toast.makeText(this, "Marker tap\n" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onItemLongPress(int index, MarkerItem item) {
        Toast.makeText(this, "Marker long press\n" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return true;
    }
}
