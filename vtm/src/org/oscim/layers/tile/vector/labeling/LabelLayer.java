/*
 * Copyright 2012, 2013 Hannes Janetzek
 * Copyright 2017 Wolfgang Schramm
 * Copyright 2017 devemux86
 * Copyright 2017 Andrey Novikov
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
package org.oscim.layers.tile.vector.labeling;

import org.oscim.core.MapPosition;
import org.oscim.event.Event;
import org.oscim.layers.Layer;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.TileManager;
import org.oscim.layers.tile.ZoomLimiter;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.map.Map;
import org.oscim.utils.async.SimpleWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LabelLayer extends Layer implements Map.UpdateListener, TileManager.Listener,
        ZoomLimiter.IZoomLimiter {

    static final Logger log = LoggerFactory.getLogger(LabelLayer.class);

    static final String LABEL_DATA = LabelLayer.class.getName();

    private static final long MAX_RELABEL_DELAY = 100;

    /* Set to maximum "zoom-min" value for labels in render themes */
    public static final int ZOOM_LIMIT = 18;

    private final LabelPlacement mLabelPlacer;
    private final Worker mWorker;
    private final ZoomLimiter mZoomLimiter;

    public LabelLayer(Map map, VectorTileLayer l) {
        this(map, l, new LabelTileLoaderHook());
    }

    public LabelLayer(Map map, VectorTileLayer l, VectorTileLayer.TileLoaderThemeHook h) {
        super(map);
        l.getManager().events.bind(this);
        l.addHook(h);

        mZoomLimiter = new ZoomLimiter(l.getManager(), map.viewport().getMinZoomLevel(),
                map.viewport().getMaxZoomLevel(), ZOOM_LIMIT);

        mLabelPlacer = new LabelPlacement(map, l.tileRenderer(), mZoomLimiter);
        mWorker = new Worker(map);
        mRenderer = new TextRenderer(mWorker);
    }

    class Worker extends SimpleWorker<LabelTask> {

        public Worker(Map map) {
            super(map, 50, new LabelTask(), new LabelTask());
        }

        @Override
        public boolean doWork(LabelTask t) {

            if (mLabelPlacer.updateLabels(t)) {
                mMap.render();
                return true;
            }

            return false;
        }

        @Override
        public void cleanup(LabelTask t) {
        }

        @Override
        public void finish() {
            mLabelPlacer.cleanup();
        }

        public synchronized boolean isRunning() {
            return mRunning;
        }
    }

    @Override
    public void addZoomLimit() {
        mZoomLimiter.addZoomLimit();
    }

    @Override
    public void removeZoomLimit() {
        mZoomLimiter.removeZoomLimit();
    }

    public void clearLabels() {
        mWorker.cancel(true);
    }

    public void update() {
        if (!isEnabled())
            return;

        mWorker.submit(MAX_RELABEL_DELAY);
    }

    @Override
    public void onDetach() {
        mWorker.cancel(true);
        super.onDetach();
    }

    @Override
    public void onMapEvent(Event event, MapPosition mapPosition) {

        if (event == Map.CLEAR_EVENT)
            mWorker.cancel(true);

        if (!isEnabled())
            return;

        if (event == Map.POSITION_EVENT)
            mWorker.submit(MAX_RELABEL_DELAY);
    }

    //    @Override
    //    public void onMotionEvent(MotionEvent e) {
    //        //    int action = e.getAction() & MotionEvent.ACTION_MASK;
    //        //    if (action == MotionEvent.ACTION_POINTER_DOWN) {
    //        //        multi++;
    //        //        mTextRenderer.hold(true);
    //        //    } else if (action == MotionEvent.ACTION_POINTER_UP) {
    //        //        multi--;
    //        //        if (multi == 0)
    //        //            mTextRenderer.hold(false);
    //        //    } else if (action == MotionEvent.ACTION_CANCEL) {
    //        //        multi = 0;
    //        //        log.debug("cancel " + multi);
    //        //        mTextRenderer.hold(false);
    //        //    }
    //    }

    @Override
    public void onTileManagerEvent(Event e, MapTile tile) {
        if (e == TileManager.TILE_LOADED) {
            if (tile.isVisible && isEnabled())
                mWorker.submit(MAX_RELABEL_DELAY / 4);
            //log.debug("tile loaded: {}", tile);
        } else if (e == TileManager.TILE_REMOVED) {
            //log.debug("tile removed: {}", tile);
        }
    }

}
