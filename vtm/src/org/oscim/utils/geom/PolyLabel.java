/**
 * Copyright 2016 Andrey Novikov
 * Java implementation of https://github.com/mapbox/polylabel
 * <p>
 * ISC License
 * Copyright (c) 2016 Mapbox
 * <p>
 * Permission to use, copy, modify, and/or distribute this software for any purpose
 * with or without fee is hereby granted, provided that the above copyright notice
 * and this permission notice appear in all copies.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS" AND ISC DISCLAIMS ALL WARRANTIES WITH REGARD TO
 * THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS.
 * IN NO EVENT SHALL ISC BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR
 * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA
 * OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
 * ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS
 * SOFTWARE.
 */
package org.oscim.utils.geom;

import org.oscim.core.GeometryBuffer;
import org.oscim.core.Point;
import org.oscim.core.PointF;

import java.util.Comparator;
import java.util.PriorityQueue;

public class PolyLabel {
    /**
     * Calculation precision.
     */
    public static float PRECISION = 5f;

    private static final float SQRT2 = (float) Math.sqrt(2);

    /**
     * Returns pole of inaccessibility, the most distant internal point from the polygon outline.
     *
     * @param polygon polygon geometry
     * @return optimal label placement point
     */
    public static Point get(GeometryBuffer polygon) {
        // find the bounding box of the outer ring
        double minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;

        // take centroid as the first best guess
        Cell bestCell = getCentroidCell(polygon);

        // if polygon is clipped to a line, return invalid label point
        if (Double.isNaN(bestCell.x) || Double.isNaN(bestCell.y))
            return new Point(-1f, -1f);

        int n = polygon.index[0];

        for (int i = 0; i < n; ) {
            double x = polygon.points[i++];
            double y = polygon.points[i++];
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }

        double width = maxX - minX;
        double height = maxY - minY;
        double cellSize = Math.min(width, height);
        double h = cellSize / 2;

        // a priority queue of cells in order of their "potential" (max distance to polygon)
        PriorityQueue<Cell> cellQueue = new PriorityQueue<>(1, new MaxComparator());

        // cover polygon with initial cells
        for (double x = minX; x < maxX; x += cellSize) {
            for (double y = minY; y < maxY; y += cellSize) {
                cellQueue.add(new Cell(x + h, y + h, h, polygon));
            }
        }

        // special case for rectangular polygons
        Cell bboxCell = new Cell(minX + width / 2, minY + height / 2, 0, polygon);
        if (bboxCell.d > bestCell.d) bestCell = bboxCell;

        while (!cellQueue.isEmpty()) {
            // pick the most promising cell from the queue
            Cell cell = cellQueue.remove();

            // update the best cell if we found a better one
            if (cell.d > bestCell.d)
                bestCell = cell;

            // do not drill down further if there's no chance of a better solution
            if (cell.max - bestCell.d <= PRECISION) continue;

            // split the cell into four cells
            h = cell.h / 2;
            cellQueue.add(new Cell(cell.x - h, cell.y - h, h, polygon));
            cellQueue.add(new Cell(cell.x + h, cell.y - h, h, polygon));
            cellQueue.add(new Cell(cell.x - h, cell.y + h, h, polygon));
            cellQueue.add(new Cell(cell.x + h, cell.y + h, h, polygon));
        }

        return new Point(bestCell.x, bestCell.y);
    }

    private static class MaxComparator implements Comparator<Cell> {
        @Override
        public int compare(Cell a, Cell b) {
            return Double.compare(b.max, a.max);
        }
    }

    private static class Cell {
        final double x;
        final double y;
        final double h;
        final double d;
        final double max;

        Cell(double x, double y, double h, GeometryBuffer polygon) {
            this.x = x; // cell center x
            this.y = y; // cell center y
            this.h = h; // half the cell size
            this.d = pointToPolygonDist(x, y, polygon); // distance from cell center to polygon
            this.max = this.d + this.h * SQRT2; // max distance to polygon within a cell
        }
    }

    // signed distance from point to polygon outline (negative if point is outside)
    private static float pointToPolygonDist(double x, double y, GeometryBuffer polygon) {
        boolean inside = false;
        double minDistSq = Float.POSITIVE_INFINITY;

        int pos = 0;

        for (int k = 0; k < polygon.index.length; k++) {
            if (polygon.index[k] < 0)
                break;
            if (polygon.index[k] == 0)
                continue;

            for (int i = 0, n = polygon.index[k], j = n - 2; i < n; j = i, i += 2) {
                double ax = polygon.points[pos + i];
                double ay = polygon.points[pos + i + 1];
                double bx = polygon.points[pos + j];
                double by = polygon.points[pos + j + 1];

                if (((ay > y) ^ (by > y)) &&
                        (x < (bx - ax) * (y - ay) / (by - ay) + ax)) inside = !inside;

                minDistSq = Math.min(minDistSq, getSegDistSq(x, y, ax, ay, bx, by));
            }

            pos += polygon.index[k];
        }

        return (float) ((inside ? 1 : -1) * Math.sqrt(minDistSq));
    }

    // get polygon centroid
    private static Cell getCentroidCell(GeometryBuffer polygon) {
        double area = 0f;
        double x = 0f;
        double y = 0f;

        for (int i = 0, n = polygon.index[0], j = n - 2; i < n; j = i, i += 2) {
            double ax = polygon.points[i];
            double ay = polygon.points[i + 1];
            double bx = polygon.points[j];
            double by = polygon.points[j + 1];
            double f = ax * by - bx * ay;
            x += (ax + bx) * f;
            y += (ay + by) * f;
            area += f * 3;
        }
        return new Cell(x / area, y / area, 0f, polygon);
    }

    // get squared distance from a point to a segment
    private static double getSegDistSq(double px, double py, double ax, double ay, double bx, double by) {

        double x = ax;
        double y = ay;
        double dx = bx - x;
        double dy = by - y;

        if (dx != 0f || dy != 0f) {

            double t = ((px - x) * dx + (py - y) * dy) / (dx * dx + dy * dy);

            if (t > 1) {
                x = bx;
                y = by;

            } else if (t > 0) {
                x += dx * t;
                y += dy * t;
            }
        }

        dx = px - x;
        dy = py - y;

        return dx * dx + dy * dy;
    }
}
