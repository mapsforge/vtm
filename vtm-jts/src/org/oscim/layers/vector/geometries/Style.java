/*
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
package org.oscim.layers.vector.geometries;

import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint;
import org.oscim.renderer.bucket.TextureItem;

import static org.oscim.backend.canvas.Color.parseColor;

/**
 * Class encapsulating style information for drawing geometries on the map.
 */
public class Style {

    public static final int GENERALIZATION_HIGH = 1 << 3;
    public static final int GENERALIZATION_MEDIUM = 1 << 2;
    public static final int GENERALIZATION_SMALL = 1 << 0;
    public static final int GENERALIZATION_NONE = 0;

    public final float strokeWidth;
    public final int strokeColor;

    public final int fillColor;
    public final float fillAlpha;

    public final double buffer;
    public final int scalingZoomLevel;

    public final int generalization;

    public final Paint.Cap cap;
    public final boolean fixed;
    public final int stipple;
    public final int stippleColor;
    public final float stippleWidth;
    public final TextureItem texture;

    public final float heightOffset;
    public final boolean randomOffset;

    private Style(Builder builder) {
        strokeWidth = builder.strokeWidth;
        strokeColor = builder.strokeColor;

        fillColor = builder.fillColor;
        fillAlpha = builder.fillAlpha;

        buffer = builder.buffer;
        scalingZoomLevel = builder.scalingZoomLevel;

        generalization = builder.generalization;

        cap = builder.cap;
        fixed = builder.fixed;
        stipple = builder.stipple;
        stippleColor = builder.stippleColor;
        stippleWidth = builder.stippleWidth;
        texture = builder.texture;

        heightOffset = builder.heightOffset;
        randomOffset = builder.randomOffset;
    }

    /**
     * Geometry style builder. Usage example:
     * <p/>
     * <pre>
     * {
     *     Style style = Style.builder()
     *         .strokeWidth(1f).strokeColor(Color.BLACK).build();
     * }
     * </pre>
     */
    public static class Builder {

        private float strokeWidth = 1f;
        private int strokeColor = Color.GRAY;
        private int fillColor = Color.GRAY;
        private float fillAlpha = 0.25f;

        private double buffer = 1;
        private int scalingZoomLevel = 1;

        private int generalization = GENERALIZATION_NONE;

        public Paint.Cap cap = Paint.Cap.ROUND;
        public boolean fixed = false;
        public int stipple = 0;
        public int stippleColor = Color.GRAY;
        public float stippleWidth = 1;
        public TextureItem texture = null;

        public float heightOffset = 0;
        public boolean randomOffset = true;

        protected Builder() {
        }

        /**
         * Builds the GeometryStyle from the specified parameters.
         */
        public Style build() {
            return new Style(this);
        }

        /**
         * Sets the line width for the geometry's line or outline.
         */
        public Builder strokeWidth(float lineWidth) {
            this.strokeWidth = lineWidth;
            return this;
        }

        /**
         * Sets the color for the geometry's line or outline.
         */
        public Builder strokeColor(int strokeColor) {
            this.strokeColor = strokeColor;
            return this;
        }

        /**
         * Sets the color for the geometry's line or outline.
         */
        public Builder strokeColor(String strokeColor) {
            this.strokeColor = parseColor(strokeColor);
            return this;
        }

        /**
         * Sets the color for the geometry's area.
         */
        public Builder fillColor(int fillColor) {
            this.fillColor = fillColor;
            return this;
        }

        /**
         * Sets the color for the geometry's area.
         */
        public Builder fillColor(String fillColor) {
            this.fillColor = parseColor(fillColor);
            return this;
        }

        /**
         * Sets alpha channel value for the geometry's area.
         */
        public Builder fillAlpha(float fillAlpha) {
            this.fillAlpha = fillAlpha;
            return this;
        }

        /**
         * This function has effect only on Points:
         * use it to control the size on the circle that
         * will be built from a buffer around the point.
         */
        public Builder buffer(double buffer) {
            this.buffer = buffer;
            return this;
        }

        /**
         * This function has effect only on Points:
         * use it to specify from which zoom level the point
         * should stop decreasing in size and "stick to the map".
         */
        public Builder scaleZoomLevel(int zoomlvl) {
            this.scalingZoomLevel = zoomlvl;
            return this;
        }

        /**
         * Sets generalization factor for the geometry.
         * Use predefined GeometryStyle.GENERALIZATION_HIGH,
         * GENERALIZATION_MEDIUM or GENERALIZATION_SMALL
         */
        public Builder generalization(int generalization) {
            this.generalization = generalization;
            return this;
        }

        public Builder cap(Paint.Cap cap) {
            this.cap = cap;
            return this;
        }

        public Builder fixed(boolean b) {
            this.fixed = b;
            return this;
        }

        public Builder stipple(int width) {
            this.stipple = width;
            return this;
        }

        public Builder stippleColor(int color) {
            this.stippleColor = color;
            return this;
        }

        public Builder stippleColor(String color) {
            this.stippleColor = parseColor(color);
            return this;
        }

        public Builder stippleWidth(float width) {
            this.stippleWidth = width;
            return this;
        }

        public Builder texture(TextureItem texture) {
            this.texture = texture;
            return this;
        }

        public Builder heightOffset(float heightOffset) {
            this.heightOffset = heightOffset;
            return this;
        }

        public Builder randomOffset(boolean randomOffset) {
            this.randomOffset = randomOffset;
            return this;
        }
    }

    static final Style DEFAULT_STYLE = new Builder()
            .fillColor(0xcccccccc)
            .fillAlpha(1)
            .build();

    public static Style defaultStyle() {
        return DEFAULT_STYLE;
    }

    public static Style.Builder builder() {
        return new Style.Builder();
    }
}
