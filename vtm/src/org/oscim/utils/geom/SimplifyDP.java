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

import org.oscim.core.GeometryBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.oscim.utils.geom.GeometryUtils.squareSegmentDistance;

/**
 * Douglas-Peucker line simplification with stack and some bit twiddling.
 * <p/>
 * based on: https://github.com/mourner/simplify-js,
 * https://github.com/ekeneijeoma/simplify-java
 */
public class SimplifyDP {
    final static Logger log = LoggerFactory.getLogger(SimplifyDP.class);

    boolean[] markers = new boolean[128];
    int[] stack = new int[32];

    public void simplify(GeometryBuffer geom, float sqTolerance) {
        int[] idx = geom.index;

        int inPos = 0;
        int outPos = 0;

        for (int i = 0, n = idx.length; i < n; i++) {
            int len = idx[i];
            if (len < 0)
                break;
            if (len < 6) {
                inPos += len;
                outPos += len;
                continue;
            }

            int end = simplify(geom.points, inPos, len, outPos, sqTolerance);
            if (end > inPos + len)
                log.error("out larger than cur: {} > {}", end, inPos + len);

            idx[i] = (short) (end - outPos);
            outPos = end;
            inPos += len;
        }
    }

    public int simplify(double[] points, int inPos, int length, int out, float sqTolerance) {

        if ((length >> 1) >= markers.length)
            markers = new boolean[length >> 1];
        //else
        //    Arrays.fill(markers, false);

        int first = inPos;
        int last = inPos + length - 2;
        int index = 0;

        double maxSqDist;
        double sqDist;
        int sp = 0;

        while (true) {
            maxSqDist = 0;

            for (int i = first + 2; i < last; i += 2) {
                sqDist = squareSegmentDistance(points, i, first, last);
                if (sqDist > maxSqDist) {
                    index = i;
                    maxSqDist = sqDist;
                }
            }

            if (maxSqDist > sqTolerance) {
                markers[(index - inPos) >> 1] = true;

                if (sp + 4 == stack.length) {
                    int tmp[] = new int[stack.length + 64];
                    System.arraycopy(stack, 0, tmp, 0, stack.length);
                    stack = tmp;
                }

                stack[sp++] = first;
                stack[sp++] = index;

                stack[sp++] = index;
                stack[sp++] = last;
            }

            if (sp == 0)
                break;

            last = stack[--sp];
            first = stack[--sp];
        }

        points[out++] = points[inPos];
        points[out++] = points[inPos + 1];

        last = inPos + length - 2;

        for (int i = 0; i < length / 2; i++) {
            if (!markers[i])
                continue;
            markers[i] = false;

            int pos = inPos + i * 2;

            points[out++] = points[pos];
            points[out++] = points[pos + 1];
        }
        points[out++] = points[last];
        points[out++] = points[last + 1];

        return out;
    }
}
