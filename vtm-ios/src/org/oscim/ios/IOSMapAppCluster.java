/*
 * Copyright 2017 Longri
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
package org.oscim.ios;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.oscim.backend.AssetAdapter;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.GLAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.gdx.GdxAssets;
import org.oscim.gdx.GdxMap;
import org.oscim.ios.backend.IosGL;
import org.oscim.ios.backend.IosGraphics;
import org.oscim.layers.GroupLayer;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.markercluster.ClusteredItemizedLayer;
import org.oscim.layers.markercluster.ClusteredMarkerItem;
import org.oscim.layers.markercluster.ClusteredMarkerRenderer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Map;
import org.oscim.renderer.BitmapRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.scalebar.*;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

import java.io.InputStream;
import java.util.ArrayList;

public class IOSMapAppCluster extends GdxMap {

    final static int COUNT = 5;
    final float STEP = 100f / 110000f; // roughly 100 meters

    public static void init() {
        // init globals
        IosGraphics.init();
        GdxAssets.init("assets/");
        GLAdapter.init(new IosGL());
    }

    @Override
    public void createLayers() {
        Map map = getMap();

        VectorTileLayer l = map.setBaseMap(new OSciMap4TileSource());

        GroupLayer groupLayer = new GroupLayer(mMap);
        groupLayer.layers.add(new BuildingLayer(map, l));
        groupLayer.layers.add(new LabelLayer(map, l));
        map.layers().add(groupLayer);

        DefaultMapScaleBar mapScaleBar = new DefaultMapScaleBar(mMap);
        mapScaleBar.setScaleBarMode(DefaultMapScaleBar.ScaleBarMode.BOTH);
        mapScaleBar.setDistanceUnitAdapter(MetricUnitAdapter.INSTANCE);
        mapScaleBar.setSecondaryDistanceUnitAdapter(ImperialUnitAdapter.INSTANCE);
        mapScaleBar.setScaleBarPosition(MapScaleBar.ScaleBarPosition.BOTTOM_LEFT);

        MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mMap, mapScaleBar);
        BitmapRenderer renderer = mapScaleBarLayer.getRenderer();
        renderer.setPosition(GLViewport.Position.BOTTOM_LEFT);
        renderer.setOffset(5, 0);
        map.layers().add(mapScaleBarLayer);

        map.setTheme(VtmThemes.DEFAULT);
        map.setMapPosition(53.075, 8.808, 1 << 17);

        addClusterLayer();
    }


    private void addClusterLayer() {

        MapPosition pos = new MapPosition();
        mMap.getMapPosition(pos);
        pos.setZoomLevel(2);
        mMap.setMapPosition(pos);


        // Create the itemized layer and assign cluster icon style
        ClusteredItemizedLayer layer = new ClusteredItemizedLayer(
                mMap,
                null,
                new ClusteredMarkerRenderer.ClusterStyle(0xffffffff, 0xff123456)
        );

        // add it top the map
        mMap.layers().add(layer);

        FileHandle fileHandle = Gdx.files.internal("./res/marker_poi.png");
        InputStream inputStream = fileHandle.read();
        if (inputStream == null) throw new RuntimeException("Source not found");
        Bitmap bmpPoi = CanvasAdapter.decodeBitmap(inputStream);

        MarkerSymbol symbol = new MarkerSymbol(bmpPoi, MarkerSymbol.HotspotPlace.CENTER);


        // create some markers spaced STEP degrees
        GeoPoint center = pos.getGeoPoint();
        ArrayList<ClusteredMarkerItem> list = new ArrayList<>();

        for (int x = -COUNT; x < COUNT; x++) {
            for (int y = -COUNT; y < COUNT; y++) {

                double random = STEP * Math.random() * 2;

                ClusteredMarkerItem item = new ClusteredMarkerItem(
                        "Demo Marker " + ((x * COUNT) + y),
                        "Your typical marker in your typical map",
                        new GeoPoint(center.getLatitude() + y * STEP + random, center.getLongitude() + x * STEP + random)
                );

                item.setMarker(symbol);
                list.add(item);
            }
        }

        // add'em all at once
        layer.addItems(list);
    }
}
