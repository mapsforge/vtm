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
package org.oscim.layers.tile.vector.labeling;

import org.oscim.backend.canvas.Color;
import org.oscim.core.MapPosition;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.bucket.LineBucket;
import org.oscim.renderer.bucket.RenderBuckets;
import org.oscim.renderer.bucket.TextItem;
import org.oscim.theme.styles.LineStyle;

class Debug {

    private final static double[] mDebugPoints = new double[8];
    // TODO Auto-generated method stub
    static RenderBuckets dbg;

    static void addDebugBox(Label l, TextItem ti, int overlaps, boolean prev,
                            float scale) {

        LineBucket ll;
        if (prev) {
            if (overlaps == 1)
                ll = dbg.getLineBucket(4);
            else
                ll = dbg.getLineBucket(5);

        } else {
            if (ti.width > ti.length * scale) {
                ll = dbg.getLineBucket(1);
                overlaps = 3;
            } else if (overlaps == 1)
                ll = dbg.getLineBucket(0);
            else if (overlaps == 2)
                ll = dbg.getLineBucket(3);
            else
                ll = dbg.getLineBucket(2);
        }
        double[] points = mDebugPoints;
        double width = (ti.x2 - ti.x1) / 2f;
        double height = (ti.y2 - ti.y1) / 2f;
        points[0] = (l.x - width * scale);
        points[1] = (l.y - height * scale);
        points[2] = (l.x + width * scale);
        points[3] = (l.y + height * scale);
        ll.addLine(points, 4, false);

        System.arraycopy(l.bbox.vec, 2, points, 0, 8);
        if (l.bbox != null && overlaps != 3) {
            ll.addLine(points, 8, true);
        }
    }

    static void addDebugLayers(RenderBuckets dbg) {
        int alpha = 0xaaffffff;

        dbg.clear();
        dbg.addLineBucket(0, new LineStyle((Color.BLUE & alpha), 2));
        dbg.addLineBucket(1, new LineStyle((Color.RED & alpha), 2));
        dbg.addLineBucket(3, new LineStyle((Color.YELLOW & alpha), 2));
        dbg.addLineBucket(2, new LineStyle((Color.GREEN & alpha), 2));
        dbg.addLineBucket(4, new LineStyle((Color.CYAN & alpha), 2));
        dbg.addLineBucket(5, new LineStyle((Color.MAGENTA & alpha), 2));
    }

    public static void draw(MapPosition pos, GLViewport m, RenderBuckets layers) {
        //        if (layers.baseLayers != null) {
        //            //setMatrix(pos, m, true);
        //
        //            for (RenderElement l = layers.baseLayers; l != null;) {
        //                if (l.type == RenderElement.POLYGON) {
        //                    l = PolygonLayer.Renderer.draw(pos, l, m, true, 1, false);
        //                } else {
        //                    //float div = (float) (mMapPosition.scale / (1 << pos.zoomLevel));
        //                    l = LineLayer.Renderer.draw(layers, l, pos, m, div);
        //                }
        //            }
        //        }
    }

}
