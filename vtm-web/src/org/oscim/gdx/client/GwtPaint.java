/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2017 devemux86
 * Copyright 2017 nebular
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
package org.oscim.gdx.client;

import com.badlogic.gdx.graphics.Pixmap;

import org.oscim.backend.canvas.Paint;

public class GwtPaint implements Paint {

    private Align align;
    String color;
    boolean stroke;

    float strokeWidth;

    float fontSize = 12;

    private FontStyle fontStyle = FontStyle.NORMAL;
    private FontFamily fontFamily = FontFamily.DEFAULT;

    //String font = "12px sans-serif";
    String font = "13px Helvetica";

    Style style;

    @Override
    public int getColor() {
        return 0;
    }

    @Override
    public void setColor(int color) {
        float a = ((color >>> 24) & 0xff) / 255f;
        int r = (color >>> 16) & 0xff;
        int g = (color >>> 8) & 0xff;
        int b = (color & 0xff);

        this.color = Pixmap.make(r, g, b, a);
    }

    @Override
    public void setStrokeCap(Cap cap) {
        stroke = true;
    }

    @Override
    public void setStrokeJoin(Join join) {
        stroke = true;
    }

    @Override
    public void setStrokeWidth(float width) {
        stroke = true;
        strokeWidth = width;
    }

    @Override
    public void setStyle(Style style) {
        this.style = style;
    }

    @Override
    public void setTextAlign(Align align) {
        // TODO never read
        this.align = align;
    }

    @Override
    public void setTextSize(float size) {
        fontSize = size;
        buildFont();
    }

    @Override
    public void setTypeface(FontFamily fontFamily, FontStyle fontStyle) {
        this.fontStyle = fontStyle;
        this.fontFamily = fontFamily;
        buildFont();
    }

    @Override
    public float measureText(String text) {
        return GwtGdxGraphics.getTextWidth(text, font);
    }

    // FIXME all estimates. no idea how to properly measure canvas text..
    @Override
    public float getFontHeight() {
        return 2 + fontSize + strokeWidth * 2;
    }

    @Override
    public float getFontDescent() {
        return 4 + strokeWidth;
    }

    private void buildFont() {
        StringBuilder sb = new StringBuilder();
        String weight = null; // Default 400 -> normal
        String name = "Helvetica";

        switch (this.fontFamily) {
            case MEDIUM:
                weight = "500";
                break;
            case BLACK:
                weight = "900";
                break;
            case DEFAULT_BOLD:
                weight = "bold"; // 700
                break;
            case LIGHT:
                weight = "300";
                break;
            case THIN:
                weight = "200"; //lighter
                break;
            case SERIF:
                name = "Georgia";
                break;
            case MONOSPACE:
                name = "'Courier New'";
                break;
            case CONDENSED:
                sb.append("condensed ");
        }

        switch (this.fontStyle) {
            case BOLD:
                weight = "bold";
                break;
            case ITALIC:
                sb.append("italic ");
                break;
            case BOLD_ITALIC:
                weight = "bold";
                sb.append("italic ");
        }
        if (weight != null)
            sb.append(weight).append(" ");
        sb.append(Math.round(this.fontSize));
        sb.append("px ").append(name);

        this.font = sb.toString();
    }

    @Override
    public float getStrokeWidth() {
        return strokeWidth;
    }

    @Override
    public Style getStyle() {
        return style;
    }

    @Override
    public float getTextHeight(String text) {
        // TODO
        return 0;
    }

    @Override
    public float getTextWidth(String text) {
        return measureText(text);
    }
}
