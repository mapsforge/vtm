/*
 * Copyright 2016 Marvin W
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
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Map;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

public class MultiMapViewActivity extends Activity {
    private MapView mMapView1, mMapView2;
    private MapPreferences mPrefs1, mPrefs2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_multi);

        setTitle(getClass().getSimpleName());

        // 1st map view
        mMapView1 = (MapView) findViewById(R.id.mapView1);
        Map map1 = mMapView1.map();
        mPrefs1 = new MapPreferences(MultiMapViewActivity.class.getName() + "1", this);
        TileSource tileSource1 = OSciMap4TileSource.builder()
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();
        VectorTileLayer baseLayer1 = map1.setBaseMap(tileSource1);
        map1.layers().add(new BuildingLayer(map1, baseLayer1));
        map1.layers().add(new LabelLayer(map1, baseLayer1));
        map1.setTheme(VtmThemes.DEFAULT);

        // 2nd map view
        mMapView2 = (MapView) findViewById(R.id.mapView2);
        Map map2 = mMapView2.map();
        mPrefs2 = new MapPreferences(MultiMapViewActivity.class.getName() + "2", this);
        TileSource tileSource2 = OSciMap4TileSource.builder()
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();
        VectorTileLayer baseLayer2 = map2.setBaseMap(tileSource2);
        map2.layers().add(new BuildingLayer(map2, baseLayer2));
        map2.layers().add(new LabelLayer(map2, baseLayer2));
        map2.setTheme(VtmThemes.DEFAULT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPrefs1.load(mMapView1.map());
        mMapView1.onResume();

        mPrefs2.load(mMapView2.map());
        mMapView2.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mMapView1.onPause();
        mPrefs1.save(mMapView1.map());

        mMapView2.onPause();
        mPrefs2.save(mMapView2.map());
    }
}
