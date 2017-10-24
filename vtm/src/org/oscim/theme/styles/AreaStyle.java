/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2014 Hannes Janetzek
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
import org.oscim.renderer.bucket.TextureItem;
import org.oscim.utils.FastMath;

import static org.oscim.backend.canvas.Color.parseColor;

/*TODO 
 * - add custom shaders
 * - create distance field per tile?
 */
public class AreaStyle extends RenderStyle<AreaStyle> {

    private static final float FADE_START = 0.25f;

    /**
     * Drawing order level
     */
    private final int level;

    /**
     * Style name
     */
    public final String style;

    /**
     * Fill color
     */
    public final int color;

    /**
     * Fade-out zoom-level
     */
    public final int fadeScale;

    /**
     * Fade to blendColor zoom-level
     */
    public final int blendColor;

    /**
     * Blend fill color
     */
    public final int blendScale;

    /**
     * Pattern texture
     */
    public final TextureItem texture;

    /**
     * Outline
     */
    public final int strokeColor;
    public final float strokeWidth;

    /**
     * Tessellation
     */
    public boolean mesh;

    public final float heightOffset;

    public final int symbolWidth;
    public final int symbolHeight;
    public final int symbolPercent;

    public AreaStyle(int color) {
        this(0, color);
    }

    public AreaStyle(int level, int color) {
        this.level = level;
        this.style = "";
        this.fadeScale = -1;
        this.blendColor = 0;
        this.blendScale = -1;
        this.color = color;
        this.texture = null;
        this.strokeColor = color;
        this.strokeWidth = 1;
        this.mesh = false;

        this.heightOffset = 0;

        this.symbolWidth = 0;
        this.symbolHeight = 0;
        this.symbolPercent = 100;
    }

    public AreaStyle(AreaBuilder<?> b) {
        this.cat = b.cat;
        this.level = b.level;
        this.style = b.style;
        this.fadeScale = b.fadeScale;
        this.blendColor = b.themeCallback != null ? b.themeCallback.getColor(b.blendColor) : b.blendColor;
        this.blendScale = b.blendScale;
        this.color = b.themeCallback != null ? b.themeCallback.getColor(b.fillColor) : b.fillColor;
        this.texture = b.texture;
        this.strokeColor = b.themeCallback != null ? b.themeCallback.getColor(b.strokeColor) : b.strokeColor;
        this.strokeWidth = b.strokeWidth;
        this.mesh = b.mesh;

        this.heightOffset = b.heightOffset;

        this.symbolWidth = b.symbolWidth;
        this.symbolHeight = b.symbolHeight;
        this.symbolPercent = b.symbolPercent;
    }

    @Override
    public AreaStyle current() {
        return (AreaStyle) mCurrent;
    }

    @Override
    public void renderWay(Callback cb) {
        cb.renderArea(this, level);
    }

    public boolean hasAlpha(int zoom) {
        if (!Color.isOpaque(color))
            return true;

        if (texture != null)
            return true;

        if (blendScale < 0 && fadeScale < 0)
            return false;

        if (zoom >= blendScale) {
            if (!Color.isOpaque(blendColor))
                return true;
        }

        if (fadeScale <= zoom)
            return true;

        return false;
    }

    public float getFade(double scale) {
        if (fadeScale < 0)
            return 1;

        float f = (float) (scale / (1 << fadeScale)) - 1;
        return FastMath.clamp(f, FADE_START, 1);
    }

    public float getBlend(double scale) {
        if (blendScale < 0)
            return 0;

        float f = (float) (scale / (1 << blendScale)) - 1;
        return FastMath.clamp(f, 0, 1);
    }

    public static class AreaBuilder<T extends AreaBuilder<T>> extends StyleBuilder<T> {

        public int fadeScale;
        public int blendColor;
        public int blendScale;
        public boolean mesh;

        public TextureItem texture;

        public float heightOffset;

        public int symbolWidth;
        public int symbolHeight;
        public int symbolPercent;

        public AreaBuilder() {
        }

        public T set(AreaStyle area) {
            if (area == null)
                return reset();

            this.cat = area.cat;
            this.level = area.level;
            this.style = area.style;
            this.fadeScale = area.fadeScale;
            this.blendColor = themeCallback != null ? themeCallback.getColor(area.blendColor) : area.blendColor;
            this.blendScale = area.blendScale;
            this.fillColor = themeCallback != null ? themeCallback.getColor(area.color) : area.color;
            this.texture = area.texture;
            this.strokeColor = themeCallback != null ? themeCallback.getColor(area.strokeColor) : area.strokeColor;
            this.strokeWidth = area.strokeWidth;
            this.mesh = area.mesh;

            this.heightOffset = area.heightOffset;

            this.symbolWidth = area.symbolWidth;
            this.symbolHeight = area.symbolHeight;
            this.symbolPercent = area.symbolPercent;

            return self();
        }

        public T blendScale(int zoom) {
            this.blendScale = zoom;
            return self();
        }

        public T blendColor(int color) {
            this.blendColor = color;
            return self();
        }

        public T blendColor(String color) {
            this.blendColor = parseColor(color);
            return self();
        }

        public T texture(TextureItem texture) {
            this.texture = texture;
            return self();
        }

        public T fadeScale(int zoom) {
            this.fadeScale = zoom;
            return self();
        }

        public T mesh(boolean mesh) {
            this.mesh = mesh;
            return self();
        }

        public T heightOffset(float heightOffset) {
            this.heightOffset = heightOffset;
            return self();
        }

        public T symbolWidth(int symbolWidth) {
            this.symbolWidth = symbolWidth;
            return self();
        }

        public T symbolHeight(int symbolHeight) {
            this.symbolHeight = symbolHeight;
            return self();
        }

        public T symbolPercent(int symbolPercent) {
            this.symbolPercent = symbolPercent;
            return self();
        }

        public T reset() {
            cat = null;
            level = -1;
            fillColor = Color.WHITE;
            strokeColor = Color.BLACK;
            strokeWidth = 0;
            fadeScale = -1;
            blendScale = -1;
            blendColor = Color.TRANSPARENT;
            style = null;
            texture = null;
            mesh = false;

            heightOffset = 0;

            symbolWidth = 0;
            symbolHeight = 0;
            symbolPercent = 100;

            return self();
        }

        public AreaStyle build() {
            return new AreaStyle(this);
        }
    }

    @SuppressWarnings("rawtypes")
    public static AreaBuilder<?> builder() {
        return new AreaBuilder();
    }
}
