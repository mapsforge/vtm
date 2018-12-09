/*
 * Copyright 2014 Hannes Janetzek
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
package org.oscim.layers.vector;

import org.oscim.core.Box;
import org.oscim.core.GeometryBuffer;
import org.oscim.core.MapPosition;
import org.oscim.event.Event;
import org.oscim.layers.Layer;
import org.oscim.map.Map;
import org.oscim.map.Map.UpdateListener;
import org.oscim.map.Viewport;
import org.oscim.renderer.BucketRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.MapRenderer;
import org.oscim.renderer.bucket.RenderBuckets;
import org.oscim.utils.async.SimpleWorker;
import org.oscim.utils.geom.TileClipper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractVectorLayer<T> extends Layer implements UpdateListener {
    public static final Logger log = LoggerFactory.getLogger(AbstractVectorLayer.class);

    protected static final double UNSCALE_COORD = 4;

    // limit coords to maximum resolution of GL.Short
    private static final int MAX_CLIP = (int) (Short.MAX_VALUE / MapRenderer.COORD_SCALE);

    protected final GeometryBuffer mGeom = new GeometryBuffer(128, 4);
    protected final TileClipper mClipper = new TileClipper(-MAX_CLIP, -MAX_CLIP, MAX_CLIP, MAX_CLIP);

    protected final Worker mWorker;
    protected long mUpdateDelay = 50;

    protected boolean mUpdate = true;

    public AbstractVectorLayer(Map map) {
        super(map);
        mWorker = new Worker(mMap);
        mRenderer = new Renderer();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mWorker.cancel(true);
    }

    @Override
    public void onMapEvent(Event e, MapPosition pos) {
        if (mUpdate) {
            mUpdate = false;
            mWorker.submit(0);
        } else if (e == Map.POSITION_EVENT || e == Map.CLEAR_EVENT) {
            /* throttle worker */
            mWorker.submit(mUpdateDelay);
        }
    }

    public void update() {
        mWorker.submit(0);
    }

    abstract protected void processFeatures(Task t, Box b);

    protected static class Task {
        public final RenderBuckets buckets = new RenderBuckets();
        public final MapPosition position = new MapPosition();
    }

    protected class Worker extends SimpleWorker<Task> {

        public Worker(Map map) {
            super(map, 50, new Task(), new Task());
        }

        /**
         * automatically in sync with worker thread
         */
        @Override
        public void cleanup(Task t) {
            if (t.buckets != null)
                t.buckets.clear();
        }

        /**
         * running on worker thread
         */
        @Override
        public boolean doWork(Task t) {

            Box bbox;
            float[] box = new float[8];

            Viewport v = mMap.viewport().getSyncViewport();
            synchronized (v) {
                bbox = v.getBBox(null, 0);
                v.getMapExtents(box, 0);
                v.getMapPosition(t.position);
            }

            /* Hmm what is this for? */
            //    double scale = t.position.scale * Tile.SIZE;
            //    t.position.x = (long) (t.position.x * scale) / scale;
            //    t.position.y = (long) (t.position.y * scale) / scale;

            bbox.map2mercator();

            //    double xmin = bbox.xmin;
            //    double xmax = bbox.xmax;
            //    Box lbox = null;
            //    Box rbox = null;
            //    if (bbox.xmin < -180) {
            //        bbox.xmin = -180;
            //        lbox = new Box(bbox);
            //    }
            //    if (bbox.xmax > 180) {
            //        bbox.xmax = 180;
            //        rbox = new Box(bbox);
            //    }

            processFeatures(t, bbox);

            //if (lbox != null) {
            //    t.position.x += 1;
            //    lbox.xmax = 180;
            //    lbox.xmin = xmin + 180;
            //    processFeatures(t, lbox);
            //    t.position.x -= 1;
            //}
            //
            //if (rbox != null) {
            //    t.position.x -= 1;
            //    rbox.xmin = -180;
            //    rbox.xmax = xmax - 180;
            //    processFeatures(t, rbox);
            //    t.position.x += 1;
            //}

            t.buckets.prepare();

            mMap.render();
            return true;
        }
    }

    public class Renderer extends BucketRenderer {
        MapPosition mTmpPos = new MapPosition();

        public Renderer() {
            mFlipOnDateLine = true;
        }

        @Override
        public void update(GLViewport v) {

            Task t = mWorker.poll();

            if (t == null)
                return;

            mMapPosition.copy(t.position);
            mMapPosition.setScale(mMapPosition.scale / UNSCALE_COORD);

            buckets.setFrom(t.buckets);

            compile();
        }
    }
}
