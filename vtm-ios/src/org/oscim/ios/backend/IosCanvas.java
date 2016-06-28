/*
 * Copyright 2016 Longri
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
import org.robovm.apple.coregraphics.CGRect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * iOS specific implementation of {@link Canvas}<br>
 * <br>
 * Created by Longri on 25.06.16.
 */
public class IosCanvas implements Canvas {

    static final Logger log = LoggerFactory.getLogger(IosCanvas.class);

    CGBitmapContext cgBitmapContext;

    @Override
    public void setBitmap(Bitmap bitmap) {
        cgBitmapContext = ((IosBitmap) bitmap).cgBitmapContext;
    }

    @Override
    public void drawText(String string, float x, float y, Paint fill, Paint stroke) {

        //flip Y-axis
        y = this.cgBitmapContext.getHeight() - y;

        IosPaint iosFill = (IosPaint) fill;
        if (stroke != null) {
            IosPaint iosStroke = (IosPaint) stroke;
            iosFill.setStrokeWidth(iosStroke.strokeWidth);
            iosFill.setStrokeColor(iosStroke.getColor());
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
}
