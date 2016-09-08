/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
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
package org.oscim.theme.styles;

import org.oscim.backend.canvas.Color;

public class ExtrusionStyle extends RenderStyle<ExtrusionStyle> {

    public ExtrusionStyle(int level, int colorSides, int colorTop, int colorLine, int defaultHeight) {

        this.colors = new float[16];
        fillColors(colorSides, colorTop, colorLine, colors);

        this.defaultHeight = defaultHeight;
        this.level = level;
    }

    public static void fillColors(int sides, int top, int lines, float[] colors) {
        float a = Color.aToFloat(top);
        colors[0] = a * Color.rToFloat(top);
        colors[1] = a * Color.gToFloat(top);
        colors[2] = a * Color.bToFloat(top);
        colors[3] = a;

        a = Color.aToFloat(sides);
        colors[4] = a * Color.rToFloat(sides);
        colors[5] = a * Color.gToFloat(sides);
        colors[6] = a * Color.bToFloat(sides);
        colors[7] = a;

        a = Color.aToFloat(sides);
        colors[8] = a * Color.rToFloat(sides);
        colors[9] = a * Color.gToFloat(sides);
        colors[10] = a * Color.bToFloat(sides);
        colors[11] = a;

        a = Color.aToFloat(lines);
        colors[12] = a * Color.rToFloat(lines);
        colors[13] = a * Color.gToFloat(lines);
        colors[14] = a * Color.bToFloat(lines);
        colors[15] = a;
    }

    @Override
    public void renderWay(Callback cb) {
        cb.renderExtrusion(this, this.level);
    }

    @Override
    public ExtrusionStyle current() {
        return (ExtrusionStyle) mCurrent;
    }

    private final int level;
    public final float[] colors;
    public final int defaultHeight;
}
