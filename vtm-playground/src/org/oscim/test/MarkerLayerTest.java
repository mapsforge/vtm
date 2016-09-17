/*
 * Copyright 2016 devemux86
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
import org.oscim.core.GeoPoint;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.TileGridLayer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.tiling.source.bitmap.DefaultSources;

import java.util.ArrayList;
import java.util.List;

import static org.oscim.layers.marker.MarkerSymbol.*;

public class MarkerLayerTest extends GdxMapApp implements ItemizedLayer.OnItemGestureListener<MarkerItem> {

    private static final boolean BILLBOARDS = true;
    private MarkerSymbol mFocusMarker;

    @Override
    public void createLayers() {
        BitmapTileLayer bitmapLayer = new BitmapTileLayer(mMap, DefaultSources.STAMEN_TONER.build());
        bitmapLayer.tileRenderer().setBitmapAlpha(0.5f);
        mMap.setBaseMap(bitmapLayer);

        mMap.setMapPosition(0, 0, 1 << 2);

        Bitmap bitmapPoi = CanvasAdapter.decodeBitmap(getClass().getResourceAsStream("/res/marker_poi.png"));
        MarkerSymbol symbol;
        if (BILLBOARDS)
            symbol = new MarkerSymbol(bitmapPoi, HotspotPlace.BOTTOM_CENTER);
        else
            symbol = new MarkerSymbol(bitmapPoi, HotspotPlace.CENTER, false);

        Bitmap bitmapFocus = CanvasAdapter.decodeBitmap(getClass().getResourceAsStream("/res/marker_focus.png"));
        if (BILLBOARDS)
            mFocusMarker = new MarkerSymbol(bitmapFocus, HotspotPlace.BOTTOM_CENTER);
        else
            mFocusMarker = new MarkerSymbol(bitmapFocus, HotspotPlace.CENTER, false);

        ItemizedLayer<MarkerItem> markerLayer = new ItemizedLayer<>(mMap, new ArrayList<MarkerItem>(), symbol, this);
        mMap.layers().add(markerLayer);

        List<MarkerItem> pts = new ArrayList<>();
        for (double lat = -90; lat <= 90; lat += 5) {
            for (double lon = -180; lon <= 180; lon += 5)
                pts.add(new MarkerItem(lat + "/" + lon, "", new GeoPoint(lat, lon)));
        }
        markerLayer.addItems(pts);

        mMap.layers().add(new TileGridLayer(mMap));
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerItem item) {
        if (item.getMarker() == null)
            item.setMarker(mFocusMarker);
        else
            item.setMarker(null);

        System.out.println(item.getTitle());
        return true;
    }

    @Override
    public boolean onItemLongPress(int index, MarkerItem item) {
        return false;
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new MarkerLayerTest(), null, 400);
    }
}
