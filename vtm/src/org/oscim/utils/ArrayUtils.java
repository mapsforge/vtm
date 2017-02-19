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
package org.oscim.utils;

public class ArrayUtils {

    public static <T> void reverse(T[] data) {
        reverse(data, 0, data.length);
    }

    public static <T> void reverse(T[] data, int left, int right) {
        right--;

        while (left < right) {
            T tmp = data[left];
            data[left] = data[right];
            data[right] = tmp;

            left++;
            right--;
        }
    }

    public static void reverse(short[] data, int left, int right, int stride) {
        right -= stride;

        while (left < right) {
            for (int i = 0; i < stride; i++) {
                short tmp = data[left + i];
                data[left + i] = data[right + i];
                data[right + i] = tmp;
            }
            left += stride;
            right -= stride;
        }
    }

    public static void reverse(byte[] data, int left, int right) {
        right -= 1;

        while (left < right) {
            byte tmp = data[left];
            data[left] = data[right];
            data[right] = tmp;

            left++;
            right--;
        }
    }

    public static double parseNumber(char[] str, int pos, int end) {

        boolean neg = false;
        if (str[pos] == '-') {
            neg = true;
            pos++;
        }

        double val = 0;
        int pre = 0;
        char c = 0;

        for (; pos < end; pos++, pre++) {
            c = str[pos];
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
            for (pos++; pos < end; pos++) {
                c = str[pos];
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
            if (str[pos] == '-') {
                dir = -1;
                pos++;
            }
            // skip leading zeros
            for (; pos < end; pos++)
                if (str[pos] != '0')
                    break;

            int shift = 0;
            for (pre = 0; pos < end; pos++, pre++) {
                c = str[pos];
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

        return neg ? -val : val;
    }

    public static boolean withinRange(float[] vec, float min, float max) {
        for (int i = 0, n = vec.length; i < n; i++) {
            float v = vec[i];
            if (v < min || v > max)
                return false;
        }
        return true;
    }

    /**
     * Set bbox array to:
     * xmin, ymin,
     * xmin, ymax,
     * xmax, ymax,
     * xmax, ymin,
     */
    public static void setBox2D(double[] bbox, double xmin, double ymin, double xmax, double ymax) {
        bbox[0] = bbox[2] = xmin;
        bbox[4] = bbox[6] = xmax;
        bbox[1] = bbox[7] = ymin;
        bbox[3] = bbox[5] = ymax;
    }
}
