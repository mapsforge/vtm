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

import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.android.drag.DragAndDropListener;
import org.oscim.android.drag.DraggableItemizedLayer;
import org.oscim.android.drag.DraggableMarkerItem;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerInterface;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.marker.MarkerSymbol.HotspotPlace;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DraggableMarkerOverlayActivity extends MapActivity {

    public DraggableMarkerOverlayActivity() {
        super(R.layout.activity_map_draggable);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createLayers();
    }

    void createLayers() {
        final UrlTileSource tileSource =
                DefaultSources
                        .OPENSTREETMAP
                        .httpFactory(new OkHttpEngine.OkHttpFactory())
                        .build();
        tileSource.setHttpRequestHeaders(Collections.singletonMap("User-Agent", "vtm-android-example"));
        mMap.layers().add(new BitmapTileLayer(mMap, tileSource));

        final ItemizedLayer mMarkerLayer =
                new DraggableItemizedLayer(
                        mMap,
                        new ArrayList<MarkerInterface>(),
                        new MarkerSymbol(
                                new AndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.marker_poi)),
                                HotspotPlace.BOTTOM_CENTER),
                        null);
        mMap.layers().add(mMarkerLayer);

        final DragAndDropListener dragAndDropListener =
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
                };

        final List<MarkerInterface> pts = new ArrayList<>();
        for (double lat = -90; lat <= 90; lat += 45) {
            for (double lon = -180; lon <= 180; lon += 45) {
                pts.add(
                        new DraggableMarkerItem(
                                lat + "/" + lon,
                                "",
                                new GeoPoint(lat, lon),
                                dragAndDropListener));
            }
        }

        mMarkerLayer.addItems(pts);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* ignore saved position */
        mMap.setMapPosition(0, 0, 1 << 2);
    }
}
