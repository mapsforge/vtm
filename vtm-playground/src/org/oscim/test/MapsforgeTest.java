/*
 * Copyright 2016-2022 devemux86
 * Copyright 2018-2019 Gustl22
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
package org.oscim.test;

import org.oscim.core.BoundingBox;
import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.event.Event;
import org.oscim.gdx.GdxMapApp;
import org.oscim.gdx.poi3d.Poi3DLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.buildings.S3DBLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Map;
import org.oscim.renderer.BitmapRenderer;
import org.oscim.renderer.ExtrusionRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.scalebar.*;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.oscim.tiling.source.mapfile.MultiMapFileTileSource;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapsforgeTest extends GdxMapApp {

    private static final boolean SHADOWS = false;

    private final List<File> mapFiles;
    private final boolean poi3d;
    private final boolean s3db;

    MapsforgeTest(List<File> mapFiles) {
        this(mapFiles, false, false);
    }

    MapsforgeTest(List<File> mapFiles, boolean s3db, boolean poi3d) {
        this.mapFiles = mapFiles;
        this.s3db = s3db;
        this.poi3d = poi3d;
    }

    @Override
    public void createLayers() {
        MultiMapFileTileSource tileSource = new MultiMapFileTileSource();
        for (File mapFile : mapFiles) {
            MapFileTileSource mapFileTileSource = new MapFileTileSource();
            mapFileTileSource.setMapFile(mapFile.getAbsolutePath());
            tileSource.add(mapFileTileSource);
        }
        tileSource.setDeduplicate(true);
        //tileSource.setPreferredLanguage("en");

        VectorTileLayer l = mMap.setBaseMap(tileSource);
        loadTheme(null);

        BuildingLayer buildingLayer = s3db ? new S3DBLayer(mMap, l, SHADOWS) : new BuildingLayer(mMap, l, false, SHADOWS);
        mMap.layers().add(buildingLayer);

        if (poi3d)
            mMap.layers().add(new Poi3DLayer(mMap, l));

        mMap.layers().add(new LabelLayer(mMap, l));

        DefaultMapScaleBar mapScaleBar = new DefaultMapScaleBar(mMap);
        mapScaleBar.setScaleBarMode(DefaultMapScaleBar.ScaleBarMode.BOTH);
        mapScaleBar.setDistanceUnitAdapter(MetricUnitAdapter.INSTANCE);
        mapScaleBar.setSecondaryDistanceUnitAdapter(ImperialUnitAdapter.INSTANCE);
        mapScaleBar.setScaleBarPosition(MapScaleBar.ScaleBarPosition.BOTTOM_LEFT);

        MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mMap, mapScaleBar);
        BitmapRenderer renderer = mapScaleBarLayer.getRenderer();
        renderer.setPosition(GLViewport.Position.BOTTOM_LEFT);
        renderer.setOffset(5, 0);
        mMap.layers().add(mapScaleBarLayer);

        MapPosition pos = MapPreferences.getMapPosition();
        BoundingBox bbox = tileSource.getBoundingBox();
        if (pos == null || !bbox.contains(pos.getGeoPoint())) {
            pos = new MapPosition();
            pos.setByBoundingBox(bbox, Tile.SIZE * 4, Tile.SIZE * 4);
        }
        mMap.setMapPosition(pos);

        if (SHADOWS) {
            final ExtrusionRenderer extrusionRenderer = buildingLayer.getExtrusionRenderer();
            mMap.events.bind(new Map.UpdateListener() {
                Calendar date = Calendar.getInstance();
                long prevTime = System.currentTimeMillis();

                @Override
                public void onMapEvent(Event e, MapPosition mapPosition) {
                    long curTime = System.currentTimeMillis();
                    int diff = (int) (curTime - prevTime);
                    prevTime = curTime;
                    date.add(Calendar.MILLISECOND, diff * 60 * 60); // Every second equates to one hour

                    //extrusionRenderer.getSun().setProgress((curTime % 2000) / 1000f);
                    extrusionRenderer.getSun().setProgress(date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), date.get(Calendar.SECOND));
                    extrusionRenderer.getSun().updatePosition();
                    extrusionRenderer.getSun().updateColor(); // only relevant for shadow implementation

                    mMap.updateMap(true);
                }
            });
        }
    }

    @Override
    public void dispose() {
        MapPreferences.saveMapPosition(mMap.getMapPosition());
        super.dispose();
    }

    static List<File> getMapFiles(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("missing argument: <mapFile>");
        }

        List<File> result = new ArrayList<>();
        for (String arg : args) {
            File mapFile = new File(arg);
            if (!mapFile.exists()) {
                System.err.println("file does not exist: " + mapFile);
            } else if (!mapFile.isFile()) {
                System.err.println("not a file: " + mapFile);
            } else if (!mapFile.canRead()) {
                System.err.println("cannot read file: " + mapFile);
            } else
                result.add(mapFile);
        }
        return result;
    }

    void loadTheme(final String styleId) {
        mMap.setTheme(VtmThemes.DEFAULT);
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new MapsforgeTest(getMapFiles(args)));
    }
}
