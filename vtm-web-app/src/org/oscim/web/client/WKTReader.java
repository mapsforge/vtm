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
package org.oscim.web.client;

import org.oscim.core.GeometryBuffer;

public class WKTReader {
    private static final String POINT = "POINT";
    private static final String LINE = "LINESTRING";
    private static final String POLY = "POLYGON";
    private static final String MULTI = "MULTI";

    private static final int SKIP_POINT = POINT.length();
    private static final int SKIP_LINE = LINE.length();
    private static final int SKIP_POLY = POLY.length();
    private static final int SKIP_MULTI = MULTI.length();

    public void parse(String wkt, GeometryBuffer geom) throws Exception {
        // return position.
        int[] pos = new int[]{0};

        int len = wkt.length();

        if (wkt.startsWith(POINT, pos[0])) {
            pos[0] += SKIP_POINT;
            geom.startPoints();
            ensure(wkt, pos, '(');
            parsePoint(geom, wkt, len, pos);
            ensure(wkt, pos, ')');
        } else if (wkt.startsWith(LINE, pos[0])) {
            pos[0] += SKIP_LINE;
            geom.startLine();

            parseLine(geom, wkt, len, pos);

        } else if (wkt.startsWith(POLY, pos[0])) {
            pos[0] += SKIP_POLY;
            geom.startPolygon();

            parsePoly(geom, wkt, len, pos);

        } else if (wkt.startsWith(MULTI, pos[0])) {
            pos[0] += SKIP_MULTI;

            if (wkt.startsWith(POINT, pos[0])) {
                pos[0] += SKIP_POINT;
                geom.startPoints();
                ensure(wkt, pos, '(');
                parsePoint(geom, wkt, len, pos);
                while (wkt.charAt(pos[0]) == ',') {
                    pos[0]++;
                    parsePoint(geom, wkt, len, pos);
                }
                ensure(wkt, pos, ')');

            } else if (wkt.startsWith(LINE, pos[0])) {
                pos[0] += SKIP_LINE;
                geom.startLine();
                ensure(wkt, pos, '(');
                parseLine(geom, wkt, len, pos);
                while (wkt.charAt(pos[0]) == ',') {
                    pos[0]++;
                    geom.startLine();
                    parseLine(geom, wkt, len, pos);
                }
                ensure(wkt, pos, ')');

            } else if (wkt.startsWith(POLY, pos[0])) {
                pos[0] += SKIP_POLY;
                geom.startPolygon();
                ensure(wkt, pos, '(');
                parsePoly(geom, wkt, len, pos);
                while (wkt.charAt(pos[0]) == ',') {
                    pos[0]++;
                    geom.startPolygon();
                    parsePoly(geom, wkt, len, pos);
                }
                ensure(wkt, pos, ')');
            } else
                throw new Exception("usupported geometry ");
        } else
            throw new Exception("usupported geometry ");
    }

    private static void ensure(String wkt, int[] pos, char c) throws Exception {
        if (wkt.charAt(pos[0]) != c)
            throw new Exception();

        pos[0]++;
    }

    private static void parsePoly(GeometryBuffer geom, String wkt, int len, int[] adv)
            throws Exception {
        // outer ring
        ensure(wkt, adv, '(');
        parseLine(geom, wkt, len, adv);

        while (wkt.charAt(adv[0]) == ',') {
            adv[0]++;
            geom.startHole();
            parseLine(geom, wkt, len, adv);
        }
        ensure(wkt, adv, ')');
    }

    private static void parseLine(GeometryBuffer geom, String wkt, int len, int[] adv)
            throws Exception {
        ensure(wkt, adv, '(');

        parsePoint(geom, wkt, len, adv);
        while (wkt.charAt(adv[0]) == ',') {
            adv[0]++;
            parsePoint(geom, wkt, len, adv);
        }
        ensure(wkt, adv, ')');
    }

    private static void parsePoint(GeometryBuffer geom, String wkt, int len, int[] adv) {
        float x = parseNumber(wkt, len, adv);

        // skip ' '
        adv[0]++;

        float y = parseNumber(wkt, len, adv);

        geom.addPoint(x, y);
    }

    static float parseNumber(String wkt, int len, int[] adv) {
        int pos = adv[0];

        boolean neg = false;
        if (wkt.charAt(pos) == '-') {
            neg = true;
            pos++;
        }

        float val = 0;
        int pre = 0;
        char c = 0;

        for (; pos < len; pos++, pre++) {
            c = wkt.charAt(pos);
            if (c < '0' || c > '9') {
                if (pre == 0)
                    throw new NumberFormatException("s " + c);

                break;
            }
            val = val * 10 + (int) (c - '0');
        }

        if (pre == 0)
            throw new NumberFormatException();

        if (c == '.') {
            float div = 10;
            for (pos++; pos < len; pos++) {
                c = wkt.charAt(pos);
                if (c < '0' || c > '9')
                    break;
                val = val + ((int) (c - '0')) / div;
                div *= 10;
            }
        }

        if (c == 'e' || c == 'E') {
            // advance 'e'
            pos++;

            // check direction
            int dir = 1;
            if (wkt.charAt(pos) == '-') {
                dir = -1;
                pos++;
            }
            // skip leading zeros
            for (; pos < len; pos++)
                if (wkt.charAt(pos) != '0')
                    break;

            int shift = 0;
            for (pre = 0; pos < len; pos++, pre++) {
                c = wkt.charAt(pos);
                if (c < '0' || c > '9') {
                    // nothing after 'e'
                    if (pre == 0)
                        throw new NumberFormatException("e " + c);
                    break;
                }
                shift = shift * 10 + (int) (c - '0');
            }

            // guess it's ok for sane values of E
            if (dir > 0) {
                while (shift-- > 0)
                    val *= 10;
            } else {
                while (shift-- > 0)
                    val /= 10;
            }
        }

        adv[0] = pos;

        return neg ? -val : val;
    }

    //    public static void main(String[] args) {
    //        WKTReader r = new WKTReader();
    //        GeometryBuffer geom = new GeometryBuffer(10, 10);
    //        try {
    //            String wkt = "MULTIPOINT(0 0,1 0)";
    //            r.parse(wkt, geom);
    //            for (int i = 0; i < geom.index.length; i++) {
    //                int len = geom.index[i];
    //                if (len < 0)
    //                    break;
    //                for (int p = 0; p < len; p += 2)
    //                    System.out.println(len + ": " + geom.points[p] + "," + geom.points[p + 1]);
    //            }
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }
}
