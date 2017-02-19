/*
 * Copyright 2012, 2013 Hannes Janetzek
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

public final class GeometryUtils {

    private GeometryUtils() {
    }

    /**
     * Test if point x/y is in polygon defined by vertices[offset ...
     * offset+length]
     * <p/>
     * -- from www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
     * <p/>
     * If there is only one connected component, then it is optional to repeat
     * the first vertex at the end. It's also optional to surround the component
     * with zero vertices.
     * <p/>
     * The polygon may contain multiple separate components, and/or holes,
     * provided that you separate the components and holes with a (0,0) vertex,
     * as follows.
     * <li>First, include a (0,0) vertex.
     * <li>Then include the first component' vertices, repeating its first
     * vertex after the last vertex.
     * <li>Include another (0,0) vertex.
     * <li>Include another component or hole, repeating its first vertex after
     * the last vertex.
     * <li>Repeat the above two steps for each component and hole.
     * <li>Include a final (0,0) vertex.
     * <p/>
     * Each component or hole's vertices may be listed either clockwise or
     * counter-clockwise.
     */
    public static boolean pointInPoly(float x, float y, float[] vertices,
                                      int length, int offset) {

        int end = (offset + length);

        boolean inside = false;
        for (int i = offset, j = (end - 2); i < end; j = i, i += 2) {
            if (((vertices[i + 1] > y) != (vertices[j + 1] > y)) &&
                    (x < (vertices[j] - vertices[i]) * (y - vertices[i + 1])
                            / (vertices[j + 1] - vertices[i + 1]) + vertices[i]))
                inside = !inside;
        }
        return inside;
    }

    public static double area(double ax, double ay, double bx, double by, double cx, double cy) {

        double area = ((ax - cx) * (by - cy)
                - (bx - cx) * (ay - cy));

        return (area < 0 ? -area : area) * 0.5f;
    }

    public static double area(double[] a, int p1, int p2, int p3) {

        double area = ((a[p1] - a[p3]) * (a[p2 + 1] - a[p3 + 1])
                - (a[p2] - a[p3]) * (a[p1 + 1] - a[p3 + 1]));

        return (area < 0 ? -area : area) * 0.5f;
    }

    public static float squaredDistance(float[] p, int a, int b) {
        return (p[a] - p[b]) * (p[a] - p[b]) + (p[a + 1] - p[b + 1]) * (p[a + 1] - p[b + 1]);
    }

    /**
     * square distance from a point a to a segment b,c
     */
    // modified from https://github.com/ekeneijeoma/simplify-java
    public static double squareSegmentDistance(double[] p, int a, int b, int c) {
        double x = p[b];
        double y = p[b + 1];

        double dx = p[c] - x;
        double dy = p[c + 1] - y;

        if (dx != 0 || dy != 0) {
            double t = ((p[a] - x) * dx + (p[a + 1] - y) * dy) / (dx * dx + dy * dy);

            if (t > 1) {
                x = p[c];
                y = p[c + 1];
            } else if (t > 0) {
                x += dx * t;
                y += dy * t;
            }
        }

        dx = p[a] - x;
        dy = p[a + 1] - y;

        return dx * dx + dy * dy;
    }

    public static double distance(double[] p, int a, int b) {
        double dx = p[a] - p[b];
        double dy = p[a + 1] - p[b + 1];
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double dotProduct(double[] p, int a, int b, int c) {

        double ux = (p[b] - p[a]);
        double uy = (p[b + 1] - p[a + 1]);
        double ab = Math.sqrt(ux * ux + uy * uy);
        double vx = (p[b] - p[c]);
        double vy = (p[b + 1] - p[c + 1]);
        double bc = Math.sqrt(vx * vx + vy * vy);

        double d = ab * bc;

        if (d <= 0)
            return 0;

        double dotp = (ux * -vx + uy * -vy) / d;

        if (dotp > 1)
            dotp = 1;
        else if (dotp < -1)
            dotp = -1;

        return dotp;
    }

    public static void main(String[] args) {
        double[] p = {-1, 0, 0, 0, 0, 0};

        for (int i = 0; i < 9; i++) {
            p[4] = (float) Math.cos(Math.toRadians(i * 45));
            p[5] = (float) Math.sin(Math.toRadians(i * 45));
            System.out.println("\n> " + (i * 45) + " " + p[3] + ":" + p[4] + "\n="
                    + dotProduct(p, 0, 2, 4));
        }
    }
}
