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
package org.oscim.utils.geom;

/**
 * ported from http://www.flipcode.com/archives/2D_OBB_Intersection.shtml
 */

public class OBB2D {

    /**
     * Vector math for one array
     */
    public static class Vec2 {

        public static void set(float[] v, int pos, float x, float y) {
            v[pos + 0] = x;
            v[pos + 1] = y;
        }

        public static double dot(double[] v, int a, int b) {
            return v[a] * v[b] + v[a + 1] * v[b + 1];
        }

        public final static double lengthSquared(double[] v, int pos) {
            double x = v[pos + 0];
            double y = v[pos + 1];

            return x * x + y * y;
        }

        public final static void normalizeSquared(double[] v, int pos) {
            double x = v[pos + 0];
            double y = v[pos + 1];

            double length = x * x + y * y;

            v[pos + 0] = x / length;
            v[pos + 1] = y / length;
        }

        public final static void normalize(float[] v, int pos) {
            float x = v[pos + 0];
            float y = v[pos + 1];

            double length = Math.sqrt(x * x + y * y);

            v[pos + 0] = (float) (x / length);
            v[pos + 1] = (float) (y / length);
        }

        public final static float length(float[] v, int pos) {
            float x = v[pos + 0];
            float y = v[pos + 1];

            return (float) Math.sqrt(x * x + y * y);
        }

        public final static void add(double[] v, int r, int a, int b) {
            v[r + 0] = v[a + 0] + v[b + 0];
            v[r + 1] = v[a + 1] + v[b + 1];
        }

        public final static void sub(double[] v, int r, int a, int b) {
            v[r + 0] = v[a + 0] - v[b + 0];
            v[r + 1] = v[a + 1] - v[b + 1];
        }

        public final static void mul(double[] v, int pos, float a) {
            v[pos + 0] *= a;
            v[pos + 1] *= a;
        }
    }

    double originX;
    double originY;

    public final double[] vec = new double[4 * 2 + 2 * 2];

    // Corners of the box, where 0 is the lower left.
    //public final float[] corner = new float[ 4 * 2];
    private final static int CORNER_X = 0;
    private final static int CORNER_Y = CORNER_X + 1;
    private final static int CORNER_0 = CORNER_X;
    private final static int CORNER_1 = CORNER_X + 2;
    //private final static int CORNER_2 = CORNER_X + 4;
    private final static int CORNER_3 = CORNER_X + 6;

    // Two edges of the box extended away from origin[CORNER_X + 0].
    //public final float[] axis = new float[2 * 2];
    private final static int AXIS_X = 2 * 4;
    private final static int AXIS_Y = AXIS_X + 1;

    private final static int AXIS_1 = AXIS_X;
    private final static int AXIS_2 = AXIS_X + 2;

    // Returns true if other overlaps one dimension of this.
    private boolean overlaps1Way(OBB2D other) {
        for (int a = 0; a <= 2; a += 2) {
            double ax = vec[AXIS_X + a];
            double ay = vec[AXIS_Y + a];

            // dot product
            double t = ax * other.vec[CORNER_X] + ay * other.vec[CORNER_Y];

            // Find the extent of box 2 on axis a
            double tMin = t;
            double tMax = t;

            for (int c = CORNER_X + 2; c < 8; c += 2) {
                t = ax * other.vec[c] + ay * other.vec[c + 1];

                if (t < tMin)
                    tMin = t;
                else if (t > tMax)
                    tMax = t;
            }

            // We have to subtract off the origin
            // See if [tMin, tMax] intersects [0, 1]
            if (a == 0) {
                if ((tMin > 1 + originX) || (tMax < originX))
                    // There was no intersection along this dimension;
                    // the boxes cannot possibly overlap.
                    return false;
            } else {
                if ((tMin > 1 + originY) || (tMax < originY))
                    return false;
            }
        }

        // There was no dimension along which there is no intersection.
        // Therefore the boxes overlap.
        return true;
    }

    // Updates the axes after the corners move.  Assumes the
    // corners actually form a rectangle.
    private void computeAxes() {
        Vec2.sub(vec, AXIS_1, CORNER_1, CORNER_0);
        Vec2.sub(vec, AXIS_2, CORNER_3, CORNER_0);

        // Make the length of each axis 1/edge length so we know any
        // dot product must be less than 1 to fall within the edge.
        Vec2.normalizeSquared(vec, AXIS_1);
        originX = Vec2.dot(vec, CORNER_0, AXIS_1);

        Vec2.normalizeSquared(vec, AXIS_2);
        originY = Vec2.dot(vec, CORNER_0, AXIS_2);
    }

    //    public OBB2D(float cx, float cy, float w, float h, float angle) {
    //        float rcos = (float) Math.cos(angle);
    //        float rsin = (float) Math.sin(angle);
    //
    //        float[] tmp = new float[4 * 2];
    //        Vec2.set(tmp, 0, rcos, rsin);
    //        Vec2.set(tmp, 1, -rsin, rcos);
    //
    //        Vec2.mul(tmp, 0, w / 2);
    //        Vec2.mul(tmp, 1, h / 2);
    //
    //        Vec2.add(tmp, 2, tmp, 0, tmp, 1);
    //        Vec2.sub(tmp, 3, tmp, 0, tmp, 1);
    //
    //        Vec2.set(tmp, 0, cx, cy);
    //
    //        Vec2.sub(origin, CORNER_X + 0, tmp, 0, tmp, 3);
    //        Vec2.add(origin, CORNER_X + 2, tmp, 0, tmp, 3);
    //        Vec2.add(origin, CORNER_X + 4, tmp, 0, tmp, 2);
    //        Vec2.sub(origin, CORNER_X + 6, tmp, 0, tmp, 2);
    //
    //        computeAxes();
    //    }
    //
    public OBB2D() {

    }

    public OBB2D(double cx, double cy, double width, double height, double acos, double asin) {

        double vx = (double) acos * width / 2;
        double vy = (double) asin * width / 2;

        double ux = (double) -asin * height / 2;
        double uy = (double) acos * height / 2;

        vec[CORNER_X] = cx + (vx - ux);
        vec[CORNER_Y] = cy + (vy - uy);

        vec[CORNER_X + 2] = cx + (-vx - ux);
        vec[CORNER_Y + 2] = cy + (-vy - uy);

        vec[CORNER_X + 4] = cx + (-vx + ux);
        vec[CORNER_Y + 4] = cy + (-vy + uy);

        vec[CORNER_X + 6] = cx + (vx + ux);
        vec[CORNER_Y + 6] = cy + (vy + uy);

        computeAxes();
    }

    public void setNormalized(double cx, double cy, double vx, double vy, double width, double height,
                              double dy) {
        double ux = -vy;
        double uy = vx;

        double hw = width / 2;
        double hh = height / 2;

        if (dy != 0) {
            cx += vx * dy + vy * dy;
            cy += -vy * dy + vx * dy;
        }

        vx *= hw;
        vy *= hw;

        ux *= hh;
        uy *= hh;

        vec[CORNER_X] = cx - (vx - ux);
        vec[CORNER_Y] = cy - (vy - uy);

        vec[CORNER_X + 2] = cx + (vx - ux);
        vec[CORNER_Y + 2] = cy + (vy - uy);

        vec[CORNER_X + 4] = cx + (vx + ux);
        vec[CORNER_Y + 4] = cy + (vy + uy);

        vec[CORNER_X + 6] = cx - (vx + ux);
        vec[CORNER_Y + 6] = cy - (vy + uy);

        computeAxes();
    }

    public void set(double cx, double cy, double dx, double dy, double width, double height) {
        double vx = cx - dx;
        double vy = cy - dy;

        double a = (double) Math.sqrt(vx * vx + vy * vy);
        vx /= a;
        vy /= a;

        double hw = width / 2;
        double hh = height / 2;

        double ux = vy * hh;
        double uy = -vx * hh;

        vx *= hw;
        vy *= hw;

        vec[CORNER_X] = cx - vx - ux;
        vec[CORNER_Y] = cy - vy - uy;

        vec[CORNER_X + 2] = cx + vx - ux;
        vec[CORNER_Y + 2] = cy + vy - uy;

        vec[CORNER_X + 4] = cx + vx + ux;
        vec[CORNER_Y + 4] = cy + vy + uy;

        vec[CORNER_X + 6] = cx - vx + ux;
        vec[CORNER_Y + 6] = cy - vy + uy;

        computeAxes();
    }

    public OBB2D(float cx, float cy, float dx, float dy, float width, float height) {

        float vx = cx - dx;
        float vy = cy - dy;

        float a = (float) Math.sqrt(vx * vx + vy * vy);
        vx /= a;
        vy /= a;

        float hw = width / 2;
        float hh = height / 2;

        float ux = vy * hh;
        float uy = -vx * hh;

        vx *= hw;
        vy *= hw;

        vec[CORNER_X + 0] = cx - vx - ux;
        vec[CORNER_Y + 0] = cy - vy - uy;

        vec[CORNER_X + 2] = cx + vx - ux;
        vec[CORNER_Y + 2] = cy + vy - uy;

        vec[CORNER_X + 4] = cx + vx + ux;
        vec[CORNER_Y + 4] = cy + vy + uy;

        vec[CORNER_X + 6] = cx - vx + ux;
        vec[CORNER_Y + 6] = cy - vy + uy;

        computeAxes();
    }

    // width and height must be > 1 I guess
    public OBB2D(float cx, float cy, float width, float height) {

        float hw = width / 2;
        float hh = height / 2;

        vec[CORNER_X] = cx - hw;
        vec[CORNER_Y] = cy - hh;

        vec[CORNER_X + 2] = cx - hw;
        vec[CORNER_Y + 2] = cy + hh;

        vec[CORNER_X + 4] = cx + hw;
        vec[CORNER_Y + 4] = cy + hh;

        vec[CORNER_X + 6] = cx + hw;
        vec[CORNER_Y + 6] = cy - hh;

        vec[AXIS_X + 0] = 0;
        vec[AXIS_X + 1] = 1 / height;

        vec[AXIS_X + 2] = 1 / width;
        vec[AXIS_X + 3] = 0;

        vec[0] = vec[CORNER_Y] * vec[AXIS_Y];
        vec[1] = vec[CORNER_X + 2] * vec[AXIS_X + 2];
    }

    // Returns true if the intersection of the boxes is non-empty.
    public boolean overlaps(OBB2D other) {
        return overlaps1Way(other) && other.overlaps1Way(this);
    }
}
