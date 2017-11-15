/*
 * Copyright 2016-2017 Longri
 * Copyright 2016-2017 devemux86
 * Copyright 2017 nebular
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
package org.oscim.ios.backend;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Paint;
import org.robovm.apple.coregraphics.CGBitmapContext;
import org.robovm.apple.coregraphics.CGBlendMode;
import org.robovm.apple.coregraphics.CGRect;

/**
 * iOS specific implementation of {@link Canvas}.
 */
public class IosCanvas implements Canvas {

    static void setFillColor(CGBitmapContext bctx, int color) {
        float blue = (color & 0xFF) / 255f;
        color >>= 8;
        float green = (color & 0xFF) / 255f;
        color >>= 8;
        float red = (color & 0xFF) / 255f;
        color >>= 8;
        float alpha = (color & 0xFF) / 255f;
        bctx.setRGBFillColor(red, green, blue, alpha);
    }

    static void setStrokeColor(CGBitmapContext bctx, int color) {
        float blue = (color & 0xFF) / 255f;
        color >>= 8;
        float green = (color & 0xFF) / 255f;
        color >>= 8;
        float red = (color & 0xFF) / 255f;
        color >>= 8;
        float alpha = (color & 0xFF) / 255f;
        bctx.setRGBStrokeColor(red, green, blue, alpha);
    }

    CGBitmapContext cgBitmapContext;

    @Override
    public void setBitmap(Bitmap bitmap) {
        cgBitmapContext = ((IosBitmap) bitmap).cgBitmapContext;
    }

    @Override
    public void drawText(String string, float x, float y, Paint paint) {

        //flip Y-axis
        y = this.cgBitmapContext.getHeight() - y;

        IosPaint iosPaint = (IosPaint) paint;
        iosPaint.drawLine(this.cgBitmapContext, string, x, y);
    }

    @Override
    public void drawText(String string, float x, float y, Paint fill, Paint stroke) {

        //flip Y-axis
        y = this.cgBitmapContext.getHeight() - y;

        IosPaint iosFill = (IosPaint) fill;
        if (stroke != null) {
            IosPaint iosStroke = (IosPaint) stroke;
            iosStroke.drawLine(this.cgBitmapContext, string, x, y);
        }
        iosFill.drawLine(this.cgBitmapContext, string, x, y);
    }

    @Override
    public void drawBitmap(Bitmap bitmap, float x, float y) {
        this.cgBitmapContext.saveGState();
        this.cgBitmapContext.translateCTM(x, y);
        this.cgBitmapContext.drawImage(new CGRect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                ((IosBitmap) bitmap).cgBitmapContext.toImage());
        this.cgBitmapContext.restoreGState();
    }

    @Override
    public void drawBitmapScaled(Bitmap bitmap) {
        CGRect rect = new CGRect(0, 0, this.cgBitmapContext.getWidth(), this.cgBitmapContext.getHeight());
        this.cgBitmapContext.saveGState();
        this.cgBitmapContext.translateCTM(0, 0);
        this.cgBitmapContext.drawImage(rect, ((IosBitmap) bitmap).cgBitmapContext.toImage());
        this.cgBitmapContext.restoreGState();
    }

    @Override
    public void drawCircle(float x, float y, float radius, Paint paint) {
        CGRect rect = new CGRect(x - radius, y - radius, x + radius, y + radius);

        switch (paint.getStyle()) {
            case FILL:
                setFillColor(this.cgBitmapContext, paint.getColor());
                this.cgBitmapContext.fillEllipseInRect(rect);
                break;
            case STROKE:
                // set Stroke properties
                this.cgBitmapContext.setLineWidth(((IosPaint) paint).strokeWidth);
                this.cgBitmapContext.setLineCap(((IosPaint) paint).getIosStrokeCap());
                this.cgBitmapContext.setLineJoin(((IosPaint) paint).getIosStrokeJoin());
                setStrokeColor(this.cgBitmapContext, (paint.getColor()));
                this.cgBitmapContext.strokeEllipseInRect(rect);
                break;
        }
    }

    @Override
    public void drawLine(float x1, float y1, float x2, float y2, Paint paint) {
        //flip Y-axis
        y1 = (int) (this.cgBitmapContext.getHeight() - y1);
        y2 = (int) (this.cgBitmapContext.getHeight() - y2);

        // set Stroke properties
        this.cgBitmapContext.setLineWidth(((IosPaint) paint).strokeWidth);
        this.cgBitmapContext.setLineCap(((IosPaint) paint).getIosStrokeCap());
        this.cgBitmapContext.setLineJoin(((IosPaint) paint).getIosStrokeJoin());
        setStrokeColor(this.cgBitmapContext, (paint.getColor()));

        //draw line
        this.cgBitmapContext.beginPath();
        this.cgBitmapContext.moveToPoint(x1, y1);
        this.cgBitmapContext.addLineToPoint(x2, y2);
        this.cgBitmapContext.strokePath();
    }

    @Override
    public void fillColor(int color) {
        CGRect rect = new CGRect(0, 0, this.cgBitmapContext.getWidth(), this.cgBitmapContext.getHeight());
        setFillColor(this.cgBitmapContext, (color));
        this.cgBitmapContext.setBlendMode(CGBlendMode.Clear);
        this.cgBitmapContext.fillRect(rect);
        this.cgBitmapContext.setBlendMode(CGBlendMode.Normal);
        this.cgBitmapContext.fillRect(rect);
    }

    @Override
    public void fillRectangle(float x, float y, float width, float height, int color) {
        CGRect rect = new CGRect(x, y, width, height);
        setFillColor(this.cgBitmapContext, (color));
        this.cgBitmapContext.setBlendMode(CGBlendMode.Normal);
        this.cgBitmapContext.fillRect(rect);
    }

    @Override
    public int getHeight() {
        return this.cgBitmapContext != null ? (int) this.cgBitmapContext.getHeight() : 0;
    }

    @Override
    public int getWidth() {
        return this.cgBitmapContext != null ? (int) this.cgBitmapContext.getWidth() : 0;
    }
}
