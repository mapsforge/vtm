/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
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
package org.oscim.backend.canvas;

/**
 * The Interface Paint.
 */
public interface Paint {

    enum Align {
        CENTER, LEFT, RIGHT
    }

    enum Cap {
        BUTT, ROUND, SQUARE
    }

    enum Join {
        MITER, ROUND, BEVEL
    }

    enum Style {
        FILL, STROKE
    }

    enum FontFamily {
        DEFAULT, DEFAULT_BOLD, MONOSPACE, SANS_SERIF, SERIF
    }

    enum FontStyle {
        BOLD, BOLD_ITALIC, ITALIC, NORMAL
    }

    int getColor();

    void setColor(int color);

    void setStrokeCap(Cap cap);

    void setStrokeJoin(Join join);

    void setStrokeWidth(float width);

    void setStyle(Style style);

    void setTextAlign(Align align);

    void setTextSize(float textSize);

    void setTypeface(FontFamily fontFamily, FontStyle fontStyle);

    float measureText(String text);

    float getFontHeight();

    float getFontDescent();

    float getStrokeWidth();

    Style getStyle();

    float getTextHeight(String text);

    float getTextWidth(String text);
}
