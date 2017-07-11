/*
 * Copyright 2012, 2013 Hannes Janetzek
 * Copyright 2017 Wolfgang Schramm

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
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.map.Map;
import org.oscim.utils.async.SimpleWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LabelLayer extends Layer implements Map.UpdateListener, TileManager.Listener {

    static final Logger log = LoggerFactory.getLogger(LabelLayer.class);

    public final static String LABEL_DATA = LabelLayer.class.getName();

    private final static long MAX_RELABEL_DELAY = 100;

    private final LabelPlacement mLabelPlacer;
    private final Worker mWorker;

    public LabelLayer(Map map, VectorTileLayer l) {
        super(map);
        l.getManager().events.bind(this);
        l.addHook(new LabelTileLoaderHook());

        mLabelPlacer = new LabelPlacement(map, l.tileRenderer());
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

    public void clearLabels() {
        mWorker.cancel(true);
    }

    public void update() {
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

        if (event == Map.POSITION_EVENT && isEnabled())
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
            if (tile.isVisible)
                mWorker.submit(MAX_RELABEL_DELAY / 4);
            //log.debug("tile loaded: {}", tile);
        } else if (e == TileManager.TILE_REMOVED) {
            //log.debug("tile removed: {}", tile);
        }
    }

}
