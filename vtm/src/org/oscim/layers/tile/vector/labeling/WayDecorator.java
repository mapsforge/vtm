/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 * Copyright 2013 Hannes Janetzek
 * Copyright 2018-2019 devemux86
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
import org.oscim.renderer.bucket.SymbolItem;
import org.oscim.renderer.bucket.TextItem;
import org.oscim.theme.styles.SymbolStyle;
import org.oscim.theme.styles.TextStyle;
import org.oscim.utils.geom.GeometryUtils;
import org.oscim.utils.geom.LineClipper;

public final class WayDecorator {

    /**
     * Mapsforge implementation.
     */
    public static void renderSymbol(LineClipper clipper, float[] coordinates, SymbolStyle symbol,
                                    int pos, int len, LabelTileData ld) {
        int skipPixels = (int) symbol.repeatStart;

        // get the first way point coordinates
        float previousX = coordinates[pos + 0];
        float previousY = coordinates[pos + 1];

        // draw the symbol on each way segment
        float segmentLengthRemaining;
        float segmentSkipPercentage;
        float theta = 0;

        for (int i = pos; i < pos + len - 2; i += 2) {
            // get the current way point coordinates
            float currentX = coordinates[i + 2];
            float currentY = coordinates[i + 3];

            // calculate the length of the current segment (Euclidean distance)
            float diffX = currentX - previousX;
            float diffY = currentY - previousY;
            double segmentLengthInPixel = Math.sqrt(diffX * diffX + diffY * diffY);
            segmentLengthRemaining = (float) segmentLengthInPixel;

            while (segmentLengthRemaining - skipPixels > symbol.repeatStart) {
                // calculate the percentage of the current segment to skip
                segmentSkipPercentage = skipPixels / segmentLengthRemaining;

                // move the previous point forward towards the current point
                previousX += diffX * segmentSkipPercentage;
                previousY += diffY * segmentSkipPercentage;

                if (symbol.rotate) {
                    // if we do not rotate theta will be 0, which is correct
                    theta = (float) Math.toDegrees(Math.atan2(currentY - previousY, currentX - previousX));
                }

                float x = previousX;
                float y = previousY;
                if (x >= 0 && x <= Tile.SIZE && y >= 0 && y <= Tile.SIZE) {
                    SymbolItem s = SymbolItem.pool.get();
                    if (symbol.bitmap != null)
                        s.set(x, y, symbol.bitmap, theta, symbol.billboard);
                    else
                        s.set(x, y, symbol.texture, theta, symbol.billboard);
                    ld.symbols.push(s);
                }

                // check if the symbol should only be rendered once
                if (!symbol.repeat) {
                    return;
                }

                // recalculate the distances
                diffX = currentX - previousX;
                diffY = currentY - previousY;

                // recalculate the remaining length of the current segment
                segmentLengthRemaining -= skipPixels;

                // set the amount of pixels to skip before repeating the symbol
                skipPixels = (int) symbol.repeatGap;
            }

            skipPixels -= segmentLengthRemaining;
            if (skipPixels < symbol.repeatStart) {
                skipPixels = (int) symbol.repeatStart;
            }

            // set the previous way point coordinates for the next loop
            previousX = currentX;
            previousY = currentY;
        }
    }

    public static void renderText(LineClipper clipper, float[] coordinates, String label,
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
            float prevX = coordinates[i + 0];
            float prevY = coordinates[i + 1];

            byte edge = 0;
            //clipper.clipStart(prevX, prevY);

            // get the current way point coordinates
            float curX = coordinates[i + 2];
            float curY = coordinates[i + 3];

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

            // calculate the length of the current segment (Euclidean distance)
            float vx = prevX - curX;
            float vy = prevY - curY;
            if (vx == 0 && vy == 0)
                continue;

            float a = (float) Math.sqrt(vx * vx + vy * vy);

            // only if not cur segment crosses edge
            if (edge < (1 << 4)) {
                vx /= a;
                vy /= a;

                // add additional segments if possible
                for (int j = i + 4; j < pos + len; j += 2) {
                    float nextX = coordinates[j + 0];
                    float nextY = coordinates[j + 1];

                    //if ((clip = clipper.clipNext(nextX, nextY)) != 0) {
                    //    if (clip < 0) {
                    //        curX = clipper.out[0];
                    //        curY = clipper.out[1];
                    //        // TODO break when cur has changed
                    //        nextX = clipper.out[2];
                    //        nextY = clipper.out[3];
                    //    }
                    //}

                    float wx = nextX - curX;
                    float wy = nextY - curY;
                    if (wx == 0 && wy == 0)
                        continue;

                    float area = GeometryUtils.area(prevX, prevY, curX, curY, nextX, nextY);

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

                    float ux = vx + wx;
                    float uy = vy + wy;
                    float diff = wx * uy - wy * ux;

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

            float segmentLength = a;

            if (edge == 0) {
                if (segmentLength < minWidth) {
                    continue;
                }

                if (labelWidth < 0) {
                    labelWidth = text.paint.measureText(label);
                }

                if (segmentLength < labelWidth * 0.50) {
                    continue;
                }
            } else if (labelWidth < 0) {
                labelWidth = text.paint.measureText(label);
            }

            float x1, y1, x2, y2;
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
            t.label = label;
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
