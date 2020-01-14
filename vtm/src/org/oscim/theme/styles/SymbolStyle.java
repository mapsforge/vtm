/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2019 devemux86
 * Copyright 2017 Longri
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

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.renderer.atlas.TextureRegion;

/**
 * Represents an icon on the map.
 */
public final class SymbolStyle extends RenderStyle<SymbolStyle> {

    public static final float REPEAT_START_DEFAULT = 30f;
    public static final float REPEAT_GAP_DEFAULT = 200f;

    public final Bitmap bitmap;
    public final TextureRegion texture;
    public final int hash;

    public final int symbolWidth;
    public final int symbolHeight;
    public final int symbolPercent;

    // Symbols on lines
    public final boolean billboard;
    public final boolean repeat;
    public final float repeatStart;
    public final float repeatGap;
    public final boolean rotate;

    public SymbolStyle(Bitmap bitmap) {
        this(bitmap, null, 0);
    }

    public SymbolStyle(TextureRegion texture) {
        this(null, texture, 0);
    }

    public SymbolStyle(int hash) {
        this(null, null, hash);
    }

    private SymbolStyle(Bitmap bitmap, TextureRegion texture, int hash) {
        this.bitmap = bitmap;
        this.texture = texture;
        this.hash = hash;

        this.symbolWidth = 0;
        this.symbolHeight = 0;
        this.symbolPercent = 100;

        this.billboard = false;
        this.repeat = false;
        this.repeatStart = REPEAT_START_DEFAULT * CanvasAdapter.getScale();
        this.repeatGap = REPEAT_GAP_DEFAULT * CanvasAdapter.getScale();
        this.rotate = true;
    }

    public SymbolStyle(SymbolBuilder<?> b) {
        this.cat = b.cat;

        this.bitmap = b.bitmap;
        this.texture = b.texture;
        this.hash = b.hash;

        this.symbolWidth = b.symbolWidth;
        this.symbolHeight = b.symbolHeight;
        this.symbolPercent = b.symbolPercent;

        this.billboard = b.billboard;
        this.repeat = b.repeat;
        this.repeatStart = b.repeatStart;
        this.repeatGap = b.repeatGap;
        this.rotate = b.rotate;
    }

    @Override
    public SymbolStyle current() {
        return (SymbolStyle) mCurrent;
    }

    @Override
    public void dispose() {
        if (bitmap != null)
            bitmap.recycle();
    }

    @Override
    public void renderNode(Callback cb) {
        cb.renderSymbol(this);
    }

    @Override
    public void renderWay(Callback cb) {
        cb.renderSymbol(this);
    }

    public static class SymbolBuilder<T extends SymbolBuilder<T>> extends StyleBuilder<T> {

        public Bitmap bitmap;
        public TextureRegion texture;
        public int hash;

        public int symbolWidth;
        public int symbolHeight;
        public int symbolPercent;

        // Symbols on lines
        public boolean billboard;
        public boolean repeat;
        public float repeatStart;
        public float repeatGap;
        public boolean rotate;

        public SymbolBuilder() {
        }

        public T set(SymbolStyle symbol) {
            if (symbol == null)
                return reset();

            this.cat = symbol.cat;

            this.bitmap = symbol.bitmap;
            this.texture = symbol.texture;
            this.hash = symbol.hash;

            this.symbolWidth = symbol.symbolWidth;
            this.symbolHeight = symbol.symbolHeight;
            this.symbolPercent = symbol.symbolPercent;

            this.billboard = symbol.billboard;
            this.repeat = symbol.repeat;
            this.repeatStart = symbol.repeatStart;
            this.repeatGap = symbol.repeatGap;
            this.rotate = symbol.rotate;

            return self();
        }

        public T bitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            return self();
        }

        public T texture(TextureRegion texture) {
            this.texture = texture;
            return self();
        }

        public T hash(int hash) {
            this.hash = hash;
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

        public T billboard(boolean billboard) {
            this.billboard = billboard;
            return self();
        }

        public T repeat(boolean repeat) {
            this.repeat = repeat;
            return self();
        }

        public T repeatStart(float repeatStart) {
            this.repeatStart = repeatStart;
            return self();
        }

        public T repeatGap(float repeatGap) {
            this.repeatGap = repeatGap;
            return self();
        }

        public T rotate(boolean rotate) {
            this.rotate = rotate;
            return self();
        }

        public T reset() {
            cat = null;

            bitmap = null;
            texture = null;
            hash = 0;

            symbolWidth = 0;
            symbolHeight = 0;
            symbolPercent = 100;

            billboard = false;
            repeat = false;
            repeatStart = REPEAT_START_DEFAULT * CanvasAdapter.getScale();
            repeatGap = REPEAT_GAP_DEFAULT * CanvasAdapter.getScale();
            rotate = true;

            return self();
        }

        @Override
        public SymbolStyle build() {
            return new SymbolStyle(this);
        }
    }

    @SuppressWarnings("rawtypes")
    public static SymbolBuilder<?> builder() {
        return new SymbolBuilder();
    }
}
