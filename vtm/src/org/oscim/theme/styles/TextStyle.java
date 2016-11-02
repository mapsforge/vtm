/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
 * Copyright 2016 Andrey Novikov
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
import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint;
import org.oscim.backend.canvas.Paint.Align;
import org.oscim.backend.canvas.Paint.FontFamily;
import org.oscim.backend.canvas.Paint.FontStyle;
import org.oscim.renderer.atlas.TextureRegion;

public final class TextStyle extends RenderStyle<TextStyle> {

    public static class TextBuilder<T extends TextBuilder<T>> extends StyleBuilder<T> {

        public float fontSize;

        public String textKey;
        public boolean caption;
        public float dy;
        public int priority;
        public float areaSize;
        public Bitmap bitmap;
        public TextureRegion texture;
        public FontFamily fontFamily;
        public FontStyle fontStyle;

        public T reset() {
            fontFamily = FontFamily.DEFAULT;
            fontStyle = FontStyle.NORMAL;
            style = null;
            textKey = null;
            fontSize = 0;
            caption = false;
            priority = Integer.MAX_VALUE;
            areaSize = 0f;
            bitmap = null;
            texture = null;
            fillColor = Color.BLACK;
            strokeColor = Color.BLACK;
            strokeWidth = 0;
            dy = 0;
            return self();
        }

        public TextBuilder() {
            reset();
        }

        public TextStyle build() {
            TextStyle t = new TextStyle(this);
            t.fontHeight = t.paint.getFontHeight();
            t.fontDescent = t.paint.getFontDescent();
            return t;
        }

        public TextStyle buildInternal() {
            return new TextStyle(this);
        }

        public T fontSize(float fontSize) {
            this.fontSize = fontSize;
            return self();
        }

        public T textKey(String textKey) {
            this.textKey = textKey;
            return self();
        }

        public T isCaption(boolean caption) {
            this.caption = caption;
            return self();
        }

        public T offsetY(float dy) {
            this.dy = dy;
            return self();
        }

        public T priority(int priority) {
            this.priority = priority;
            return self();
        }

        public T areaSize(float areaSize) {
            this.areaSize = areaSize;
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

        public T fontFamily(FontFamily fontFamily) {
            this.fontFamily = fontFamily;
            return self();
        }

        public T fontStyle(FontStyle fontStyle) {
            this.fontStyle = fontStyle;
            return self();
        }

        public T from(TextBuilder<?> other) {
            fontFamily = other.fontFamily;
            fontStyle = other.fontStyle;
            style = other.style;
            textKey = other.textKey;
            fontSize = other.fontSize;
            caption = other.caption;
            priority = other.priority;
            areaSize = other.areaSize;
            bitmap = other.bitmap;
            texture = other.texture;
            fillColor = other.fillColor;
            strokeColor = other.strokeColor;
            strokeWidth = other.strokeWidth;
            dy = other.dy;
            return self();
        }

        public TextBuilder<?> from(TextStyle style) {
            this.style = style.style;
            this.textKey = style.textKey;
            this.caption = style.caption;
            this.dy = style.dy;
            this.priority = style.priority;
            this.areaSize = style.areaSize;
            this.bitmap = style.bitmap;
            this.texture = style.texture;
            this.fillColor = style.paint.getColor();
            this.fontFamily = FontFamily.DEFAULT;
            this.fontStyle = FontStyle.NORMAL;
            this.strokeColor = style.stroke.getColor();
            this.strokeWidth = 2;
            this.fontSize = style.fontSize;
            return self();
        }
    }

    TextStyle(TextBuilder<?> tb) {
        this.style = tb.style;
        this.textKey = tb.textKey;
        this.caption = tb.caption;
        this.dy = tb.dy;
        this.priority = tb.priority;
        this.areaSize = tb.areaSize;
        this.bitmap = tb.bitmap;
        this.texture = tb.texture;

        paint = CanvasAdapter.newPaint();
        paint.setTextAlign(Align.CENTER);
        paint.setTypeface(tb.fontFamily, tb.fontStyle);

        paint.setColor(tb.fillColor);
        paint.setTextSize(tb.fontSize);

        if (tb.strokeWidth > 0) {
            stroke = CanvasAdapter.newPaint();
            stroke.setStyle(Paint.Style.STROKE);
            stroke.setTextAlign(Align.CENTER);
            stroke.setTypeface(tb.fontFamily, tb.fontStyle);
            stroke.setColor(tb.strokeColor);
            stroke.setStrokeWidth(tb.strokeWidth);
            stroke.setTextSize(tb.fontSize);
        } else
            stroke = null;

        this.fontSize = tb.fontSize;
    }

    public final String style;

    public final float fontSize;
    public final Paint paint;
    public final Paint stroke;
    public final String textKey;

    public final boolean caption;
    public final float dy;
    public final int priority;
    public final float areaSize;

    public float fontHeight;
    public float fontDescent;

    public final Bitmap bitmap;
    public final TextureRegion texture;

    @Override
    public void dispose() {
        if (bitmap != null)
            bitmap.recycle();
    }

    @Override
    public void renderNode(Callback cb) {
        cb.renderText(this);
    }

    @Override
    public void renderWay(Callback cb) {
        cb.renderText(this);
    }

    @Override
    public TextStyle current() {
        return (TextStyle) mCurrent;
    }

    @Override
    public void scaleTextSize(float scaleFactor) {
        paint.setTextSize(fontSize * scaleFactor);
        if (stroke != null)
            stroke.setTextSize(fontSize * scaleFactor);

        fontHeight = paint.getFontHeight();
        fontDescent = paint.getFontDescent();
    }

    @SuppressWarnings("rawtypes")
    public static TextBuilder<?> builder() {
        return new TextBuilder();
    }
}
