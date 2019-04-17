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
import org.oscim.core.GeometryBuffer;
import org.oscim.core.Point;
import org.oscim.gdx.lwjgl.GdxMapApp;
import org.oscim.layers.GenericLayer;
import org.oscim.renderer.BucketRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.bucket.LineBucket;
import org.oscim.theme.styles.LineStyle;
import org.oscim.utils.geom.BezierPath;

import java.util.List;

public class BezierTest extends GdxMapApp {

    @Override
    public void createLayers() {
        mMap.layers().add(new GenericLayer(mMap, new BezierPathLayer()));
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new BezierTest());
    }

    static class BezierPathLayer extends BucketRenderer {

        public BezierPathLayer() {
            mMapPosition.scale = 0;

            GeometryBuffer g = new GeometryBuffer(100, 1);
            g.startLine();

            Point[] pts = new Point[10];
            for (int i = 0; i < 10; i++) {
                pts[i] = new Point(i * 3, (i * i) % 3 * 4);
                pts[i].x *= 10;
                pts[i].y *= 10;
                // System.out.println(pts[i]);
                g.addPoint(pts[i]);
            }
            LineBucket ll = buckets.addLineBucket(0, new LineStyle(Color.BLUE, 2f));
            ll.addLine(g);

            List<Point> ctrl = BezierPath.cubicSplineControlPoints(pts, 0.1f);

            g.clear();
            g.startLine();
            Point p0 = pts[0];

            for (int j = 1, k = 0; j < pts.length; j++) {
                Point p1 = ctrl.get(k++);
                Point p2 = ctrl.get(k++);
                Point p3 = pts[j];
                System.out.println(">>> " + p1 + " " + p2);
                for (int i = 0; i < 10; i++) {
                    double mu = (i / 10f);
                    Point p = BezierPath.cubicBezier(p0, p1, p2, p3, mu);
                    g.addPoint(p);
                    System.out.println(mu + " " + p);
                }
                p0 = p3;
            }
            ll = buckets.addLineBucket(1, new LineStyle(Color.CYAN, 2f));
            ll.addLine(g);

        }

        public synchronized void clear() {
            buckets.clear();
            setReady(false);
        }

        @Override
        public synchronized void update(GLViewport v) {

            if (mMapPosition.scale == 0)
                mMapPosition.copy(v.pos);

            if (!isReady()) {
                compile();
            }
        }
    }
}
