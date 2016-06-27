package org.oscim.ios.backend;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Paint;
import org.robovm.apple.coregraphics.CGBitmapContext;
import org.robovm.apple.coregraphics.CGBlendMode;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coretext.CTFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IosCanvas implements Canvas {

    static final Logger log = LoggerFactory.getLogger(IosCanvas.class);


    private CGBitmapContext cgBitmapContext;

    @Override
    public void setBitmap(Bitmap bitmap) {
        cgBitmapContext = ((IosBitmap) bitmap).cgBitmapContext;
    }

    @Override
    public void drawText(String string, float x, float y, Paint fill, Paint stroke) {

        y = this.cgBitmapContext.getHeight() - y;

        IosPaint iosFill = (IosPaint) fill;
        if (stroke != null) {
            IosPaint iosStroke = (IosPaint) stroke;
            iosFill.setStrokeWidth(iosStroke.strokeWidth);
            iosFill.setStrokeColor(iosStroke.getColor());
        }


        this.cgBitmapContext.saveGState();


        {// clear old content under Text
            float measure = fill.measureText(string);
            CGRect rect = new CGRect(x, y, measure, fill.getFontHeight());
            this.cgBitmapContext.setBlendMode(CGBlendMode.Clear);
            this.cgBitmapContext.fillRect(rect);
            this.cgBitmapContext.setBlendMode(CGBlendMode.Normal);
        }


        iosFill.drawLine(this.cgBitmapContext, string, x, y);

        this.cgBitmapContext.restoreGState();
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
