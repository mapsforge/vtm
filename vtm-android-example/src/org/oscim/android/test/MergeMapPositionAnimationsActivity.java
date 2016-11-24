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
import org.oscim.layers.GroupLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.renderer.BitmapRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.scalebar.DefaultMapScaleBar;
import org.oscim.scalebar.ImperialUnitAdapter;
import org.oscim.scalebar.MapScaleBar;
import org.oscim.scalebar.MapScaleBarLayer;
import org.oscim.scalebar.MetricUnitAdapter;
import org.oscim.theme.IRenderTheme;
import org.oscim.theme.ThemeLoader;
import org.oscim.theme.VtmThemes;

import java.text.DecimalFormat;

public class MergeMapPositionAnimationsActivity extends BaseMapActivity {
    private DefaultMapScaleBar mapScaleBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GroupLayer groupLayer = new GroupLayer(mMap);
        groupLayer.layers.add(new BuildingLayer(mMap, mBaseLayer));
        groupLayer.layers.add(new LabelLayer(mMap, mBaseLayer));
        mMap.layers().add(groupLayer);

        mapScaleBar = new DefaultMapScaleBar(mMap);
        mapScaleBar.setScaleBarMode(DefaultMapScaleBar.ScaleBarMode.BOTH);
        mapScaleBar.setDistanceUnitAdapter(MetricUnitAdapter.INSTANCE);
        mapScaleBar.setSecondaryDistanceUnitAdapter(ImperialUnitAdapter.INSTANCE);
        mapScaleBar.setScaleBarPosition(MapScaleBar.ScaleBarPosition.BOTTOM_LEFT);

        MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mMap, mapScaleBar);
        BitmapRenderer renderer = mapScaleBarLayer.getRenderer();
        renderer.setPosition(GLViewport.Position.BOTTOM_LEFT);
        renderer.setOffset(5 * getResources().getDisplayMetrics().density, 0);
        mMap.layers().add(mapScaleBarLayer);

        mMap.setTheme(VtmThemes.DEFAULT);

        runTest();
    }

    @Override
    protected void onDestroy() {
        mapScaleBar.destroy();

        super.onDestroy();
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
                    Log.e(MergeMapPositionAnimationsActivity.class.getName(), "Bearing is not correct (expected:" + bearing + ", actual:" + p.getBearing() + ")");
                }
            }
        }, 3000);
    }
}
