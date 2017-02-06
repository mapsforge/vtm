/*
 * Copyright 2016 Mathieu De Brito
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
package org.oscim.android.test;

import android.os.Bundle;
import android.widget.Toast;

import org.oscim.core.MapPosition;
import org.oscim.core.MercatorProjection;

/**
 * Test consecutive map position animations.
 */
public class MapPositionActivity extends SimpleMapActivity {

    // Reuse MapPosition instance
    private final MapPosition mapPosition = new MapPosition();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        runTest();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* ignore saved position */
        mMap.setMapPosition(0, 0, 1 << 2);
    }

    private void animateToBearing(final float bearing) {
        mMap.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMap.getMapPosition(mapPosition);
                mapPosition.setBearing(bearing);
                mMap.animator().animateTo(1000, mapPosition);
            }
        }, 500);
    }

    private void animateToLocation(final double latitude, final double longitude) {
        mMap.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMap.getMapPosition(true, mapPosition);
                mapPosition.setPosition(latitude, longitude);
                mMap.animator().animateTo(1000, mapPosition);
            }
        }, 1000);
    }

    private void runTest() {
        // 1 - ask for a bearing
        final float bearing = 180;
        animateToBearing(bearing);

        // 2 - ask for a new location
        double latitude = Math.random() * MercatorProjection.LATITUDE_MAX;
        double longitude = Math.random() * MercatorProjection.LONGITUDE_MAX;
        animateToLocation(latitude, longitude);

        // If animations were merged, final bearing should be 180°
        mMap.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMap.getMapPosition(mapPosition);
                Toast.makeText(MapPositionActivity.this, "Bearing expected: " + bearing + ", got: " + mapPosition.getBearing(), Toast.LENGTH_LONG).show();
            }
        }, 3000);
    }
}
