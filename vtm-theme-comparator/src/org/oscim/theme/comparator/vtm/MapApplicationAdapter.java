/*
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
package org.oscim.theme.comparator.vtm;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.input.GestureDetector;

import org.oscim.core.MapPosition;
import org.oscim.event.Event;
import org.oscim.gdx.GestureHandlerImpl;
import org.oscim.gdx.MotionHandler;
import org.oscim.layers.AbstractMapEventLayer;
import org.oscim.layers.GroupLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Map;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.MapRenderer;
import org.oscim.scalebar.DefaultMapScaleBar;
import org.oscim.scalebar.ImperialUnitAdapter;
import org.oscim.scalebar.MapScaleBar;
import org.oscim.scalebar.MapScaleBarLayer;
import org.oscim.scalebar.MetricUnitAdapter;
import org.oscim.theme.ExternalRenderTheme;
import org.oscim.theme.VtmThemes;
import org.oscim.theme.comparator.BothMapPositionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.oscim.backend.GLAdapter.gl;

public class MapApplicationAdapter extends ApplicationAdapter {

    Logger log = LoggerFactory.getLogger(MapApplicationAdapter.class);

    private int x, y, width, height;
    private MapScaleBarLayer mapScaleBarLayer;
    private CenterCrossLayer centerCrossLayer;
    private DefaultMapScaleBar mapScaleBar;
    private BothMapPositionHandler bothMapPositionHandler;
    private AtomicInteger iniCount = new AtomicInteger();
    private AtomicBoolean drawMap = new AtomicBoolean(false);

    MapApplicationAdapter(MapReadyCallback callback) {
        this.callback = callback;
    }

    void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        if (map != null && mapRenderer != null) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    map.viewport().setViewSize(MapApplicationAdapter.this.width, MapApplicationAdapter.this.height);
                    mapRenderer.onSurfaceChanged(MapApplicationAdapter.this.width, MapApplicationAdapter.this.height);
                }
            });
        }
    }

    void setMapPositionHandler(BothMapPositionHandler bothMapPositionHandler) {
        this.bothMapPositionHandler = bothMapPositionHandler;
    }

    void setTheme(String themePath) {
        ExternalRenderTheme externalRenderTheme = new ExternalRenderTheme(themePath);
        try {
            map.setTheme(externalRenderTheme, true);
        } catch (Exception e) {
            log.error("SetTheme", e);
        }
    }

    public interface MapReadyCallback {
        void ready();
    }

    private MapRenderer mapRenderer;
    private MapAdapter map;
    private VectorTileLayer vectorTileLayer;
    private final MapReadyCallback callback;

    @Override
    public void create() {
        map = new MapAdapter() {

            @Override
            public void beginFrame() {
                super.beginFrame();
            }

            @Override
            public void onMapEvent(Event e, final MapPosition mapPosition) {
                super.onMapEvent(e, mapPosition);
                if (e == Map.MOVE_EVENT || e == Map.SCALE_EVENT) {
                    bothMapPositionHandler.mapPositionChangedFromVtmMap(mapPosition);
                }
            }
        };
        mapRenderer = new MapRenderer(map);
        Gdx.graphics.setContinuousRendering(true);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        map.viewport().setViewSize(w, h);
        mapRenderer.onSurfaceCreated();
        mapRenderer.onSurfaceChanged(w, h);

        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(new MotionHandler(map) {
            @Override
            public boolean scrolled(int amount) {
                super.scrolled(amount);
                MapPosition mapPosition = map.getMapPosition();
                int zoomLevel = mapPosition.getZoomLevel() - amount;
                mapPosition.setZoomLevel(zoomLevel);
                map.setMapPosition(mapPosition);
                bothMapPositionHandler.mapPositionChangedFromVtmMap(mapPosition);
                return true;
            }
        });
        mux.addProcessor(new GestureDetector(new GestureHandlerImpl(map)));


        Gdx.input.setInputProcessor(mux);

        mapScaleBar = new DefaultMapScaleBar(map);
        mapScaleBar.setScaleBarMode(DefaultMapScaleBar.ScaleBarMode.BOTH);
        mapScaleBar.setDistanceUnitAdapter(MetricUnitAdapter.INSTANCE);
        mapScaleBar.setSecondaryDistanceUnitAdapter(ImperialUnitAdapter.INSTANCE);
        mapScaleBarLayer = new MapScaleBarLayer(map, mapScaleBar);
        mapScaleBarLayer.getRenderer().setPosition(GLViewport.Position.BOTTOM_LEFT);

        centerCrossLayer = new CenterCrossLayer(map);
    }


    @Override
    public void render() {
        gl.viewport(0, 0, width, height);

        try {
            mapRenderer.onDrawFrame();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (iniCount.get() == 10) {
            mapRenderer.onSurfaceCreated();

            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    drawMap.set(true);
                }
            });
            callback.ready();
            iniCount.incrementAndGet();
        } else {
            iniCount.incrementAndGet();
        }
    }

    @Override
    public void dispose() {
        System.out.print("Dispose");
    }

    void loadMap(File mapFile, File themeFile) {
        FileHandle fileHandle = Gdx.files.absolute(mapFile.getAbsolutePath());
        MapsforgeVectorSingleMap mapLayer = new MapsforgeVectorSingleMap(fileHandle);
        if (vectorTileLayer == null) {
            vectorTileLayer = (VectorTileLayer) mapLayer.getTileLayer(map);
        } else {
            vectorTileLayer.setTileSource(mapLayer.getVectorTileSource());
        }

        AbstractMapEventLayer eventLayer = null;
        if (map.layers().size() > 1) {
            map.layers().clear();
            eventLayer = map.getEventLayer();
        }

        map.layers().add(vectorTileLayer);
        map.setTheme(VtmThemes.OSMARENDER);
        if (eventLayer != null) map.layers().add(eventLayer);
        map.layers().add(new BuildingLabelLayer(map, vectorTileLayer));
        map.layers().add(centerCrossLayer);
        map.layers().add(mapScaleBarLayer);
    }

    void setCoordinate(double latitude, double longitude, byte zoomLevel) {
        map.setMapPosition(latitude, longitude, 1 << zoomLevel);
        mapScaleBar.setScaleBarPosition(MapScaleBar.ScaleBarPosition.BOTTOM_CENTER);
    }

    public static final class BuildingLabelLayer extends GroupLayer {
        final BuildingLayer buildingLayer;

        BuildingLabelLayer(Map map, VectorTileLayer vectorTileLayer) {
            super(map);
            this.buildingLayer = new BuildingLayer(map, vectorTileLayer);
            this.layers.add(this.buildingLayer);
            this.layers.add(new LabelLayer(map, vectorTileLayer));
        }
    }
}
