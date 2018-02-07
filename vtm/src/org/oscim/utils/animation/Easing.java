/*
 * Copyright 2016 Izumi Kawashima
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
package org.oscim.utils.animation;

import static org.oscim.utils.FastMath.clamp;

public class Easing {
    public enum Type {
        LINEAR,
        SINE_INOUT,
        SINE_IN,
        SINE_OUT,
        EXPO_OUT,
        QUAD_INOUT,
        CUBIC_INOUT,
        QUART_INOUT,
        QUINT_INOUT
    }

    static public float ease(long start, long current, float duration, Type easingType) {
        long millisElapsed = current - start;
        if (millisElapsed > duration) {
            return 1;
        }
        float x = (float) millisElapsed / duration;
        float t = millisElapsed;
        float b = 0;
        float c = 1;
        float d = duration;

        float adv = 0;
        switch (easingType) {
            case LINEAR: // Already linear.
                adv = linear(x, t, b, c, d);
                break;
            case SINE_INOUT:
                adv = sineInout(x, t, b, c, d);
                break;
            case SINE_IN:
                adv = sineIn(x, t, b, c, d);
                break;
            case SINE_OUT:
                adv = sineOut(x, t, b, c, d);
                break;
            case EXPO_OUT:
                adv = expoOut(x, t, b, c, d);
                break;
            case QUAD_INOUT:
                adv = quadInout(x, t, b, c, d);
                break;
            case CUBIC_INOUT:
                adv = cubicInout(x, t, b, c, d);
                break;
            case QUART_INOUT:
                adv = quartInout(x, t, b, c, d);
                break;
            case QUINT_INOUT:
                adv = quintInout(x, t, b, c, d);
                break;
        }
        adv = clamp(adv, 0, 1);
        return adv;
    }

    // Following easing functions are copied from https://github.com/danro/jquery-easing/blob/master/jquery.easing.js
    // Under BSD license
    static private float linear(float x, float t, float b, float c, float d) {
        return c * x + b;
    }

    static private float sineInout(float x, float t, float b, float c, float d) {
        return -c / 2 * (float) (Math.cos(Math.PI * t / d) - 1) + b;
    }

    static private float sineIn(float x, float t, float b, float c, float d) {
        return -c * (float) Math.cos(t / d * (Math.PI / 2)) + c + b;
    }

    static private float sineOut(float x, float t, float b, float c, float d) {
        return c * (float) Math.sin(t / d * (Math.PI / 2)) + b;
    }

    static private float expoOut(float x, float t, float b, float c, float d) {
        return (t == d) ? b + c : c * (float) (-Math.pow(2, -10 * x) + 1) + b;
    }

    static private float quadInout(float x, float t, float b, float c, float d) {
        if ((t /= d / 2) < 1) return c / 2 * t * t + b;
        return -c / 2 * ((--t) * (t - 2) - 1) + b;
    }

    static private float cubicInout(float x, float t, float b, float c, float d) {
        if ((t /= d / 2) < 1) return c / 2 * t * t * t + b;
        return c / 2 * ((t -= 2) * t * t + 2) + b;
    }

    static private float quartInout(float x, float t, float b, float c, float d) {
        if ((t /= d / 2) < 1) return c / 2 * t * t * t * t + b;
        return -c / 2 * ((t -= 2) * t * t * t - 2) + b;
    }

    static private float quintInout(float x, float t, float b, float c, float d) {
        if ((t /= d / 2) < 1) return c / 2 * t * t * t * t * t + b;
        return c / 2 * ((t -= 2) * t * t * t * t + 2) + b;
    }
}
