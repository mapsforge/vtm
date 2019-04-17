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
package org.oscim.test;

import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint.Cap;
import org.oscim.core.GeometryBuffer;
import org.oscim.gdx.lwjgl.GdxMapApp;
import org.oscim.layers.GenericLayer;
import org.oscim.renderer.BucketRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.MapRenderer;
import org.oscim.renderer.bucket.HairLineBucket;
import org.oscim.renderer.bucket.LineBucket;
import org.oscim.renderer.bucket.PolygonBucket;
import org.oscim.theme.styles.AreaStyle;
import org.oscim.theme.styles.LineStyle;
import org.oscim.theme.styles.LineStyle.LineBuilder;

public class HairLineTest extends GdxMapApp {

    static GeometryBuffer createLine(float r) {
        GeometryBuffer in = new GeometryBuffer(100, 2);

        for (int j = 0; j <= 12; j += 3) {
            in.startLine();
            for (int i = 0; i <= 120; i++) {
                double rad = Math.toRadians(i * 3);
                in.addPoint((float) (Math.cos(rad) * (r + j)), (float) (Math.sin(rad) * (r + j)));
            }
        }
        return in;
    }

    static class Renderer extends BucketRenderer {
        boolean init;
        LineBuilder<?> l = LineStyle.builder()
                .color(Color.WHITE)
                .strokeWidth(1.5f)
                .cap(Cap.ROUND);

        HairLineBucket ll = buckets.addHairLineBucket(1, l.build());

        //LineLayer ll = layers.addLineLayer(1, new LineStyle(Color.fade(Color.CYAN, 0.6f), 2.5f));
        LineStyle style = new LineStyle(Color.fade(Color.MAGENTA, 0.6f), 2.5f);

        HairLineBucket l1 = buckets.addHairLineBucket(2, style);

        //style = new LineStyle(Color.fade(Color.LTGRAY, 0.8f), 1.5f);
        LineBucket l2 = buckets.addLineBucket(3, style);

        PolygonBucket pl = buckets.addPolygonBucket(4, AreaStyle.builder()
                .color(Color.BLUE)
                //.outline(Color.CYAN, 1)
                .build());

        @Override
        public boolean setup() {
            //ll.roundCap = true;
            return super.setup();
        }

        @Override
        public void update(GLViewport v) {
            if (!init) {
                mMapPosition.copy(v.pos);
                init = true;
                GeometryBuffer g;

                for (int i = 105; i < 160; i += 30) {

                    g = createLine(i);

                    ll.addLine(g);

                    //g.translate(10, 10);
                    //l1.addLine(g);

                    //        int o = 0;
                    //        for (int k = 0; k < g.index.length && g.index[k] >= 0; k++) {
                    //
                    //            for (int j = 0; j < g.index[k];)
                    //                ll.addPoint(g.points[o + j++], g.points[o + j++]);
                    //
                    //            o += g.index[k];
                    //        }
                }
                g = new GeometryBuffer(4, 2);
                g.clear();
                g.startPolygon();
                g.addPoint(-100, -100);
                g.addPoint(100, -100);
                g.addPoint(100, 100);
                g.addPoint(-100, 100);
                g.translate(100, 100);
                l2.addLine(g);
                pl.addPolygon(g);

                compile();
            }
        }
    }

    @Override
    public void createLayers() {
        MapRenderer.setBackgroundColor(Color.BLACK);
        mMap.layers().add(new GenericLayer(mMap, new Renderer()));

    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new HairLineTest());

    }
}
