/*
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
package org.oscim.test.renderer;

import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint.Cap;
import org.oscim.gdx.GdxMap;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.GenericLayer;
import org.oscim.renderer.BucketRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.atlas.TextureAtlas;
import org.oscim.renderer.atlas.TextureAtlas.Rect;
import org.oscim.renderer.atlas.TextureAtlas.Slot;
import org.oscim.renderer.bucket.LineBucket;
import org.oscim.renderer.bucket.TextBucket;
import org.oscim.renderer.bucket.TextItem;
import org.oscim.theme.styles.LineStyle;
import org.oscim.theme.styles.TextStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class AtlasTest extends GdxMap {

    @Override
    protected void createLayers() {
        mMap.setMapPosition(0, 0, 1 << 4);
        mMap.layers().add(new GenericLayer(mMap, new AtlasRenderLayer()));
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new AtlasTest());
    }

    static class AtlasRenderLayer extends BucketRenderer {

        Logger log = LoggerFactory.getLogger(AtlasRenderLayer.class);

        public AtlasRenderLayer() {

            TextureAtlas mAtlas = TextureAtlas.create(2048, 2048, 1);

            TextBucket tl = new TextBucket();
            TextStyle t = TextStyle.builder().fontSize(20).color(Color.BLACK).build();
            buckets.set(tl);

            LineBucket ll = buckets.getLineBucket(0);
            ll.line = new LineStyle(Color.BLUE, 3, Cap.BUTT);
            ll.scale = 1f;

            LineBucket ll2 = buckets.getLineBucket(1);
            ll2.line = new LineStyle(Color.RED, 3, Cap.BUTT);
            ll2.scale = 1f;

            LineBucket ll3 = buckets.getLineBucket(2);
            ll3.line = new LineStyle(Color.GREEN, 3, Cap.BUTT);
            ll3.scale = 1f;

            double[] points = new double[10];

            for (int i = 0; i < 400; i++) {
                int w = (int) (20 + Math.random() * 256);
                int h = (int) (20 + Math.random() * 56);
                Rect r = mAtlas.getRegion(w, h);
                if (r == null) {
                    log.debug("no space left");
                    continue;
                }
                r.x += 1;
                r.y += 1;

                points[0] = r.x;
                points[1] = r.y;
                points[2] = r.x + (r.w - 2);
                points[3] = r.y;
                points[4] = r.x + (r.w - 2);
                points[5] = r.y + (r.h - 2);
                points[6] = r.x;
                points[7] = r.y + (r.h - 2);
                points[8] = r.x;
                points[9] = r.y;
                ll.addLine(points, 10, false);

                r.x += 1;
                r.y += 1;
                points[0] = r.x;
                points[1] = r.y;
                points[2] = r.x + (w - 4);
                points[3] = r.y;
                points[4] = r.x + (w - 4);
                points[5] = r.y + (h - 4);
                points[6] = r.x;
                points[7] = r.y + (h - 4);
                points[8] = r.x;
                points[9] = r.y;

                log.debug("add region: " + Arrays.toString(points));
                ll2.addLine(points, 10, false);

                TextItem ti = TextItem.pool.get();
                ti.set(r.x + r.w / 2, r.y + r.h / 2, "" + i, t);
                ti.x1 = 0;
                ti.y1 = 1; // (short) (size / 2);
                ti.x2 = 1; // (short) size;
                ti.y2 = 1;
                tl.addText(ti);
            }

            for (Slot s = mAtlas.mSlots; s != null; s = s.next) {
                points[0] = s.x;
                points[1] = s.y;
                points[2] = s.x + s.w;
                points[3] = s.y;
                points[4] = s.x + s.w;
                points[5] = 2048;
                points[6] = s.x;
                points[7] = 2048;
                points[8] = s.x;
                points[9] = s.y;

                ll3.addLine(points, 10, false);
            }

            tl.prepare();
            //tl.labels = TextItem.pool.releaseAll(tl.labels);
        }

        boolean initial = true;

        @Override
        public void update(GLViewport v) {

            if (initial) {
                mMapPosition.copy(v.pos);
                initial = false;

                compile();
            }
        }
    }
}
