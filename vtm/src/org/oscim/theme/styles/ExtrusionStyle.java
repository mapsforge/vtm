/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2017 devemux86
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

import static org.oscim.backend.canvas.Color.parseColor;

public class ExtrusionStyle extends RenderStyle<ExtrusionStyle> {

    public final int colorLine;
    public final int colorSide;
    public final int colorTop;
    public final int defaultHeight;
    private final int level;

    public final float[] colors;

    public ExtrusionStyle(int level, int colorSide, int colorTop, int colorLine, int defaultHeight) {
        this.level = level;

        this.colorSide = colorSide;
        this.colorTop = colorTop;
        this.colorLine = colorLine;
        this.colors = new float[16];
        fillColors(colorSide, colorTop, colorLine, colors);

        this.defaultHeight = defaultHeight;
    }

    public ExtrusionStyle(ExtrusionBuilder<?> b) {
        this.cat = b.cat;
        this.level = b.level;

        this.colorSide = b.themeCallback != null ? b.themeCallback.getColor(b.colorSide) : b.colorSide;
        this.colorTop = b.themeCallback != null ? b.themeCallback.getColor(b.colorTop) : b.colorTop;
        this.colorLine = b.themeCallback != null ? b.themeCallback.getColor(b.colorLine) : b.colorLine;
        this.colors = new float[16];
        fillColors(colorSide, colorTop, colorLine, colors);

        this.defaultHeight = b.defaultHeight;
    }

    public static void fillColors(int side, int top, int line, float[] colors) {
        float a = Color.aToFloat(top);
        colors[0] = a * Color.rToFloat(top);
        colors[1] = a * Color.gToFloat(top);
        colors[2] = a * Color.bToFloat(top);
        colors[3] = a;

        a = Color.aToFloat(side);
        colors[4] = a * Color.rToFloat(side);
        colors[5] = a * Color.gToFloat(side);
        colors[6] = a * Color.bToFloat(side);
        colors[7] = a;

        a = Color.aToFloat(side);
        colors[8] = a * Color.rToFloat(side);
        colors[9] = a * Color.gToFloat(side);
        colors[10] = a * Color.bToFloat(side);
        colors[11] = a;

        a = Color.aToFloat(line);
        colors[12] = a * Color.rToFloat(line);
        colors[13] = a * Color.gToFloat(line);
        colors[14] = a * Color.bToFloat(line);
        colors[15] = a;
    }

    @Override
    public ExtrusionStyle current() {
        return (ExtrusionStyle) mCurrent;
    }

    @Override
    public void renderWay(Callback cb) {
        cb.renderExtrusion(this, this.level);
    }

    public static class ExtrusionBuilder<T extends ExtrusionBuilder<T>> extends StyleBuilder<T> {

        public int colorSide;
        public int colorTop;
        public int colorLine;
        public int defaultHeight;

        public ExtrusionBuilder() {
        }

        public T set(ExtrusionStyle extrusion) {
            if (extrusion == null)
                return reset();

            this.cat = extrusion.cat;
            this.level = extrusion.level;
            this.colorSide = themeCallback != null ? themeCallback.getColor(extrusion.colorSide) : extrusion.colorSide;
            this.colorTop = themeCallback != null ? themeCallback.getColor(extrusion.colorTop) : extrusion.colorTop;
            this.colorLine = themeCallback != null ? themeCallback.getColor(extrusion.colorLine) : extrusion.colorLine;
            this.defaultHeight = extrusion.defaultHeight;

            return self();
        }

        public T colorSide(int colorSide) {
            this.colorSide = colorSide;
            return self();
        }

        public T colorSide(String colorSide) {
            this.colorSide = parseColor(colorSide);
            return self();
        }

        public T colorTop(int colorTop) {
            this.colorTop = colorTop;
            return self();
        }

        public T colorTop(String colorTop) {
            this.colorTop = parseColor(colorTop);
            return self();
        }

        public T colorLine(int colorLine) {
            this.colorLine = colorLine;
            return self();
        }

        public T colorLine(String colorLine) {
            this.colorLine = parseColor(colorLine);
            return self();
        }

        public T defaultHeight(int defaultHeight) {
            this.defaultHeight = defaultHeight;
            return self();
        }

        public T reset() {
            cat = null;
            level = -1;
            colorSide = Color.TRANSPARENT;
            colorTop = Color.TRANSPARENT;
            colorLine = Color.TRANSPARENT;
            defaultHeight = 12; // 12m default
            return self();
        }

        public ExtrusionStyle build() {
            return new ExtrusionStyle(this);
        }
    }

    @SuppressWarnings("rawtypes")
    public static ExtrusionBuilder<?> builder() {
        return new ExtrusionBuilder();
    }
}
