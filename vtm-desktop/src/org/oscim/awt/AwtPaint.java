/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
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
package org.oscim.awt;

import org.oscim.backend.canvas.Paint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AwtPaint implements Paint {

    private static int getCap(Cap cap) {
        switch (cap) {
            case BUTT:
                return BasicStroke.CAP_BUTT;
            case ROUND:
                return BasicStroke.CAP_ROUND;
            case SQUARE:
                return BasicStroke.CAP_SQUARE;
        }

        throw new IllegalArgumentException("unknown cap: " + cap);
    }

    private static Font getFont(FontFamily fontFamily, FontStyle fontStyle, int textSize) {
        Map<Attribute, Object> attributes = null;
        String name = null;

        switch (fontFamily) {
            case MONOSPACE:
                attributes = TEXT_ATTRIBUTES;
                name = Font.MONOSPACED;
                break;
            case SANS_SERIF:
                attributes = TEXT_ATTRIBUTES;
                name = Font.SANS_SERIF;
                break;
            case SERIF:
                attributes = TEXT_ATTRIBUTES;
                name = Font.SERIF;
                break;
            case THIN:
                attributes = new HashMap<>(TEXT_ATTRIBUTES);
                attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRA_LIGHT);
                break;
            case LIGHT:
                attributes = new HashMap<>(TEXT_ATTRIBUTES);
                attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_LIGHT);
                break;
            case MEDIUM:
                attributes = new HashMap<>(TEXT_ATTRIBUTES);
                attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_MEDIUM);
                break;
            case BLACK:
                attributes = new HashMap<>(TEXT_ATTRIBUTES);
                attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_HEAVY);
                break;
            case CONDENSED:
                attributes = new HashMap<>(TEXT_ATTRIBUTES);
                attributes.put(TextAttribute.WEIGHT, TextAttribute.WIDTH_CONDENSED);
                break;
        }

        return new Font(name, getFontStyle(fontStyle), textSize).deriveFont(attributes);
    }

    private static int getFontStyle(FontStyle fontStyle) {
        switch (fontStyle) {
            case BOLD:
                return Font.BOLD;
            case BOLD_ITALIC:
                return Font.BOLD | Font.ITALIC;
            case ITALIC:
                return Font.ITALIC;
            case NORMAL:
                return Font.PLAIN;
        }

        throw new IllegalArgumentException("unknown fontStyle: " + fontStyle);
    }

    private static int getJoin(Join join) {
        switch (join) {
            case ROUND:
                return BasicStroke.JOIN_ROUND;
            case BEVEL:
                return BasicStroke.JOIN_BEVEL;
            case MITER:
                return BasicStroke.JOIN_MITER;
        }

        throw new IllegalArgumentException("unknown cap: " + join);
    }

    private static final Font DEFAULT_FONT;
    private static final Map<Attribute, Object> TEXT_ATTRIBUTES = new HashMap<>();

    static {
        TEXT_ATTRIBUTES.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);

        DEFAULT_FONT = new Font("Arial", Font.PLAIN, 14).deriveFont(TEXT_ATTRIBUTES);
    }

    private Align align;
    Color color = new Color(0.1f, 0.1f, 0.1f, 1);
    FontMetrics fm;
    Font font = DEFAULT_FONT; // new Font("Default", Font.PLAIN, 13);
    Stroke stroke;
    Style style = Style.FILL;
    private int cap = getCap(Cap.BUTT);
    private int join = getJoin(Join.MITER);
    private float strokeWidth;
    private float textSize = DEFAULT_FONT.getSize();

    private final BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    @Override
    public int getColor() {
        return color.getRGB();
    }

    @Override
    public void setColor(int c) {
        color = new Color(((c >> 16) & 0xff) / 255f,
                ((c >> 8) & 0xff) / 255f,
                ((c >> 0) & 0xff) / 255f,
                ((c >> 24) & 0xff) / 255f);
    }

    @Override
    public void setStrokeCap(Cap cap) {
        this.cap = getCap(cap);
        createStroke();
    }

    @Override
    public void setStrokeJoin(Join join) {
        this.join = getJoin(join);
        createStroke();
    }

    @Override
    public void setStrokeWidth(float width) {
        strokeWidth = width;
        createStroke();

        // int size = font.getSize();
        // font = font.deriveFont(size + width * 4);
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
    public void setTextSize(float textSize) {
        this.textSize = textSize;
        this.font = this.font.deriveFont(textSize);
    }

    @Override
    public void setTypeface(FontFamily fontFamily, FontStyle fontStyle) {
        this.font = getFont(fontFamily, fontStyle, (int) this.textSize);
    }

    @Override
    public float measureText(String text) {
        if (fm == null)
            fm = AwtGraphics.getFontMetrics(this.font);

        float w = AwtGraphics.getTextWidth(fm, text);
        //Gdx.app.log("text width:", text + " " + w);
        return w + 4;
        // return fm.getStringBounds(text, A).getWidth();
        // return AwtGraphics.getTextWidth(fm, text);
        // return fm.stringWidth(text);
    }

    @Override
    public float getFontHeight() {
        if (fm == null)
            fm = AwtGraphics.getFontMetrics(this.font);

        float height = fm.getHeight();

        return height;
    }

    @Override
    public float getFontDescent() {
        if (fm == null)
            fm = AwtGraphics.getFontMetrics(this.font);

        float desc = fm.getDescent();

        return desc;
    }

    private void createStroke() {
        if (strokeWidth <= 0) {
            return;
        }
        stroke = new BasicStroke(strokeWidth, cap, join, join == BasicStroke.JOIN_MITER ? 1.0f : 0, null, 0);
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
        Graphics2D graphics2d = bufferedImage.createGraphics();
        FontMetrics fontMetrics = graphics2d.getFontMetrics(this.font);
        graphics2d.dispose();
        return (float) this.font.createGlyphVector(fontMetrics.getFontRenderContext(), text).getVisualBounds().getHeight();
    }

    @Override
    public float getTextWidth(String text) {
        Graphics2D graphics2d = bufferedImage.createGraphics();
        FontMetrics fontMetrics = graphics2d.getFontMetrics(this.font);
        graphics2d.dispose();
        return fontMetrics.stringWidth(text);
    }
}
