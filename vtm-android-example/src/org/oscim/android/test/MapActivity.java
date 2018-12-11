/*
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

import android.app.Activity;
import android.os.Bundle;

import org.oscim.android.MapPreferences;
import org.oscim.android.MapView;
import org.oscim.map.Map;

public class MapActivity extends Activity {
    MapView mMapView;
    Map mMap;
    MapPreferences mPrefs;

    protected final int mContentView;

    public MapActivity(int contentView) {
        mContentView = contentView;
    }

    public MapActivity() {
        this(R.layout.activity_map);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mContentView);

        setTitle(getClass().getSimpleName());

        mMapView = (MapView) findViewById(R.id.mapView);
        mMap = mMapView.map();
        mPrefs = new MapPreferences(MapActivity.class.getName(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPrefs.load(mMapView.map());
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        mPrefs.save(mMapView.map());
        mMapView.onPause();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();

        super.onDestroy();
    }
}
