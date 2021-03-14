package org.oscim.jeo;

import io.jeo.map.RGB;

public class JeoUtils {
    public static int color(RGB rgb) {
        return rgb.getAlpha() << 24
                | rgb.getRed() << 16
                | rgb.getGreen() << 8
                | rgb.getBlue();
    }
}
