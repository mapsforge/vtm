/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2019 devemux86
 * Copyright 2019 Gustl22
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
import org.oscim.backend.CanvasAdapter;
import org.oscim.core.MapPosition;
import org.oscim.core.MercatorProjection;
import org.oscim.layers.GroupLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.renderer.BitmapRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.scalebar.*;
import org.oscim.theme.IRenderTheme;
import org.oscim.theme.ThemeLoader;
import org.oscim.theme.internal.VtmThemes;

public class SimpleMapActivity extends BaseMapActivity {

    BuildingLayer mBuildingLayer;
    private boolean mShadow;

    public SimpleMapActivity() {
        this(false);
    }

    public SimpleMapActivity(boolean shadow) {
        super();
        mShadow = shadow;
    }

    public SimpleMapActivity(boolean shadow, int contentView) {
        super(contentView);
        mShadow = shadow;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createLayers();
    }

    void createLayers() {
        GroupLayer groupLayer = new GroupLayer(mMap);
        mBuildingLayer = new BuildingLayer(mMap, mBaseLayer, false, mShadow);
        groupLayer.layers.add(mBuildingLayer);
        groupLayer.layers.add(new LabelLayer(mMap, mBaseLayer));
        mMap.layers().add(groupLayer);

        DefaultMapScaleBar mapScaleBar = new DefaultMapScaleBar(mMap);
        mapScaleBar.setScaleBarMode(DefaultMapScaleBar.ScaleBarMode.BOTH);
        mapScaleBar.setDistanceUnitAdapter(MetricUnitAdapter.INSTANCE);
        mapScaleBar.setSecondaryDistanceUnitAdapter(ImperialUnitAdapter.INSTANCE);
        mapScaleBar.setScaleBarPosition(MapScaleBar.ScaleBarPosition.BOTTOM_LEFT);

        MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mMap, mapScaleBar);
        BitmapRenderer renderer = mapScaleBarLayer.getRenderer();
        renderer.setPosition(GLViewport.Position.BOTTOM_LEFT);
        renderer.setOffset(5 * CanvasAdapter.getScale(), 0);
        mMap.layers().add(mapScaleBarLayer);

        mMap.setTheme(VtmThemes.MOTORIDER);
    }

    void runTheMonkey() {
        themes[0] = ThemeLoader.load(VtmThemes.DEFAULT);
        themes[1] = ThemeLoader.load(VtmThemes.OSMARENDER);
        themes[2] = ThemeLoader.load(VtmThemes.TRONRENDER);
        loooop(1);
    }

    IRenderTheme[] themes = new IRenderTheme[3];

    // Stress testing
    void loooop(final int i) {
        final long time = (long) (500 + Math.random() * 1000);
        mMapView.postDelayed(new Runnable() {
            @Override
            public void run() {

                mMapView.map().setTheme(themes[i]);

                MapPosition p = new MapPosition();
                if (i == 1) {
                    mMapView.map().getMapPosition(p);
                    p.setScale(4);
                    mMapView.map().animator().animateTo(time, p);
                } else {
                    //mMapView.map().setMapPosition(p);

                    p.setScale(2 + (1 << (int) (Math.random() * 13)));
                    //p.setX((p.getX() + (Math.random() * 4 - 2) / p.getScale()));
                    //p.setY((p.getY() + (Math.random() * 4 - 2) / p.getScale()));
                    p.setX(MercatorProjection.longitudeToX(Math.random() * 180));
                    p.setY(MercatorProjection.latitudeToY(Math.random() * 60));

                    p.setTilt((float) (Math.random() * 60));
                    p.setBearing((float) (Math.random() * 360));
                    p.setRoll((float) (Math.random() * 360));
                    //mMapView.map().setMapPosition(p);

                    mMapView.map().animator().animateTo(time, p);
                }
                loooop((i + 1) % 2);

            }
        }, time);
    }
}
