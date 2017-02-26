/*
 * Copyright 2010, 2011, 2012 mapsforge.org
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

import org.oscim.core.Tile;
import org.oscim.renderer.bucket.TextItem;
import org.oscim.theme.styles.TextStyle;
import org.oscim.utils.geom.GeometryUtils;
import org.oscim.utils.geom.LineClipper;

public final class WayDecorator {

    public static void renderText(LineClipper clipper, double[] coordinates, String string,
                                  TextStyle text, int pos, int len, LabelTileData ld) {
        //TextItem items = textItems;
        TextItem t = null;

        // calculate the way name length plus some margin of safety
        float labelWidth = -1;
        float minWidth = Tile.SIZE / 10;

        //final int min = 0;
        //final int max = Tile.SIZE;

        // find way segments long enough to draw the way name on them
        for (int i = pos; i < pos + len - 2; i += 2) {
            // get the first way point coordinates
            double prevX = coordinates[i + 0];
            double prevY = coordinates[i + 1];

            byte edge = 0;
            //clipper.clipStart(prevX, prevY);

            // get the current way point coordinates
            double curX = coordinates[i + 2];
            double curY = coordinates[i + 3];

            //int clip;
            //if ((clip = clipper.clipNext(curX, curY)) != 0) {
            //    if (clip < 0) {
            //        prevX = clipper.out[0];
            //        prevY = clipper.out[1];
            //        curX = clipper.out[2];
            //        curY = clipper.out[3];
            //
            //        if (prevX == min)
            //            edge |= 1 << 0;
            //        else if (prevX == max)
            //            edge |= 1 << 1;
            //
            //        if (prevY == min)
            //            edge |= 1 << 2;
            //        else if (prevY == max)
            //            edge |= 1 << 3;
            //
            //        if (curX == min)
            //            edge |= 1 << 4;
            //        else if (curX == max)
            //            edge |= 1 << 5;
            //
            //        if (curY == min)
            //            edge |= 1 << 5;
            //        else if (curY == max)
            //            edge |= 1 << 6;
            //    }
            //}

            int last = i;

            // calculate the length of the current segment (Euclidian distance)
            double vx = prevX - curX;
            double vy = prevY - curY;
            if (vx == 0 && vy == 0)
                continue;

            float a = (float) Math.sqrt(vx * vx + vy * vy);

            // only if not cur segment crosses edge
            if (edge < (1 << 4)) {
                vx /= a;
                vy /= a;

                // add additional segments if possible
                for (int j = i + 4; j < pos + len; j += 2) {
                    double nextX = coordinates[j + 0];
                    double nextY = coordinates[j + 1];

                    //if ((clip = clipper.clipNext(nextX, nextY)) != 0) {
                    //    if (clip < 0) {
                    //        curX = clipper.out[0];
                    //        curY = clipper.out[1];
                    //        // TODO break when cur has changed
                    //        nextX = clipper.out[2];
                    //        nextY = clipper.out[3];
                    //    }
                    //}

                    double wx = nextX - curX;
                    double wy = nextY - curY;
                    if (wx == 0 && wy == 0)
                        continue;

                    double area = GeometryUtils.area(prevX, prevY, curX, curY, nextX, nextY);

                    if (area > 1000) {
                        //log.debug("b: " + string + " " + area );
                        break;
                    }

                    a = (float) Math.sqrt(wx * wx + wy * wy);
                    wx /= a;
                    wy /= a;

                    // avoid adding short segments that add much area
                    if (area / 2 > a * a) {
                        //log.debug("a: " +string + " " + area + " " + a*a);
                        break;
                    }

                    double ux = vx + wx;
                    double uy = vy + wy;
                    double diff = wx * uy - wy * ux;

                    // maximum angle between segments
                    if (diff > 0.1 || diff < -0.1) {
                        //log.debug("c: " + string + " " + area );
                        break;
                    }
                    curX = nextX;
                    curY = nextY;
                    last = j - 2;

                    //if (clip < 0) {
                    //    if (nextX == min)
                    //        edge |= 1 << 4;
                    //    else if (nextX == max)
                    //        edge |= 1 << 5;
                    //
                    //    if (nextY == min)
                    //        edge |= 1 << 6;
                    //    else if (nextY == max)
                    //        edge |= 1 << 7;
                    //}
                }

                vx = curX - prevX;
                vy = curY - prevY;
                a = (float) Math.sqrt(vx * vx + vy * vy);
            }

            double segmentLength = a;

            if (edge == 0) {
                if (segmentLength < minWidth) {
                    continue;
                }

                if (labelWidth < 0) {
                    labelWidth = text.paint.measureText(string);
                }

                if (segmentLength < labelWidth * 0.50) {
                    continue;
                }
            } else if (labelWidth < 0) {
                labelWidth = text.paint.measureText(string);
            }

            double x1, y1, x2, y2;
            if (prevX < curX) {
                x1 = prevX;
                y1 = prevY;
                x2 = curX;
                y2 = curY;
            } else {
                x1 = curX;
                y1 = curY;
                x2 = prevX;
                y2 = prevY;
            }

            TextItem n = TextItem.pool.get();

            // link items together
            //if (t != null) {
            //    t.n1 = n;
            //    n.n2 = t;
            //}

            t = n;
            t.x = x1 + (x2 - x1) / 2f;
            t.y = y1 + (y2 - y1) / 2f;
            t.string = string;
            t.text = text;
            t.width = labelWidth;
            t.x1 = x1;
            t.y1 = y1;
            t.x2 = x2;
            t.y2 = y2;
            t.length = (short) segmentLength;

            t.edges = edge;
            ld.labels.push(t);

            i = last;
        }
    }

    private WayDecorator() {
        throw new IllegalStateException();
    }
}
