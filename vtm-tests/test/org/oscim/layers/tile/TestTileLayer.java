/*
 * Copyright 2013 Hannes Janetzek
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
package org.oscim.layers.tile;

import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint.Cap;
import org.oscim.core.GeometryBuffer;
import org.oscim.core.Tile;
import org.oscim.map.Map;
import org.oscim.renderer.bucket.LineBucket;
import org.oscim.renderer.bucket.RenderBuckets;
import org.oscim.theme.styles.LineStyle;

import java.util.logging.Logger;

public class TestTileLayer extends TileLayer {
    private static final Logger log = Logger.getLogger(TestTileLayer.class.getName());

    public TestTileLayer(Map map) {
        super(map,
                new TileManager(map, 10),
                new VectorTileRenderer());
    }

    @Override
    protected TestTileLoader createLoader() {
        return new TestTileLoader(this);
    }

    static class TestTileLoader extends TileLoader {

        public TestTileLoader(TileLayer tileLayer) {
            super(tileLayer.getManager());
        }

        GeometryBuffer mGeom = new GeometryBuffer(128, 16);
        LineStyle mLineStyle = new LineStyle(Color.BLUE, 2f, Cap.ROUND);

        @Override
        public boolean loadTile(MapTile tile) {
            log.fine("load tile " + tile);
            RenderBuckets buckets = new RenderBuckets();
            tile.data = buckets;

            LineBucket lb = buckets.getLineBucket(0);
            lb.line = mLineStyle;
            lb.scale = 2;

            int m = 20;
            int s = Tile.SIZE - m * 2;
            GeometryBuffer g = mGeom;

            g.clear();
            g.startLine();
            g.addPoint(m, m);
            g.addPoint(m, s);
            g.addPoint(s, s);
            g.addPoint(s, m);
            g.addPoint(m, m);

            lb.addLine(g);

            return true;
        }

        @Override
        public void dispose() {
        }

        @Override
        public void cancel() {
        }

    }
}
