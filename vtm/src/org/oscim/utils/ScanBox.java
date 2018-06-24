/*
 * Copyright 2012 Hannes Janetzek
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General License for more details.
 *
 * You should have received a copy of the GNU Lesser General License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.oscim.utils;

import org.oscim.core.Tile;

/**
 * Scan-line fill algorithm to retrieve tile-coordinates from
 * Viewport.
 * <p/>
 * ScanBox is used to calculate tile coordinates that intersect the box (or
 * trapezoid) which is the projection of screen bounds to the map. Usage:
 * <p/>
 * <pre>
 * Viewport.getMapViewProjection(box)
 *     ScanBox sb = new ScanBox(){
 *          protected void setVisible(int y, int x1, int x2) {
 *          }
 *    };
 * sb.scan(pos.x, pos.y, pos.scale, * zoomLevel, coords);
 * </pre>
 * <p/>
 * where zoomLevel is the zoom-level for which tile coordinates should be
 * calculated.
 */
public abstract class ScanBox {

    private final float[] mBox = new float[8];

    private float[] transScale(double x, double y, double scale, int zoom, float[] box) {
        scale *= Tile.SIZE;

        //double curScale = Tile.SIZE * scale;
        double div = scale / (1 << zoom);

        x *= scale;
        y *= scale;

        for (int i = 0; i < 8; i += 2) {
            mBox[i + 0] = (float) ((x + box[i + 0]) / div);
            mBox[i + 1] = (float) ((y + box[i + 1]) / div);
        }
        return mBox;
    }

    /* ported from Polymaps: Layer.js */

    static class Edge {
        float x0, y0, x1, y1, dx, dy;

        void set(float x0, float y0, float x1, float y1) {
            if (y0 <= y1) {
                this.x0 = x0;
                this.y0 = y0;
                this.x1 = x1;
                this.y1 = y1;
            } else {
                this.x0 = x1;
                this.y0 = y1;
                this.x1 = x0;
                this.y1 = y0;
            }
            this.dx = this.x1 - this.x0;
            this.dy = this.y1 - this.y0;
        }
    }

    private Edge ab = new Edge();
    private Edge bc = new Edge();
    private Edge ca = new Edge();

    private int xmin, xmax;

    protected int mZoom;

    protected abstract void setVisible(int y, int x1, int x2);

    public void scan(double x, double y, double scale, int zoom, float[] box) {
        mZoom = zoom;
        // this does not modify 'box' parameter
        box = transScale(x, y, scale, zoom, box);

        // clip result to min/max as steep angles
        // cause overshooting in x direction.
        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;

        for (int i = 0; i < 8; i += 2) {
            float xx = box[i];
            if (xx > max)
                max = xx;
            if (xx < min)
                min = xx;
        }

        max = (float) Math.ceil(max);
        min = (float) Math.floor(min);
        if (min == max)
            max++;

        xmin = (int) min;
        xmax = (int) max;

        // top-right -> top-left       🡐
        ab.set(box[0], box[1], box[2], box[3]);
        // top-left ->  bottom-left    🡓
        bc.set(box[2], box[3], box[4], box[5]);
        // bottom-left -> top-right    🡕
        ca.set(box[4], box[5], box[0], box[1]);

        scanTriangle();

        // top-right -> bottom-left    🡗
        ab.set(box[0], box[1], box[4], box[5]);
        // bottom-left -> bottom-right 🡒
        bc.set(box[4], box[5], box[6], box[7]);
        // bottom-right -> top-right   🡑
        ca.set(box[6], box[7], box[0], box[1]);

        scanTriangle();
    }

    private void scanTriangle() {

        // sort so that ca.dy > bc.dy > ab.dy
        if (ab.dy > bc.dy) {
            Edge t = ab;
            ab = bc;
            bc = t;
        }
        if (ab.dy > ca.dy) {
            Edge t = ab;
            ab = ca;
            ca = t;
        }
        if (bc.dy > ca.dy) {
            Edge t = bc;
            bc = ca;
            ca = t;
        }

        // shouldnt be possible, anyway
        if (ca.dy == 0)
            return;

        if (ab.dy > 0.0)
            scanSpans(ca, ab);

        if (bc.dy > 0.0)
            scanSpans(ca, bc);
    }

    private void scanSpans(Edge e0, Edge e1) {

        // scan the y-range of the edge with less dy
        int y0 = (int) Math.max(0, Math.floor(e1.y0));
        int y1 = (int) Math.min((1 << mZoom), Math.ceil(e1.y1));

        // sort edge by x-coordinate
        if (e0.x0 == e1.x0 && e0.y0 == e1.y0) {
            // bottom-flat
            if (e0.x0 + e1.dy / e0.dy * e0.dx < e1.x1) {
                Edge t = e0;
                e0 = e1;
                e1 = t;
            }
        } else {
            // top-flat
            if (e0.x1 - e1.dy / e0.dy * e0.dx < e1.x0) {
                Edge t = e0;
                e0 = e1;
                e1 = t;
            }
        }

        float m0 = e0.dx / e0.dy;
        float m1 = e1.dx / e1.dy;

        // e0 goes to the right, e1 to the left
        int d0 = e0.dx > 0 ? 1 : 0; // use y + 1 to compute x0
        int d1 = e1.dx < 0 ? 1 : 0; // use y + 1 to compute x1
        float dy;

        for (int y = y0; y < y1; y++) {

            dy = d0 + y - e0.y0;
            if (dy > e0.dy)
                dy = e0.dy;

            int x0 = (int) Math.ceil(e0.x0 + m0 * dy);

            dy = d1 + y - e1.y0;
            if (dy > e1.dy)
                dy = e1.dy;

            int x1 = (int) Math.floor(e1.x0 + m1 * dy);

            if (x1 < xmin)
                x1 = xmin;

            if (x0 > xmax)
                x0 = xmax;

            if (x1 < x0)
                setVisible(y, x1, x0);
        }
    }
}
