package org.oscim.ios.backend;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import org.oscim.backend.canvas.Paint;

public class IosPaint implements Paint {

    @Override
    public int getColor() {
        return 0;
    }

    @Override
    public void setColor(int color) {
    }

    @Override
    public void setStrokeCap(Cap cap) {
    }

    @Override
    public void setStrokeWidth(float width) {
    }

    @Override
    public void setStyle(Style style) {
    }

    @Override
    public void setTextAlign(Align align) {
    }

    @Override
    public void setTextSize(float textSize) {
    }

    @Override
    public void setTypeface(FontFamily fontFamily, FontStyle fontStyle) {
    }

    @Override
    public float measureText(String text) {
        GlyphLayout layout = new GlyphLayout(IosCanvas.font, text);
        return layout.width;
    }

    @Override
    public float getFontHeight() {
        return IosCanvas.font.getLineHeight();
    }

    @Override
    public float getFontDescent() {
        return IosCanvas.font.getDescent();
    }

}
