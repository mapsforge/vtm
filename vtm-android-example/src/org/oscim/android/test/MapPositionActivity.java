/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
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

import android.os.Bundle;
import android.util.Log;

import org.oscim.core.MapPosition;
import org.oscim.core.MercatorProjection;

public class MapPositionActivity extends SimpleMapActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        runTest();
    }

    void runTest() {

        // 1 - ask for a bearing
        int bearing = 180;
        animateToBearing(bearing);

        // 2 - ask for a new location
        double latitude = Math.random() * 60;
        double longitude = Math.random() * 180;
        animateToLocation(latitude, longitude);

        // If animations have merged, final bearing should 180
        checkThatAnimationsHaveMerged(bearing);
    }

    void animateToLocation(final double latitude, final double longitude) {
        mMapView.postDelayed(new Runnable() {
            @Override
            public void run() {
                MapPosition p = mMapView.map().getMapPosition();
                p.setX(MercatorProjection.longitudeToX(longitude));
                p.setY(MercatorProjection.latitudeToY(latitude));
                mMapView.map().animator().animateTo(1000, p);
            }
        }, 1000);
    }

    void animateToBearing(final int bearing) {
        mMapView.postDelayed(new Runnable() {
            @Override
            public void run() {
                MapPosition p = mMapView.map().getMapPosition();
                p.setBearing(bearing);
                mMapView.map().animator().animateTo(1000, p);
            }
        }, 500);
    }

    void checkThatAnimationsHaveMerged(final int bearing) {
        mMapView.postDelayed(new Runnable() {
            @Override
            public void run() {
                MapPosition p = mMapView.map().getMapPosition();
                if (p.getBearing() != bearing) {
                    Log.e(MapPositionActivity.class.getName(), "Bearing is not correct (expected:" + bearing + ", actual:" + p.getBearing() + ")");
                }
            }
        }, 3000);
    }
}
