/*
 * Copyright 2016-2017 devemux86
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

import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.marker.MarkerItem;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class RotateMarkerLayerTest extends MarkerLayerTest {

    private Timer timer;
    private int markerCount = 0;

    private void itemEvent(final MarkerItem item) {
        if (item.getMarker() == null) {
            item.setMarker(mFocusMarker);
            markerCount++;
            final AtomicInteger rotValue = new AtomicInteger(0);
            if (timer != null)
                timer.cancel();
            timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    float value = (float) (rotValue.incrementAndGet() * 10);
                    item.setRotation(value);
                    if (rotValue.get() > 36)
                        rotValue.set(0);
                    mMarkerLayer.update();
                    mMap.updateMap(true);
                }
            };
            timer.schedule(timerTask, 1000, 1000);

        } else {
            item.setMarker(null);
            markerCount--;
            if (timer != null && markerCount == 0)
                timer.cancel();
        }
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerItem item) {
        itemEvent(item);

        System.out.println("Marker tap " + item.getTitle());
        return true;
    }

    @Override
    public boolean onItemLongPress(int index, MarkerItem item) {
        itemEvent(item);

        System.out.println("Marker long press " + item.getTitle());
        return true;
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new RotateMarkerLayerTest());
    }
}
