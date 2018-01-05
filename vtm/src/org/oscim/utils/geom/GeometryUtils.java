/*
 * Copyright 2012, 2013 Hannes Janetzek
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
package org.oscim.utils.geom;

import java.util.ArrayList;
import java.util.List;

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

    public static float area(float ax, float ay, float bx, float by, float cx, float cy) {

        float area = ((ax - cx) * (by - cy)
                - (bx - cx) * (ay - cy));

        return (area < 0 ? -area : area) * 0.5f;
    }

    public static float area(float[] a, int p1, int p2, int p3) {

        float area = ((a[p1] - a[p3]) * (a[p2 + 1] - a[p3 + 1])
                - (a[p2] - a[p3]) * (a[p1 + 1] - a[p3 + 1]));

        return (area < 0 ? -area : area) * 0.5f;
    }

    /**
     * @param v1 the first normalized vector
     * @param v2 the second normalized vector
     * @return the bisection of the to vectors
     */
    public static float[] bisectionNorm2D(float[] v1, float[] v2) {
        // Normalize vectors
        /*float absBA = (float) Math.sqrt(v1[0] * v1[0] + v1[1] * v1[1]);
        float absBC = (float) Math.sqrt(v2[0] * v2[0] + v2[1] * v2[1]);
        v1[0] /= absBA;
        v1[1] /= absBA;
        v2[0] /= absBC;
        v2[1] /= absBC;*/

        float[] bisection = new float[2];
        bisection[0] = v1[0] + v2[0];
        bisection[1] = v1[1] + v2[1];
        if (bisection[0] == 0 && bisection[1] == 0) {
            // 90 degree to v1
            bisection[0] = v1[1];
            bisection[1] = v1[0];
        }
        return bisection;
    }

    /**
     * @param a first vector
     * @param b second vector (same length as a)
     * @return a - b
     */
    public static float[] diffVec(float[] a, float[] b) {
        float[] diff = new float[a.length];
        for (int i = 0; i < a.length; i++) {
            diff[i] = a[i] - b[i];
        }
        return diff;
    }

    /**
     * @param a first vector
     * @param b second vector (same length as a)
     * @return a + b
     */
    public static float[] sumVec(float[] a, float[] b) {
        float[] add = new float[a.length];
        for (int i = 0; i < a.length; i++) {
            add[i] = b[i] + a[i];
        }
        return add;
    }

    public static float squaredDistance(float[] p, int a, int b) {
        return (p[a] - p[b]) * (p[a] - p[b]) + (p[a + 1] - p[b + 1]) * (p[a + 1] - p[b + 1]);
    }

    /**
     * square distance from a point a to a segment b,c
     */
    // modified from https://github.com/ekeneijeoma/simplify-java
    public static float squareSegmentDistance(float[] p, int a, int b, int c) {
        float x = p[b];
        float y = p[b + 1];

        float dx = p[c] - x;
        float dy = p[c + 1] - y;

        if (dx != 0 || dy != 0) {
            float t = ((p[a] - x) * dx + (p[a + 1] - y) * dy) / (dx * dx + dy * dy);

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

    public static double distance(float[] p, int a, int b) {
        float dx = p[a] - p[b];
        float dy = p[a + 1] - p[b + 1];
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * @param a point a[x,y]
     * @param b point b[x,y]
     * @return the distance between a and b.
     */
    public static double distance2D(float[] a, float[] b) {
        float dx = a[0] - b[0];
        float dy = a[1] - b[1];
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double dotProduct(float[] p, int a, int b, int c) {

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

    /**
     * @param a first vector
     * @param b second vector
     * @return the dot product
     */
    public static float dotProduct(float[] a, float[] b) {
        float dp = 0;
        for (int i = 0; i < a.length; i++) {
            dp += a[i] * b[i];
        }
        return dp;
    }

    /**
     * @param pA position vector of A
     * @param vA direction vector of A
     * @param pB position vector of B
     * @param vB direction vector of B
     * @return the intersection point
     */
    public static float[] intersectionLines2D(float[] pA, float[] vA, float[] pB, float[] vB) {
        // pA + ldA * vA == pB + ldB * vB;

        float det = vB[0] * vA[1] - vB[1] * vA[0];
        if (det == 0) {
            // log.debug(vA.toString() + "and" + vB.toString() + "do not intersect");
            return null;
        }
        float lambA = ((pB[1] - pA[1]) * vB[0] - (pB[0] - pA[0]) * vB[1]) / det;

        float[] intersection = new float[2];
        intersection[0] = pA[0] + lambA * vA[0];
        intersection[1] = pA[1] + lambA * vA[1];

        return intersection;
    }

    /**
     * Calculate intersection of a plane with a line
     *
     * @param pL position vector of line
     * @param vL direction vector of line
     * @param pP position vector of plane
     * @param vP normal vector of plane
     * @return the intersection point
     */
    public static float[] intersectionLinePlane(float[] pL, float[] vL, float[] pP, float[] vP) {
        float det = dotProduct(vL, vP);
        if (det == 0) return null;
        float phi = dotProduct(diffVec(pP, pL), vP) / det;
        return sumVec(scale(vL, phi), pL);
    }

    /**
     * @return more than 0 if points of triangle are clockwise, 0 if triangle is a line,
     * else less than 0.
     */
    public static float isTrisClockwise(float[] pA, float[] pB, float[] pC) {
        float v = (pB[0] - pA[0]) * (pB[1] + pA[1]);
        v += (pC[0] - pB[0]) * (pC[1] + pB[1]);
        v += (pA[0] - pC[0]) * (pA[1] + pC[1]);
        return v;
    }

    /**
     * Calculate the normalized direction vectors of point list (polygon)
     *
     * @param points     the list of 2D points
     * @param outLengths the optional list to store lengths of vectors
     * @return the normalized direction vectors
     */
    public static List<float[]> normalizedVectors2D(List<float[]> points, List<Float> outLengths) {
        List<float[]> normVectors = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {
            float[] pA = points.get(i);
            float[] pB = points.get((i + 1) % points.size());

            float[] vBA = diffVec(pB, pA);

            // Get length of AB
            float length = (float) Math.sqrt(vBA[0] * vBA[0] + vBA[1] * vBA[1]);
            if (outLengths != null)
                outLengths.add(length);

            vBA[0] /= length; // Normalize vector
            vBA[1] /= length;

            normVectors.add(vBA);
        }
        return normVectors;
    }

    /**
     * @param pA first point of plane
     * @param pB second point of plane
     * @param pC third point of plane
     * @return the normal of plane
     */
    public static float[] normalOfPlane(float[] pA, float[] pB, float[] pC, float[] reuse) {
        // Calculate normal for color gradient
        float[] BA = diffVec(pB, pA);
        float[] BC = diffVec(pC, pA);

        // Vector product (c is at right angle to a and b)
        if (reuse == null)
            reuse = new float[3];
        reuse[0] = BA[1] * BC[2] - BA[2] * BC[1];
        reuse[1] = BA[2] * BC[0] - BA[0] * BC[2];
        reuse[2] = BA[0] * BC[1] - BA[1] * BC[0];
        return reuse;
    }

    /**
     * @param v     the vector
     * @param scale the scale
     * @return the scaled vector
     */
    public static float[] scale(float[] v, float scale) {
        float[] scaled = new float[v.length];
        for (int i = 0; i < v.length; i++) {
            scaled[i] = v[i] * scale;
        }
        return scaled;
    }

    public static void main(String[] args) {
        float[] p = {-1, 0, 0, 0, 0, 0};

        for (int i = 0; i < 9; i++) {
            p[4] = (float) Math.cos(Math.toRadians(i * 45));
            p[5] = (float) Math.sin(Math.toRadians(i * 45));
            System.out.println("\n> " + (i * 45) + " " + p[3] + ":" + p[4] + "\n="
                    + dotProduct(p, 0, 2, 4));
        }
    }
}
