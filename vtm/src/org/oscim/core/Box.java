/*
 * Copyright 2013 Hannes Janetzek
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
package org.oscim.core;

/**
 * The Classic Box.
 */
public class Box {

    public double xmin;
    public double xmax;
    public double ymin;
    public double ymax;

    /**
     * Instantiates a new Box with all values being 0.
     */
    public Box() {

    }

    /**
     * Instantiates a new Box.
     *
     * @param xmin the min x
     * @param ymin the min y
     * @param xmax the max x
     * @param ymax the max y
     */
    public Box(double xmin, double ymin, double xmax, double ymax) {
        if (xmin > xmax || ymin > ymax)
            throw new IllegalArgumentException("min > max !");
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
    }

    public Box(Box bbox) {
        this.xmin = bbox.xmin;
        this.ymin = bbox.ymin;
        this.xmax = bbox.xmax;
        this.ymax = bbox.ymax;
    }

    public static Box createSafe(double x1, double y1, double x2, double y2) {
        return new Box(x1 < x2 ? x1 : x2,
                y1 < y2 ? y1 : y2,
                x1 > x2 ? x1 : x2,
                y1 > y2 ? y1 : y2);
    }

    public void add(double x, double y) {
        if (x < xmin)
            xmin = x;
        if (y < ymin)
            ymin = y;
        if (x > xmax)
            xmax = x;
        if (y > ymax)
            ymax = y;
    }

    public void add(Box bbox) {
        if (bbox.xmin < xmin)
            xmin = bbox.xmin;
        if (bbox.ymin < ymin)
            ymin = bbox.ymin;
        if (bbox.xmax > xmax)
            xmax = bbox.xmax;
        if (bbox.ymax > ymax)
            ymax = bbox.ymax;
    }

    /**
     * Check if Box contains point defined by coordinates x and y.
     *
     * @param x the x ordinate
     * @param y the y ordinate
     * @return true, if point is inside box.
     */
    public boolean contains(double x, double y) {
        return (x >= xmin
                && x <= xmax
                && y >= ymin
                && y <= ymax);
    }

    /**
     * Check if Box contains Point.
     */
    public boolean contains(Point p) {
        return (p.x >= xmin
                && p.x <= xmax
                && p.y >= ymin
                && p.y <= ymax);
    }

    public double getHeight() {
        return ymax - ymin;
    }

    public double getWidth() {
        return xmax - xmin;
    }

    /**
     * Check if this Box is inside box.
     */
    public boolean inside(Box box) {
        return xmin >= box.xmin
                && xmax <= box.xmax
                && ymin >= box.ymin
                && ymax <= box.ymax;
    }

    /**
     * convrt map coordinates to lat/lon.
     */
    public void map2mercator() {
        double minLon = MercatorProjection.toLongitude(xmin);
        double maxLon = MercatorProjection.toLongitude(xmax);
        double minLat = MercatorProjection.toLatitude(ymax);
        double maxLat = MercatorProjection.toLatitude(ymin);
        xmin = minLon;
        xmax = maxLon;
        ymin = minLat;
        ymax = maxLat;
    }

    public boolean overlap(Box other) {
        return !(xmin > other.xmax
                || xmax < other.xmin
                || ymin > other.ymax
                || ymax < other.ymin);
    }

    public void scale(double d) {
        xmin *= d;
        xmax *= d;
        ymin *= d;
        ymax *= d;
    }

    public void setExtents(float[] points) {
        float x1, y1, x2, y2;
        x1 = x2 = points[0];
        y1 = y2 = points[1];

        for (int i = 2, n = points.length; i < n; i += 2) {
            float x = points[i];
            if (x < x1)
                x1 = x;
            else if (x > x2)
                x2 = x;

            float y = points[i + 1];
            if (y < y1)
                y1 = y;
            else if (y > y2)
                y2 = y;
        }
        this.xmin = x1;
        this.ymin = y1;
        this.xmax = x2;
        this.ymax = y2;
    }

    @Override
    public String toString() {
        return "[" + xmin + ',' + ymin + ',' + xmax + ',' + ymax + ']';
    }

    public void translate(double dx, double dy) {
        xmin += dx;
        xmax += dx;
        ymin += dy;
        ymax += dy;
    }
}
