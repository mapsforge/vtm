/*
 * Copyright 2016 devemux86
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
import org.oscim.core.GeoPoint;
import org.oscim.event.Gesture;
import org.oscim.event.GestureListener;
import org.oscim.event.MotionEvent;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.Layer;
import org.oscim.layers.TileGridLayer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.map.Map;
import org.oscim.tiling.source.bitmap.DefaultSources;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.oscim.layers.marker.MarkerSymbol.HotspotPlace;

public class MarkerLayerTest extends GdxMapApp implements ItemizedLayer.OnItemGestureListener<MarkerItem> {

    protected static final boolean BILLBOARDS = true;
    protected MarkerSymbol mFocusMarker;
    private ItemizedLayer<MarkerItem> markerLayer;

    @Override
    public void createLayers() {
        BitmapTileLayer bitmapLayer = new BitmapTileLayer(mMap, DefaultSources.STAMEN_TONER.build());
        bitmapLayer.tileRenderer().setBitmapAlpha(0.5f);
        mMap.setBaseMap(bitmapLayer);

        // Map events receiver
        mMap.layers().add(new MapEventsReceiver(mMap));

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

        markerLayer = new ItemizedLayer<>(mMap, new ArrayList<MarkerItem>(), symbol, this);
        mMap.layers().add(markerLayer);

        List<MarkerItem> pts = new ArrayList<>();
        for (double lat = -90; lat <= 90; lat += 5) {
            for (double lon = -180; lon <= 180; lon += 5)
                pts.add(new MarkerItem(lat + "/" + lon, "", new GeoPoint(lat, lon)));
        }
        markerLayer.addItems(pts);

        mMap.layers().add(new TileGridLayer(mMap));
    }

    Timer timer;
    int markerCount = 0;

    @Override
    public boolean onItemSingleTapUp(int index,final MarkerItem item) {
        if (item.getMarker() == null) {
            item.setMarker(mFocusMarker);
            markerCount++;
            final AtomicInteger rotValue = new AtomicInteger(0);
            if(timer != null) timer.cancel();
            timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    float value = (float)(rotValue.incrementAndGet() * 10);
                    item.setRotation(value);
                    if(rotValue.get()>36) rotValue.set(0);
                    markerLayer.update();
                    mMap.updateMap(true);
                }
            };
            timer.schedule(timerTask,1000,1000);

        }else {
            item.setMarker(null);
            markerCount--;
            if(timer != null && markerCount == 0) timer.cancel();
        }

        System.out.println("Marker tap " + item.getTitle());
        return true;
    }

    @Override
    public boolean onItemLongPress(int index,final MarkerItem item) {
        if (item.getMarker() == null) {
            item.setMarker(mFocusMarker);
            markerCount++;
            final AtomicInteger rotValue = new AtomicInteger(0);
            if(timer != null) timer.cancel();
            timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    float value = (float)(rotValue.incrementAndGet() * 10);
                    item.setRotation(value);
                    if(rotValue.get()>36) rotValue.set(0);
                    markerLayer.update();
                    mMap.updateMap(true);
                }
            };
            timer.schedule(timerTask,300,300);
        }else{
            item.setMarker(null);
            markerCount--;
            if(timer != null && markerCount == 0) timer.cancel();
        }

        System.out.println("Marker long press " + item.getTitle());
        return true;
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new MarkerLayerTest());
    }

    protected class MapEventsReceiver extends Layer implements GestureListener {

        MapEventsReceiver(Map map) {
            super(map);
        }

        @Override
        public boolean onGesture(Gesture g, MotionEvent e) {
            if (g instanceof Gesture.Tap) {
                GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
                System.out.println("Map tap " + p);
                return true;
            }
            if (g instanceof Gesture.LongPress) {
                GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
                System.out.println("Map long press " + p);
                return true;
            }
            return false;
        }
    }
}
