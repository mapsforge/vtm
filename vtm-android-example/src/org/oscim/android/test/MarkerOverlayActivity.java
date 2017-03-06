/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2016-2017 devemux86
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
import android.widget.Toast;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.GeoPoint;
import org.oscim.event.Gesture;
import org.oscim.event.GestureListener;
import org.oscim.event.MotionEvent;
import org.oscim.layers.Layer;
import org.oscim.layers.TileGridLayer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.marker.MarkerSymbol.HotspotPlace;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Map;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

import java.util.ArrayList;
import java.util.List;

import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;

public class MarkerOverlayActivity extends SimpleMapActivity
        implements ItemizedLayer.OnItemGestureListener<MarkerItem> {

    static final boolean BILLBOARDS = true;
    MarkerSymbol mFocusMarker;
    ItemizedLayer<MarkerItem> mMarkerLayer;

    @Override
    void createLayers() {
        // Map events receiver
        mMap.layers().add(new MapEventsReceiver(mMap));

        VectorTileLayer l = mMap.setBaseMap(new OSciMap4TileSource());
        mMap.layers().add(new BuildingLayer(mMap, l));
        mMap.layers().add(new LabelLayer(mMap, l));
        mMap.setTheme(VtmThemes.DEFAULT);

        /* directly load bitmap from resources */
        Bitmap bitmap = drawableToBitmap(getResources(), R.drawable.marker_poi);

        MarkerSymbol symbol;
        if (BILLBOARDS)
            symbol = new MarkerSymbol(bitmap, HotspotPlace.BOTTOM_CENTER);
        else
            symbol = new MarkerSymbol(bitmap, HotspotPlace.CENTER, false);

        /* another option: use some bitmap drawable */
        Drawable d = getResources().getDrawable(R.drawable.marker_focus);
        if (BILLBOARDS)
            mFocusMarker = new MarkerSymbol(drawableToBitmap(d), HotspotPlace.BOTTOM_CENTER);
        else
            mFocusMarker = new MarkerSymbol(drawableToBitmap(d), HotspotPlace.CENTER, false);

        mMarkerLayer = new ItemizedLayer<>(mMap, new ArrayList<MarkerItem>(), symbol, this);
        mMap.layers().add(mMarkerLayer);

        List<MarkerItem> pts = new ArrayList<>();

        for (double lat = -90; lat <= 90; lat += 5) {
            for (double lon = -180; lon <= 180; lon += 5)
                pts.add(new MarkerItem(lat + "/" + lon, "", new GeoPoint(lat, lon)));
        }

        mMarkerLayer.addItems(pts);

        mMap.layers().add(new TileGridLayer(mMap, getResources().getDisplayMetrics().density));
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* ignore saved position */
        mMap.setMapPosition(0, 0, 1 << 2);
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerItem item) {
        if (item.getMarker() == null)
            item.setMarker(mFocusMarker);
        else
            item.setMarker(null);

        Toast.makeText(this, "Marker tap\n" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onItemLongPress(int index, MarkerItem item) {
        if (item.getMarker() == null)
            item.setMarker(mFocusMarker);
        else
            item.setMarker(null);

        Toast.makeText(this, "Marker long press\n" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return true;
    }

    class MapEventsReceiver extends Layer implements GestureListener {

        MapEventsReceiver(Map map) {
            super(map);
        }

        @Override
        public boolean onGesture(Gesture g, MotionEvent e) {
            if (g instanceof Gesture.Tap) {
                GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
                Toast.makeText(MarkerOverlayActivity.this, "Map tap\n" + p, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (g instanceof Gesture.LongPress) {
                GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
                Toast.makeText(MarkerOverlayActivity.this, "Map long press\n" + p, Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        }
    }
}
