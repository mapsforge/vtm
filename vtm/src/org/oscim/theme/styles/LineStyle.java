/*
 * Copyright 2010, 2011, 2012 mapsforge.org
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
import org.oscim.backend.canvas.Paint.Cap;
import org.oscim.renderer.bucket.TextureItem;

import static org.oscim.backend.canvas.Color.parseColor;

public final class LineStyle extends RenderStyle<LineStyle> {

    final int level;
    public final String style;
    public final float width;
    public final int color;
    public final Cap cap;
    public final boolean outline;
    public final boolean fixed;
    public final int fadeScale;
    public final float blur;

    public final int stipple;
    public final int stippleColor;
    public final float stippleWidth;
    public final TextureItem texture;

    public final boolean randomOffset;

    private LineStyle(LineBuilder<?> builder) {
        this.level = builder.level;
        this.style = builder.style;
        this.width = builder.strokeWidth;
        this.color = builder.fillColor;
        this.cap = builder.cap;
        this.outline = builder.outline;
        this.fixed = builder.fixed;
        this.fadeScale = builder.fadeScale;
        this.blur = builder.blur;
        this.stipple = builder.stipple;
        this.stippleColor = builder.stippleColor;
        this.stippleWidth = builder.stippleWidth;
        this.texture = builder.texture;
        this.randomOffset = builder.randomOffset;
    }

    public LineStyle(int level, String style, int color, float width,
                     Cap cap, boolean fixed,
                     int stipple, int stippleColor, float stippleWidth,
                     int fadeScale, float blur, boolean isOutline, TextureItem texture,
                     boolean randomOffset) {

        this.level = level;
        this.style = style;
        this.outline = isOutline;

        this.cap = cap;
        this.color = color;
        this.width = width;
        this.fixed = fixed;

        this.stipple = stipple;
        this.stippleColor = stippleColor;
        this.stippleWidth = stippleWidth;
        this.texture = texture;

        this.blur = blur;
        this.fadeScale = fadeScale;

        this.randomOffset = randomOffset;
    }

    public LineStyle(int stroke, float width) {
        this(0, "", stroke, width, Cap.BUTT, true, 0, 0, 0, -1, 0, false, null, true);
    }

    public LineStyle(int level, int stroke, float width) {
        this(level, "", stroke, width, Cap.BUTT, true, 0, 0, 0, -1, 0, false, null, true);
    }

    public LineStyle(int stroke, float width, Cap cap) {
        this(0, "", stroke, width, cap, true, 0, 0, 0, -1, 0, false, null, true);
    }

    @Override
    public void renderWay(Callback cb) {
        cb.renderWay(this, level);
    }

    @Override
    public LineStyle current() {
        return (LineStyle) mCurrent;
    }

    public static class LineBuilder<T extends LineBuilder<T>> extends StyleBuilder<T> {

        public Cap cap;
        public boolean outline;
        public boolean fixed;
        public int fadeScale;
        public float blur;

        public int stipple;
        public int stippleColor;
        public float stippleWidth;
        public TextureItem texture;

        public boolean randomOffset;

        public T set(LineStyle line) {
            if (line == null)
                return reset();
            this.level = line.level;
            this.style = line.style;
            this.strokeWidth = line.width;
            this.fillColor = line.color;
            this.cap = line.cap;
            this.outline = line.outline;
            this.fixed = line.fixed;
            this.fadeScale = line.fadeScale;
            this.blur = line.blur;
            this.stipple = line.stipple;
            this.stippleColor = line.stippleColor;
            this.stippleWidth = line.stippleWidth;
            this.texture = line.texture;
            this.randomOffset = line.randomOffset;
            return self();
        }

        public T reset() {
            level = -1;
            style = null;
            fillColor = Color.BLACK;
            cap = Cap.ROUND;
            strokeWidth = 1;
            fixed = false;

            fadeScale = -1;
            blur = 0;

            stipple = 0;
            stippleWidth = 1;
            stippleColor = Color.BLACK;
            texture = null;

            randomOffset = true;

            return self();
        }

        public T blur(float blur) {
            this.blur = blur;
            return self();
        }

        public T fadeScale(int zoom) {
            this.fadeScale = zoom;
            return self();
        }

        public T stipple(int width) {
            this.stipple = width;
            return self();
        }

        public T stippleColor(int color) {
            this.stippleColor = color;
            return self();
        }

        public T stippleColor(String color) {
            this.stippleColor = parseColor(color);
            return self();
        }

        public T stippleWidth(float width) {
            this.stippleWidth = width;
            return self();
        }

        public T isOutline(boolean outline) {
            this.outline = outline;
            return self();
        }

        public LineStyle build() {
            return new LineStyle(this);
        }

        public T cap(Cap cap) {
            this.cap = cap;
            return self();
        }

        public T fixed(boolean b) {
            this.fixed = b;
            return self();
        }

        public T texture(TextureItem texture) {
            this.texture = texture;
            return self();
        }

        public T randomOffset(boolean randomOffset) {
            this.randomOffset = randomOffset;
            return self();
        }
    }

    @SuppressWarnings("rawtypes")
    public static LineBuilder<?> builder() {
        return new LineBuilder();
    }
}
