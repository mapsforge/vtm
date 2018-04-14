/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2018 devemux86
 * Copyright 2018 Gustl22
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
package org.oscim.gdx;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import org.oscim.layers.TileGridLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Layers;
import org.oscim.map.Map;
import org.oscim.renderer.MapRenderer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.utils.Parameters;

public abstract class GdxMap implements ApplicationListener {

    protected Map mMap;
    protected GestureDetector mGestureDetector;

    protected MapRenderer mMapRenderer;

    public GdxMap() {
    }

    protected void initDefaultLayers(TileSource tileSource, boolean tileGrid, boolean labels,
                                     boolean buildings) {
        Layers layers = mMap.layers();

        if (tileSource != null) {
            VectorTileLayer mapLayer = mMap.setBaseMap(tileSource);
            mMap.setTheme(VtmThemes.DEFAULT);

            if (buildings)
                layers.add(new BuildingLayer(mMap, mapLayer));

            if (labels)
                layers.add(new LabelLayer(mMap, mapLayer));
        }

        if (tileGrid)
            layers.add(new TileGridLayer(mMap));
    }

    @Override
    public void create() {
        mMap = new MapAdapter();
        mMapRenderer = new MapRenderer(mMap);

        Gdx.graphics.setContinuousRendering(false);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        mMap.viewport().setViewSize(w, h);
        mMapRenderer.onSurfaceCreated();
        mMapRenderer.onSurfaceChanged(w, h);

        InputMultiplexer mux = new InputMultiplexer();
        if (!Parameters.MAP_EVENT_LAYER2) {
            mGestureDetector = new GestureDetector(new GestureHandlerImpl(mMap));
            mux.addProcessor(mGestureDetector);
        }
        mux.addProcessor(new InputHandler(this));
        mux.addProcessor(new MotionHandler(mMap));

        Gdx.input.setInputProcessor(mux);

        createLayers();
    }

    protected void createLayers() {
    }

    @Override
    public void dispose() {

    }

    /* private */ boolean mRenderWait;
    /* private */ boolean mRenderRequest;
    /* private */ boolean mUpdateRequest;

    @Override
    public void render() {
        // Workaround for flickering
        /*if (!mRenderRequest)
            return;*/

        mMapRenderer.onDrawFrame();
    }

    @Override
    public void resize(int w, int h) {
        mMap.viewport().setViewSize(w, h);
        mMapRenderer.onSurfaceChanged(w, h);
        mMap.render();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    protected boolean onKeyDown(int keycode) {
        return false;
    }

    public Map getMap() {
        return mMap;
    }

    class MapAdapter extends Map {

        @Override
        public int getWidth() {
            return Gdx.graphics.getWidth();
        }

        @Override
        public int getHeight() {
            return Gdx.graphics.getHeight();
        }

        @Override
        public int getScreenWidth() {
            return Gdx.graphics.getDisplayMode().width;
        }

        @Override
        public int getScreenHeight() {
            return Gdx.graphics.getDisplayMode().height;
        }

        private final Runnable mRedrawCb = new Runnable() {
            @Override
            public void run() {
                prepareFrame();
                Gdx.graphics.requestRendering();
            }
        };

        @Override
        public void updateMap(boolean forceRender) {
            synchronized (mRedrawCb) {
                if (!mRenderRequest) {
                    mRenderRequest = true;
                    Gdx.app.postRunnable(mRedrawCb);
                } else {
                    mRenderWait = true;
                }
            }
        }

        @Override
        public void render() {
            synchronized (mRedrawCb) {
                mRenderRequest = true;
                if (mClearMap)
                    updateMap(false);
                else {
                    Gdx.graphics.requestRendering();
                }
            }
        }

        @Override
        public boolean post(Runnable runnable) {
            Gdx.app.postRunnable(runnable);
            return true;
        }

        @Override
        public boolean postDelayed(final Runnable action, long delay) {
            Timer.schedule(new Task() {
                @Override
                public void run() {
                    action.run();
                }
            }, delay / 1000f);
            return true;
        }

        @Override
        public void beginFrame() {
        }

        @Override
        public void doneFrame(boolean animate) {
            synchronized (mRedrawCb) {
                mRenderRequest = false;
                if (animate || mRenderWait) {
                    mRenderWait = false;
                    updateMap(true);
                }
            }
        }
    }
}
