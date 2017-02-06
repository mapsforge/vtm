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

import org.oscim.theme.ThemeCallback;

import static org.oscim.backend.canvas.Color.parseColor;

/**
 * A RenderInstruction is a basic graphical primitive to draw a map.
 */
public abstract class RenderStyle<T extends RenderStyle<T>> {

    public static abstract class StyleBuilder<T extends StyleBuilder<T>> {
        public String cat;
        public String style;

        public int level;

        public int fillColor;

        public int strokeColor;
        public float strokeWidth;

        public ThemeCallback themeCallback;

        public T cat(String cat) {
            this.cat = cat;
            return self();
        }

        public T style(String style) {
            this.style = style;
            return self();
        }

        public T level(int level) {
            this.level = level;
            return self();
        }

        public T outline(int color, float width) {
            this.strokeColor = color;
            this.strokeWidth = width;
            return self();
        }

        public T strokeColor(int color) {
            this.strokeColor = color;
            return self();
        }

        public T strokeColor(String color) {
            this.strokeColor = parseColor(color);
            return self();
        }

        public T strokeWidth(float width) {
            this.strokeWidth = width;
            return self();
        }

        public T color(int color) {
            this.fillColor = color;
            return self();
        }

        public T color(String color) {
            this.fillColor = parseColor(color);
            return self();
        }

        public T themeCallback(ThemeCallback themeCallback) {
            this.themeCallback = themeCallback;
            return self();
        }

        @SuppressWarnings("unchecked")
        protected T self() {
            return (T) this;
        }

        public abstract RenderStyle build();
    }

    /**
     * Callback methods for rendering areas, ways and points of interest (POIs).
     */
    public interface Callback {
        /**
         * Renders an area with the given parameters.
         */
        void renderArea(AreaStyle area, int level);

        /**
         * Renders an extrusion with the given parameters.
         */
        void renderExtrusion(ExtrusionStyle extrusion, int level);

        /**
         * Renders a point of interest circle with the given parameters.
         *
         * @param circle the circle.
         * @param level  the drawing level on which the circle should be rendered.
         */
        void renderCircle(CircleStyle circle, int level);

        /**
         * Renders a point of interest symbol with the given bitmap.
         *
         * @param symbol the symbol to be rendered.
         */
        void renderSymbol(SymbolStyle symbol);

        /**
         * Renders a way with the given parameters.
         */
        void renderWay(LineStyle line, int level);

        /**
         * Renders a way with the given text along the way path.
         */
        void renderText(TextStyle text);

    }

    /**
     * Category
     */
    public String cat = null;

    RenderStyle mCurrent = this;
    RenderStyle mNext;
    boolean update;

    public T setCat(String cat) {
        this.cat = cat;
        return self();
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public void set(RenderStyle next) {
        update = true;
        mNext = next;
    }

    public void unsetOverride() {
        update = true;
        mNext = null;
    }

    /**
     * Destroys this RenderInstruction and cleans up all its internal resources.
     */
    public void dispose() {
    }

    /**
     * @param renderCallback a reference to the receiver of all render callbacks.
     */
    public void renderNode(Callback renderCallback) {
    }

    /**
     * @param renderCallback a reference to the receiver of all render callbacks.
     */
    public void renderWay(Callback renderCallback) {
    }

    /**
     * Scales the text size of this RenderInstruction by the given factor.
     *
     * @param scaleFactor the factor by which the text size should be scaled.
     */
    public void scaleTextSize(float scaleFactor) {
    }

    public void update() {
        if (update) {
            update = false;
            mCurrent = mNext;
        }
    }

    public abstract RenderStyle current();
}
