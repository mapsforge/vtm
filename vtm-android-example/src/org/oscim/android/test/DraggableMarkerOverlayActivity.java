/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2016-2020 devemux86
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

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.android.drag.DragAndDropListener;
import org.oscim.android.drag.DraggableItemizedLayer;
import org.oscim.android.drag.DraggableMarkerItem;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.GeoPoint;
import org.oscim.event.Gesture;
import org.oscim.event.GestureListener;
import org.oscim.event.MotionEvent;
import org.oscim.layers.Layer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerInterface;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.marker.MarkerSymbol.HotspotPlace;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.map.Map;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DraggableMarkerOverlayActivity extends MapActivity {

    static final boolean BILLBOARDS = true;
    MarkerSymbol mFocusMarker;
    ItemizedLayer mMarkerLayer;

    public DraggableMarkerOverlayActivity() {
        super(R.layout.activity_map_draggable);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createLayers();
    }

    void createLayers() {
        // Map events receiver
        mMap.layers().add(new MapEventsReceiver(mMap));

        UrlTileSource tileSource = DefaultSources.OPENSTREETMAP
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();
        tileSource.setHttpRequestHeaders(Collections.singletonMap("User-Agent", "vtm-android-example"));
        mMap.layers().add(new BitmapTileLayer(mMap, tileSource));

        Bitmap bitmapPoi = new AndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.marker_poi));
        MarkerSymbol symbol;
        if (BILLBOARDS)
            symbol = new MarkerSymbol(bitmapPoi, HotspotPlace.BOTTOM_CENTER);
        else
            symbol = new MarkerSymbol(bitmapPoi, HotspotPlace.CENTER, false);

        Bitmap bitmapFocus = new AndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.marker_focus));
        if (BILLBOARDS)
            mFocusMarker = new MarkerSymbol(bitmapFocus, HotspotPlace.BOTTOM_CENTER);
        else
            mFocusMarker = new MarkerSymbol(bitmapFocus, HotspotPlace.CENTER, false);

        mMarkerLayer = new DraggableItemizedLayer(mMap, new ArrayList<MarkerInterface>(), symbol, null);
        mMap.layers().add(mMarkerLayer);

        List<MarkerInterface> pts = new ArrayList<>();

        for (double lat = -90; lat <= 90; lat += 25) {
            for (double lon = -180; lon <= 180; lon += 25)
                pts.add(
                        new DraggableMarkerItem(
                                null,
                                lat + "/" + lon,
                                "",
                                new GeoPoint(lat, lon),
                                mFocusMarker,
                                mFocusMarker,
                                true,
                                new DragAndDropListener() {
                                    @Override
                                    public void startDragItemAtGeoPoint(final DraggableMarkerItem item, final GeoPoint geoPoint) {
                                        Log.i(DraggableMarkerOverlayActivity.this.getClass().getSimpleName(), "startDragItemAtGeoPoint: " + geoPoint);
                                    }

                                    @Override
                                    public void ongoingDragItemToGeoPoint(final DraggableMarkerItem item, final GeoPoint geoPoint) {
                                        Log.i(DraggableMarkerOverlayActivity.this.getClass().getSimpleName(), "ongoingDragItemToGeoPoint: " + geoPoint);
                                    }

                                    @Override
                                    public void dropItemAtGeoPoint(final DraggableMarkerItem item, final GeoPoint geoPoint) {
                                        Log.i(DraggableMarkerOverlayActivity.this.getClass().getSimpleName(), "dropItemAtGeoPoint: " + geoPoint);
                                    }
                                }));
        }

        mMarkerLayer.addItems(pts);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* ignore saved position */
        mMap.setMapPosition(0, 0, 1 << 2);
    }

    class MapEventsReceiver extends Layer implements GestureListener {

        MapEventsReceiver(Map map) {
            super(map);
        }

        @Override
        public boolean onGesture(Gesture g, MotionEvent e) {
            if (g instanceof Gesture.Tap) {
                GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
                Toast.makeText(DraggableMarkerOverlayActivity.this, "Map tap\n" + p, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (g instanceof Gesture.LongPress) {
                GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
                Toast.makeText(DraggableMarkerOverlayActivity.this, "Map long press\n" + p, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (g instanceof Gesture.TripleTap) {
                GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
                Toast.makeText(DraggableMarkerOverlayActivity.this, "Map triple tap\n" + p, Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        }
    }
}
