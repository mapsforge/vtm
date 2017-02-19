/*
 * Copyright 2012 Hannes Janetzek
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

public class FastMath {
    /**
     * Integer version of log2(x)
     * <p/>
     * from http://graphics.stanford.edu/~seander/bithacks.html#IntegerLog
     */
    public static int log2(int x) {

        int r = 0; // result of log2(v) will go here

        if ((x & 0xFFFF0000) != 0) {
            x >>= 16;
            r |= 16;
        }
        if ((x & 0xFF00) != 0) {
            x >>= 8;
            r |= 8;
        }
        if ((x & 0xF0) != 0) {
            x >>= 4;
            r |= 4;
        }
        if ((x & 0xC) != 0) {
            x >>= 2;
            r |= 2;
        }
        if ((x & 0x2) != 0) {
            r |= 1;
        }
        return r;
    }

    /**
     * Integer version of 2^x
     */
    public static float pow(int x) {
        if (x == 0)
            return 1;

        return (x > 0 ? (1 << x) : (1.0f / (1 << -x)));
    }

    public static int clamp(int value, int min, int max) {
        return (value < min ? min : (value > max ? max : value));
    }

    public static float clamp(float value, float min, float max) {
        return (value < min ? min : (value > max ? max : value));
    }

    public static double clamp(double value, double min, double max) {
        return (value < min ? min : (value > max ? max : value));
    }

    public static float clampN(float value) {
        return (value < 0f ? 0f : (value > 1f ? 1f : value));
    }

    public static byte clampToByte(int value) {
        return (byte) (value < 0 ? 0 : (value > 255 ? 255 : value));
    }

    public static float abs(float value) {
        return value < 0 ? -value : value;
    }

    public static float absMax(float value1, float value2) {
        float a1 = value1 < 0 ? -value1 : value1;
        float a2 = value2 < 0 ? -value2 : value2;
        return a2 < a1 ? a1 : a2;
    }

    /**
     * test if any absolute value is greater than 'cmp'
     */
    public static boolean absMaxCmp(double value1, double value2, double cmp) {
        return value1 < -cmp || value1 > cmp || value2 < -cmp || value2 > cmp;
    }

    /**
     * test if any absolute value is greater than 'cmp'
     */
    public static boolean absMaxCmp(int value1, int value2, int cmp) {
        return value1 < -cmp || value1 > cmp || value2 < -cmp || value2 > cmp;
    }

    public static boolean withinSquaredDist(int dx, int dy, int distance) {
        return dx * dx + dy * dy < distance;
    }

    public static boolean withinSquaredDist(float dx, float dy, float distance) {
        return dx * dx + dy * dy < distance;
    }
}
