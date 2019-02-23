/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2018-2019 Gustl22
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

package org.oscim.backend.canvas;

import org.oscim.utils.ColorUtil;
import org.oscim.utils.FastMath;

public final class Color {

    public static class HSV {
        public double hue;
        public double saturation;
        public double value;

        public HSV() {
            hue = 0;
            saturation = 1;
            value = 1;
        }

        public HSV(double hue, double saturation, double value) {
            this.hue = hue;
            this.saturation = saturation;
            this.value = value;
        }

        public int mod(int color, boolean relativ) {
            return ColorUtil.modHsv(color, hue, saturation, value, relativ);
        }
    }

    private static final int OPAQUE = 0xff000000;

    public static int fadePremul(int color, double alpha) {
        alpha = FastMath.clamp(alpha, 0, 1);

        alpha *= (color >>> 24) & 0xff;
        int c = (((int) alpha) & 0xff) << 24;

        alpha /= 255;

        c |= ((int) (alpha * ((color >>> 16) & 0xff))) << 16;
        c |= ((int) (alpha * ((color >>> 8) & 0xff))) << 8;
        c |= ((int) (alpha * (color & 0xff)));

        return c;
    }

    public static int fade(int color, double alpha) {
        alpha = FastMath.clamp(alpha, 0, 1);

        alpha *= (color >>> 24) & 0xff;
        int c = (((int) alpha) & 0xff) << 24;

        return c | (color & 0x00ffffff);
    }

    public static int rainbow(float pos) {
        float i = 255 * pos;
        int r = (int) Math.round(Math.sin(0.024 * i + 0) * 127 + 128);
        int g = (int) Math.round(Math.sin(0.024 * i + 2) * 127 + 128);
        int b = (int) Math.round(Math.sin(0.024 * i + 4) * 127 + 128);
        return 0xff000000 | (r << 16) | (g << 8) | b;
    }

    /**
     * Pack r, g, b bytes into one int.
     */
    public static int get(int r, int g, int b) {
        return 0xff << 24 | r << 16 | g << 8 | b;
    }

    public static int get(double r, double g, double b) {
        return 0xff << 24
                | (int) Math.round(r * 255) << 16
                | (int) Math.round(g * 255) << 8
                | (int) Math.round(b * 255);
    }

    /**
     * Pack premultiplied a, r, g, b bytes into one int.
     */
    public static int get(int a, int r, int g, int b) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    /**
     * Pack r, g, b bytes into one int with premultiplied alpha a.
     */
    public static int get(float a, int r, int g, int b) {
        return fade(0xff << 24 | r << 16 | g << 8 | b, a);
    }

    public static float rToFloat(int color) {
        return ((color >>> 16) & 0xff) / 255f;
    }

    public static float gToFloat(int color) {
        return ((color >>> 8) & 0xff) / 255f;
    }

    public static float bToFloat(int color) {
        return ((color) & 0xff) / 255f;
    }

    public static float aToFloat(int color) {
        return ((color >>> 24) & 0xff) / 255f;
    }

    public static int a(int color) {
        return ((color >>> 24) & 0xff);
    }

    public static int r(int color) {
        return ((color >>> 16) & 0xff);
    }

    public static int g(int color) {
        return ((color >>> 8) & 0xff);
    }

    public static int b(int color) {
        return ((color) & 0xff);
    }

    public static int setA(int color, int a) {
        return ((a << 24) | (color & 0xffffff));
    }

    public static int setR(int color, int r) {
        return ((r << 16) | (color & 0xff00ffff));
    }

    public static int setG(int color, int g) {
        return ((g << 8) | (color & 0xffff00ff));
    }

    public static int setB(int color, int b) {
        return (b | (color & 0xffffff00));
    }

    public static int parseColorComponents(String str) {
        int numComponents = 4;
        int cur = 5;

        if (str.startsWith("rgb(")) {
            numComponents = 3;
            cur = 4;
        } else if (!str.startsWith("rgba("))
            parseColorException(str);

        int end = str.length();
        int component = 0;
        int a = 0, r = 0, g = 0, b = 0;

        if (str.charAt(end - 1) != ')')
            parseColorException(str);

        for (; cur < end; cur++) {
            char c = str.charAt(cur);
            if (c == ',') {
                component++;
                if (component >= numComponents)
                    parseColorException(str);
                continue;
            } else if (c >= '0' && c <= '9') {
                if (component == 0) {
                    r *= 10;
                    r += c - '0';
                } else if (component == 1) {
                    g *= 10;
                    g += c - '0';
                } else if (component == 2) {
                    b *= 10;
                    b += c - '0';
                } else {
                    a *= 10;
                    a += c - '0';
                }

            } else
                parseColorException(str);
        }
        if (r > 255 || g > 255 || b > 255 || a > 255)
            parseColorException(str);

        if (numComponents == 3)
            return get(r, g, b);
        else
            return get(a, r, g, b);
    }

    private static void parseColorException(String str) {
        throw new IllegalArgumentException("Unknown color: \'" + str + '\'');
    }

    /* Copyright (C) 2006 The Android Open Source Project
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License. */
    public static final int BLACK = 0xFF000000;
    public static final int DKGRAY = 0xFF444444;
    public static final int GRAY = 0xFF888888;
    public static final int LTGRAY = 0xFFCCCCCC;
    public static final int WHITE = 0xFFFFFFFF;
    public static final int RED = 0xFFFF0000;
    public static final int GREEN = 0xFF00FF00;
    public static final int BLUE = 0xFF0000FF;
    public static final int YELLOW = 0xFFFFFF00;
    public static final int CYAN = 0xFF00FFFF;
    public static final int MAGENTA = 0xFFFF00FF;
    public static final int TRANSPARENT = 0;

    /**
     * Parse the color string, and return the corresponding color-int.
     * If the string cannot be parsed, throws an IllegalArgumentException
     * exception. Supported formats are:
     * #RRGGBB
     * #AARRGGBB
     * 'red', 'blue', 'green', 'black', 'white', 'gray', 'cyan', 'magenta',
     * 'yellow', 'lightgray', 'darkgray'
     *
     * @param colorString the color string
     * @return the int
     */
    public static int parseColor(String colorString) {
        if (colorString.charAt(0) == '#') {
            // Use a long to avoid rollovers on #ffXXXXXX
            long color = Long.parseLong(colorString.substring(1), 16);
            if (colorString.length() == 7) {
                // Set the alpha value
                color |= 0x00000000ff000000;
            } else if (colorString.length() != 9) {
                parseColorException(colorString);
            }
            return (int) color;
        }
        if (colorString.charAt(0) == 'r') {
            return parseColorComponents(colorString);
        }
        throw new IllegalArgumentException("Unknown color");
    }

    public static int parseColor(String colorString, int fallBackColor) {
        if (colorString.charAt(0) == '#') {
            // Use a long to avoid rollovers on #ffXXXXXX
            long color = Long.parseLong(colorString.substring(1), 16);
            if (colorString.length() == 7) {
                // Set the alpha value
                color |= 0x00000000ff000000;
            } else if (colorString.length() != 9) {
                return fallBackColor;
            }
            return (int) color;
        }
        return fallBackColor;
    }

    public static boolean isOpaque(int color) {
        return (color & OPAQUE) == OPAQUE;
    }

    public static String toString(int color) {
        return "RGB: " + Color.r(color) + ", " + Color.g(color) + ", " + Color.b(color);
    }

    private Color() {
    }
}
