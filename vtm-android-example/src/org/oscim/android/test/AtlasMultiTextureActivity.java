/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2016-2017 devemux86
 * Copyright 2017 Longri
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

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint;
import org.oscim.core.GeoPoint;
import org.oscim.layers.TileGridLayer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.marker.MarkerSymbol.HotspotPlace;
import org.oscim.renderer.atlas.TextureAtlas;
import org.oscim.renderer.atlas.TextureRegion;
import org.oscim.utils.TextureAtlasUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;

public class AtlasMultiTextureActivity extends MarkerOverlayActivity {

    private java.util.Map<Object, TextureRegion> regionsMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBitmapLayer.tileRenderer().setBitmapAlpha(0.5f);

        // Map events receiver
        mMap.layers().add(new MapEventsReceiver(mMap));

        /* directly load bitmap from resources */
        Bitmap bitmapPoi = drawableToBitmap(getResources(), R.drawable.marker_poi);

        /* another option: use some bitmap drawable */
        Drawable d = getResources().getDrawable(R.drawable.marker_focus);
        Bitmap bitmapFocus = drawableToBitmap(d);

        // Create Atlas from Bitmaps
        java.util.Map<Object, Bitmap> inputMap = new LinkedHashMap<>();
        regionsMap = new LinkedHashMap<>();
        List<TextureAtlas> atlasList = new ArrayList<>();

        inputMap.put("poi", bitmapPoi);
        inputMap.put("focus", bitmapFocus);

        float scale = getResources().getDisplayMetrics().density;
        Canvas canvas = CanvasAdapter.newCanvas();
        Paint paint = CanvasAdapter.newPaint();
        paint.setTypeface(Paint.FontFamily.DEFAULT, Paint.FontStyle.NORMAL);
        paint.setTextSize(12 * scale);
        paint.setStrokeWidth(2 * scale);
        paint.setColor(Color.BLACK);
        List<MarkerItem> pts = new ArrayList<>();
        for (double lat = -90; lat <= 90; lat += 5) {
            for (double lon = -180; lon <= 180; lon += 5) {
                String title = lat + "/" + lon;
                pts.add(new MarkerItem(title, "", new GeoPoint(lat, lon)));

                Bitmap bmp = CanvasAdapter.newBitmap((int) (40 * scale), (int) (40 * scale), 0);
                canvas.setBitmap(bmp);
                canvas.fillColor(Color.GREEN);

                canvas.drawText(Double.toString(lat), 3 * scale, 17 * scale, paint);
                canvas.drawText(Double.toString(lon), 3 * scale, 35 * scale, paint);
                inputMap.put(title, bmp);
            }
        }

        // Bitmaps will never used any more
        // With iOS we must flip the Y-Axis
        TextureAtlasUtils.createTextureRegions(inputMap, regionsMap, atlasList, true, false);

        MarkerSymbol symbol;
        if (BILLBOARDS)
            symbol = new MarkerSymbol(regionsMap.get("poi"), HotspotPlace.BOTTOM_CENTER);
        else
            symbol = new MarkerSymbol(regionsMap.get("poi"), HotspotPlace.CENTER, false);

        if (BILLBOARDS)
            mFocusMarker = new MarkerSymbol(regionsMap.get("focus"), HotspotPlace.BOTTOM_CENTER);
        else
            mFocusMarker = new MarkerSymbol(regionsMap.get("focus"), HotspotPlace.CENTER, false);

        mMarkerLayer = new ItemizedLayer<>(mMap, new ArrayList<MarkerItem>(), symbol, this);
        mMap.layers().add(mMarkerLayer);

        mMarkerLayer.addItems(pts);

        mMap.layers().add(new TileGridLayer(mMap, getResources().getDisplayMetrics().density));

        // set all markers
        for (MarkerItem item : pts) {
            MarkerSymbol markerSymbol = new MarkerSymbol(regionsMap.get(item.getTitle()), HotspotPlace.BOTTOM_CENTER);
            item.setMarker(markerSymbol);
        }

        Toast.makeText(this, "Atlas count: " + atlasList.size(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerItem item) {
        if (item.getMarker() == null) {
            MarkerSymbol markerSymbol = new MarkerSymbol(regionsMap.get(item.getTitle()), HotspotPlace.BOTTOM_CENTER);
            item.setMarker(markerSymbol);
        } else
            item.setMarker(null);

        Toast.makeText(this, "Marker tap\n" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return true;
    }
}
