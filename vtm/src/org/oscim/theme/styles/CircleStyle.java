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

/**
 * Represents a round area on the map.
 */
public final class CircleStyle extends RenderStyle<CircleStyle> {

    public final int fillColor;
    private final int level;
    public final float radius;
    public final boolean scaleRadius;
    public final int strokeColor;
    public final float strokeWidth;

    public CircleStyle(float radius, boolean scaleRadius, int fillColor, int strokeColor,
                       float strokeWidth, int level) {
        this.radius = radius;
        this.scaleRadius = scaleRadius;
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.level = level;
    }

    public CircleStyle(CircleBuilder<?> b) {
        this.cat = b.cat;
        this.radius = b.radius;
        this.scaleRadius = b.scaleRadius;
        this.fillColor = b.themeCallback != null ? b.themeCallback.getColor(b.fillColor) : b.fillColor;
        this.strokeColor = b.themeCallback != null ? b.themeCallback.getColor(b.strokeColor) : b.strokeColor;
        this.strokeWidth = b.strokeWidth;
        this.level = b.level;
    }

    @Override
    public CircleStyle current() {
        return (CircleStyle) mCurrent;
    }

    @Override
    public void renderNode(Callback cb) {
        cb.renderCircle(this, this.level);
    }

    public static class CircleBuilder<T extends CircleBuilder<T>> extends StyleBuilder<T> {

        public float radius;
        public boolean scaleRadius;

        public CircleBuilder() {
        }

        public T set(CircleStyle circle) {
            if (circle == null)
                return reset();

            this.radius = circle.radius;
            this.scaleRadius = circle.scaleRadius;
            this.fillColor = themeCallback != null ? themeCallback.getColor(circle.fillColor) : circle.fillColor;
            this.strokeColor = themeCallback != null ? themeCallback.getColor(circle.strokeColor) : circle.strokeColor;
            this.strokeWidth = circle.strokeWidth;
            this.cat = circle.cat;
            this.level = circle.level;

            return self();
        }

        public T radius(float radius) {
            this.radius = radius;
            return self();
        }

        public T scaleRadius(boolean scaleRadius) {
            this.scaleRadius = scaleRadius;
            return self();
        }

        public T reset() {
            cat = null;
            level = -1;
            radius = 0;
            scaleRadius = false;
            fillColor = Color.TRANSPARENT;
            strokeColor = Color.TRANSPARENT;
            strokeWidth = 0;
            return self();
        }

        public CircleStyle build() {
            return new CircleStyle(this);
        }
    }

    @SuppressWarnings("rawtypes")
    public static CircleBuilder<?> builder() {
        return new CircleBuilder();
    }
}
