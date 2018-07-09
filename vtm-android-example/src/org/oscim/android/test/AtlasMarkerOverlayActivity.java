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

import org.oscim.backend.canvas.Bitmap;
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

import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;

public class AtlasMarkerOverlayActivity extends MarkerOverlayActivity {

    @Override
    void createLayers() {
        // wait for gl surface view is created
        GLState.event.bind(new GLState.Listener() {
            @Override
            public void onGLStateInitEvent(Event event) {
                createGlLayers();
            }
        });
    }


    void createGlLayers() {
        // Map events receiver
        mMap.layers().add(new MapEventsReceiver(mMap));

        TileSource tileSource = DefaultSources.OPENSTREETMAP
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();
        mMap.layers().add(new BitmapTileLayer(mMap, tileSource));

        Bitmap bitmapPoi = drawableToBitmap(getResources().getDrawable(R.drawable.marker_poi));
        Bitmap bitmapFocus = drawableToBitmap(getResources().getDrawable(R.drawable.marker_focus));

        // Create Atlas from Bitmaps
        java.util.Map<Object, Bitmap> inputMap = new LinkedHashMap<>();
        java.util.Map<Object, TextureRegion> regionsMap = new LinkedHashMap<>();
        List<TextureAtlas> atlasList = new ArrayList<>();

        inputMap.put("poi", bitmapPoi);
        inputMap.put("focus", bitmapFocus);

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

        List<MarkerItem> pts = new ArrayList<>();

        for (double lat = -90; lat <= 90; lat += 5) {
            for (double lon = -180; lon <= 180; lon += 5)
                pts.add(new MarkerItem(lat + "/" + lon, "", new GeoPoint(lat, lon)));
        }

        mMarkerLayer.addItems(pts);
    }
}
