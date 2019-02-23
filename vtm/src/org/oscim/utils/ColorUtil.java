/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2019 Gustl22
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

import org.oscim.backend.canvas.Color;
import org.oscim.utils.math.Vec3;

import static org.oscim.backend.canvas.Color.b;
import static org.oscim.backend.canvas.Color.g;
import static org.oscim.backend.canvas.Color.r;
import static org.oscim.utils.FastMath.clamp;

public class ColorUtil {

    private static final Vec3 TMP_VEC = new Vec3();

    public static synchronized int desaturate(int color) {
        Vec3 hsl = TMP_VEC;
        rgbToHsl(r(color), g(color), b(color), hsl);
        return hslToRgb(hsl.x, 0, hsl.z);
    }

    public static synchronized int saturate(int color, double saturation) {
        Vec3 hsl = TMP_VEC;
        rgbToHsv(r(color), g(color), b(color), hsl);
        return hsvToRgb(hsl.x, saturation, hsl.z);
    }

    public static synchronized int setHue(int color, double hue) {
        Vec3 hsl = TMP_VEC;
        rgbToHsv(r(color), g(color), b(color), hsl);
        return hsvToRgb(hue, hsl.y, hsl.z, null);
    }

    public static synchronized int shiftHue(int color, double hue) {
        Vec3 hsv = TMP_VEC;
        rgbToHsv(r(color), g(color), b(color), hsv);
        hsv.x += hue;
        hsv.x -= Math.floor(hsv.x);

        return hsvToRgb(clamp(hsv.x, 0, 1), hsv.y, hsv.z, null);
    }

    public static synchronized int saturate(int color, double saturation, boolean relative) {
        Vec3 hsl = TMP_VEC;
        rgbToHsv(r(color), g(color), b(color), hsl);
        return hsvToRgb(hsl.x, clamp(saturation * hsl.y, 0, 1), hsl.z);
    }

    /**
     * @param hue        the hue from 0 to 1 (exclusive)
     *                   0: no color shift
     *                   0.5: opposite hue
     * @param saturation the saturation
     *                   0 to 1: desaturate
     *                   1 to 2: saturate
     * @param value      the lightness
     *                   0 to 1: darken
     *                   1 to 2: lighten
     * @param relative   indicate if colors are modified relative to their values
     *                   (e.g black not changes if relative)
     */
    public static synchronized int modHsv(int color, double hue, double saturation, double value,
                                          boolean relative) {
        Vec3 hsl = TMP_VEC;
        if (saturation == 1 && value == 1 && (hue == 0 || hue == 1)) return color;
        rgbToHsv(r(color), g(color), b(color), hsl);
        return hsvToRgb(clamp((hue + hsl.x) % 1, 0, 1),
                clamp(relative || saturation <= 1 ? saturation * hsl.y :
                        hsl.y + (saturation - 1) * (1 - hsl.y), 0, 1),
                clamp(relative || value <= 1 ? value * hsl.z :
                        hsl.z + (value - 1) * (1 - hsl.z), 0, 1));
    }

    // functions ported from http://axonflux.com/handy-rgb-to-hsl-and-rgb-to-hsv-color-model-c

    /**
     * Converts an RGB color value to HSL. Conversion formula
     * adapted from http://en.wikipedia.org/wiki/HSL_color_space.
     * Assumes r, g, and b are contained in the set [0, 255] and
     * returns h, s, and l in the set [0, 1].
     *
     * @param Number r The red color value
     * @param Number g The green color value
     * @param Number b The blue color value
     * @return Array The HSL representation
     */
    public static Vec3 rgbToHsl(double r, double g, double b, Vec3 out) {
        r /= 255d;
        g /= 255d;
        b /= 255d;

        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));

        double h = 0, s = 0, l = (max + min) / 2;

        if (max != min) {
            double d = max - min;
            s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
            if (max == r)
                h = (g - b) / d + (g < b ? 6 : 0);
            else if (max == g)
                h = (b - r) / d + 2;
            else
                h = (r - g) / d + 4;

            h /= 6;
        }

        out.set(h, s, l);

        return out;
    }

    public static Vec3 rgbToHsl(double r, double g, double b) {
        return rgbToHsl(r, g, b, new Vec3());
    }

    /**
     * Converts an HSL color value to RGB. Conversion formula
     * adapted from http://en.wikipedia.org/wiki/HSL_color_space.
     * Assumes h, s, and l are contained in the set [0, 1] and
     * returns r, g, and b in the set [0, 255].
     *
     * @param Number h The hue
     * @param Number s The saturation
     * @param Number l The lightness
     * @return Array The RGB representation
     */
    public static int hslToRgb(double h, double s, double l, Vec3 out) {
        double r, g, b;

        if (s == 0) {
            r = g = b = l; // achromatic
        } else {
            double q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            double p = 2 * l - q;
            r = hue2rgb(p, q, h + 1 / 3);
            g = hue2rgb(p, q, h);
            b = hue2rgb(p, q, h - 1 / 3);
        }

        if (out != null)
            out.set(r, g, b);

        return Color.get(r, g, b);
    }

    static double hue2rgb(double p, double q, double t) {
        if (t < 0)
            t += 1;
        if (t > 1)
            t -= 1;
        if (t < 1 / 6)
            return p + (q - p) * 6 * t;
        if (t < 1 / 2)
            return q;
        if (t < 2 / 3)
            return p + (q - p) * (2 / 3 - t) * 6;
        return p;
    }

    /**
     * Converts an RGB color value to HSV. Conversion formula
     * adapted from http://en.wikipedia.org/wiki/HSV_color_space.
     * Assumes r, g, and b are contained in the set [0, 255] and
     * returns h, s, and v in the set [0, 1].
     *
     * @param Number r The red color value
     * @param Number g The green color value
     * @param Number b The blue color value
     * @return Array The HSV representation
     */
    public static Vec3 rgbToHsv(double r, double g, double b, Vec3 out) {
        r /= 255d;
        g /= 255d;
        b /= 255d;

        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));

        double h = 0, s, v = max;

        double d = max - min;
        s = max == 0 ? 0 : d / max;

        if (max != min) {
            if (max == r)
                h = (g - b) / d + (g < b ? 6 : 0);
            else if (max == g)
                h = (b - r) / d + 2;
            else if (max == b)
                h = (r - g) / d + 4;
            h /= 6;
        }

        out.set(h, s, v);

        return out;
    }

    public static Vec3 rgbToHsv(double r, double g, double b) {
        return rgbToHsv(r, g, b, new Vec3());
    }

    /**
     * Converts an HSV color value to RGB. Conversion formula
     * adapted from http://en.wikipedia.org/wiki/HSV_color_space.
     * Assumes h, s, and v are contained in the set [0, 1] and
     * returns r, g, and b in the set [0, 255].
     *
     * @param h   The hue
     * @param s   The saturation
     * @param v   The value
     * @param out result rgb, may be ommited
     * @return Array The RGB representation
     */
    public static int hsvToRgb(double h, double s, double v, Vec3 out) {
        double r = 0, g = 0, b = 0;

        int i = (int) Math.floor(h * 6);
        double f = h * 6 - i;
        double p = v * (1 - s);
        double q = v * (1 - f * s);
        double t = v * (1 - (1 - f) * s);

        switch (i % 6) {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = p;
                g = v;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            case 5:
                r = v;
                g = p;
                b = q;
                break;
        }

        if (out != null)
            out.set(r, g, b);

        return Color.get(r, g, b);
    }

    public static int hsvToRgb(double h, double s, double v) {
        return hsvToRgb(h, s, v, null);
    }

    public static int hslToRgb(double h, double s, double l) {
        return hslToRgb(h, s, l, null);
    }

    /**
     * Blend two colors.
     *
     * @param color1 the first color
     * @param color2 the second color
     * @param mix    the mixing proportion in range 0 to 1
     * @return the blended color
     */
    public static int blend(int color1, int color2, float mix) {
        float mix2 = 1f - mix;
        return Color.get(
                (int) ((((color2 >>> 24) & 0xff) * mix) + (((color1 >>> 24) & 0xff) * mix2)),
                (int) ((((color2 >>> 16) & 0xff) * mix) + (((color1 >>> 16) & 0xff) * mix2)),
                (int) ((((color2 >>> 8) & 0xff) * mix) + (((color1 >>> 8) & 0xff) * mix2)),
                (int) ((((color2 >>> 0) & 0xff) * mix) + (((color1 >>> 0) & 0xff) * mix2))
        );
    }
}
