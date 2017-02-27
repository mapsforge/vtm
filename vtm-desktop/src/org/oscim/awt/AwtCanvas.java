/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
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
package org.oscim.awt;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class AwtCanvas implements Canvas {

    private static final java.awt.Color TRANSPARENT = new java.awt.Color(0, 0, 0, 0);

    private BufferedImage bitmap;
    public Graphics2D canvas;

    public AwtCanvas() {
    }

    public AwtCanvas(BufferedImage bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        if (canvas != null)
            canvas.dispose();

        AwtBitmap awtBitmap = (AwtBitmap) bitmap;

        this.bitmap = awtBitmap.bitmap;
        canvas = awtBitmap.bitmap.createGraphics();

        canvas.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0));
        canvas.fillRect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        canvas.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        canvas.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        canvas.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        canvas.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        canvas.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    private final AffineTransform tx = new AffineTransform();

    @Override
    public void drawText(String text, float x, float y, Paint paint) {

        AwtPaint awtPaint = (AwtPaint) paint;

        if (awtPaint.stroke == null) {
            canvas.setColor(awtPaint.color);
            canvas.setFont(awtPaint.font);
            canvas.drawString(text, x, y);
        } else {
            canvas.setColor(awtPaint.color);
            canvas.setStroke(awtPaint.stroke);

            TextLayout tl = new TextLayout(text, awtPaint.font,
                    canvas.getFontRenderContext());
            tx.setToIdentity();
            tx.translate(x, y);

            Shape s = tl.getOutline(tx);

            canvas.draw(s);
            canvas.setColor(awtPaint.color);
            canvas.fill(s);
        }
    }

    @Override
    public void drawText(String text, float x, float y, Paint fill, Paint stroke) {

        AwtPaint fillPaint = (AwtPaint) fill;

        if (stroke == null) {
            canvas.setColor(fillPaint.color);
            canvas.setFont(fillPaint.font);
            canvas.drawString(text, x, y);
        } else {
            AwtPaint strokePaint = (AwtPaint) stroke;

            canvas.setColor(strokePaint.color);
            canvas.setStroke(strokePaint.stroke);

            TextLayout tl = new TextLayout(text, fillPaint.font,
                    canvas.getFontRenderContext());
            tx.setToIdentity();
            tx.translate(x, y);

            Shape s = tl.getOutline(tx);

            canvas.draw(s);
            canvas.setColor(fillPaint.color);
            canvas.fill(s);
        }
    }

    @Override
    public void drawBitmap(Bitmap bitmap, float x, float y) {
        this.canvas.drawImage(((AwtBitmap) bitmap).bitmap, (int) x, (int) y, null);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2, Paint paint) {
        AwtPaint awtPaint = (AwtPaint) paint;
        this.canvas.setColor(awtPaint.color);
        if (awtPaint.stroke != null)
            this.canvas.setStroke(awtPaint.stroke);
        this.canvas.drawLine(x1, y1, x2, y2);
    }



    @Override
    public void drawCircle(float x, float y, float radius, Paint paint) {

        // TODO THIS IS UNTESTED !!!

        AwtPaint awtPaint = (AwtPaint) paint;
        this.canvas.setColor(awtPaint.color);

        switch (awtPaint.style) {
            case STROKE:
                if (awtPaint.stroke != null) {
                    this.canvas.setStroke(awtPaint.stroke);
                    this.canvas.drawOval((int)x, (int)y, (int)radius, (int)radius);
                }
                break;
            case FILL:
            default: // FILL_AND_STROKE is not defined here but exists on Android
                this.canvas.fillOval((int)x, (int)y, (int)radius, (int)radius);
        }

    }

    @Override
    public void fillColor(int color) {
        java.awt.Color awtColor = color == Color.TRANSPARENT ? TRANSPARENT : new java.awt.Color(color);
        Composite originalComposite = this.canvas.getComposite();
        this.canvas.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        this.canvas.setColor(awtColor);
        this.canvas.fillRect(0, 0, getWidth(), getHeight());
        this.canvas.setComposite(originalComposite);
    }

    @Override
    public int getHeight() {
        return this.bitmap != null ? this.bitmap.getHeight() : 0;
    }

    @Override
    public int getWidth() {
        return this.bitmap != null ? this.bitmap.getWidth() : 0;
    }
}
