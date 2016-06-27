package org.oscim.ios.backend;

import org.oscim.backend.canvas.Paint;
import org.robovm.apple.coregraphics.CGAffineTransform;
import org.robovm.apple.coregraphics.CGBitmapContext;
import org.robovm.apple.coregraphics.CGBlendMode;
import org.robovm.apple.coregraphics.CGLineCap;
import org.robovm.apple.coretext.CTFont;
import org.robovm.apple.coretext.CTLine;
import org.robovm.apple.foundation.NSAttributedString;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;

public class IosPaint implements Paint {


    CGLineCap cap = CGLineCap.Butt;
    float strokeWidth;
    Style style;
    float textSize;
    CTFont font;
    FontFamily fontFamily;
    FontStyle fontStyle;
    UIColor color;
    UIColor strokeColor;
    int colorInt;
    int strokeColorInt;


    @Override
    public int getColor() {
        return this.colorInt;
    }

    @Override
    public void setColor(int color) {
        if (colorInt == color) return;
        this.colorInt = color;
        this.color = getUiColor(color);
        this.ctLineIsDirty = true;
    }

    public void setStrokeColor(int color) {
        if (strokeColorInt == color) return;
        this.strokeColorInt = color;
        this.strokeColor = getUiColor(color);
        this.ctLineIsDirty = true;
    }

    private UIColor getUiColor(int color) {
        float colorA = ((color & 0xff000000) >>> 24) / 255f;
        float colorR = ((color & 0xff0000) >>> 16) / 255f;
        float colorG = ((color & 0xff00) >>> 8) / 255f;
        float colorB = (color & 0xff) / 255f;
        return new UIColor(colorR, colorG, colorB, colorA);
    }

    @Override
    public void setStrokeCap(Cap cap) {
        this.cap = getLineCap(cap);
    }

    @Override
    public void setStrokeWidth(float width) {
        if (this.strokeWidth == width) return;
        this.strokeWidth = width;
        this.ctLineIsDirty = true;
    }

    @Override
    public void setStyle(Style style) {
        this.style = style;
    }

    @Override
    public void setTextAlign(Align align) {
// TODO: set Align
    }

    @Override
    public void setTextSize(float textSize) {
        if (this.textSize != textSize) {
            this.textSize = textSize;
            createIosFont();
            ctLineIsDirty = true;
        }
    }

    @Override
    public void setTypeface(FontFamily fontFamily, FontStyle fontStyle) {
        if (fontFamily != this.fontFamily
                || fontStyle != this.fontStyle) {

            this.fontFamily = fontFamily;
            this.fontStyle = fontStyle;
            createIosFont();
            ctLineIsDirty = true;
        }
    }


    @Override
    public float measureText(String text) {
        if (ctLineIsDirty || !text.equals(lastText)) {
            ctLineIsDirty = true;
            createCTLine(text);
        }
        return (float) fillLine.getWidth();
    }

    CTLine strokeLine;
    CTLine fillLine;
    boolean ctLineIsDirty = true;
    String lastText = "";

    private void createCTLine(String text) {
        if (ctLineIsDirty) {
            NSAttributedStringAttributes attribs = new NSAttributedStringAttributes();
            attribs.setFont(font.as(UIFont.class));
            attribs.setForegroundColor(this.color);
            attribs.setStrokeWidth(0);

            fillLine = CTLine.create(new NSAttributedString(text, attribs));


            /*
            The sign of the value for NSStrokeWidthAttributeName is interpreted as a mode;
            it indicates whether the attributed string is to be filled, stroked, or both.
            Specifically, a zero value displays a fill only, while a positive value displays a stroke only.
            A negative value allows displaying both a fill and stroke.

            !!!!!
            NOTE: The value of NSStrokeWidthAttributeName is interpreted as a percentage of the font point size.
             */
            if (strokeColor != null) {
                NSAttributedStringAttributes strokeAttribs = new NSAttributedStringAttributes();
                strokeAttribs.setFont(font.as(UIFont.class));
                strokeAttribs.setForegroundColor(this.color);
                float strokeWidthPercent = -(this.strokeWidth / this.textSize * 100);
                strokeAttribs.setStrokeWidth(strokeWidthPercent);
                strokeAttribs.setStrokeColor(this.strokeColor);
                strokeLine = CTLine.create(new NSAttributedString(text, strokeAttribs));
            } else {
                strokeLine = null;
            }
            lastText = text;
            ctLineIsDirty = false;
        }
    }

    static final String DEFAULT_FONT_NAME = UIFont.getSystemFont(1).getFamilyName();
    static final String DEFAULT_FONT_NAME_BOLD = UIFont.getBoldSystemFont(1).getFamilyName();
    static final String DEFAULT_FONT_NAME_ITALIC = UIFont.getItalicSystemFont(1).getFamilyName();

    void createIosFont() {

        /**
         * DEVICE_DEFAULT = [iOS == getDeviceDefault()], [Android == 'Roboto']
         * MONOSPACE      = [iOS == 'Courier'], [Android == 'Droid Sans Mono']
         * SANS_SERIF     = [iOS == 'Verdena'], [Android == 'Droid Sans']
         * SERIF          = [iOS == 'Georgia'], [Android == 'Droid Serif']
         */

        String fontname = DEFAULT_FONT_NAME;
        switch (this.fontFamily) {
            case DEFAULT:
                // set Style
                switch (this.fontStyle) {
                    case NORMAL:
                        fontname = DEFAULT_FONT_NAME;
                        break;
                    case BOLD:
                        fontname = DEFAULT_FONT_NAME_BOLD;
                        break;
                    case BOLD_ITALIC:
                        fontname = DEFAULT_FONT_NAME_BOLD;
                        break;
                    case ITALIC:
                        fontname = DEFAULT_FONT_NAME_ITALIC;
                        break;
                }
                break;
            case DEFAULT_BOLD:
                // ignore style
                fontname = DEFAULT_FONT_NAME_BOLD;
                break;
            case MONOSPACE:
                // set Style
                switch (this.fontStyle) {
                    case NORMAL:
                        fontname = "CourierNewPSMT";
                        break;
                    case BOLD:
                        fontname = "CourierNewPS-BoldMT";
                        break;
                    case BOLD_ITALIC:
                        fontname = "CourierNewPS-BoldItalicMT";
                        break;
                    case ITALIC:
                        fontname = "CourierNewPS-ItalicMT";
                        break;
                }
                break;
            case SANS_SERIF:
                // set Style
                switch (this.fontStyle) {
                    case NORMAL:
                        fontname = "Verdana";
                        break;
                    case BOLD:
                        fontname = "Verdana-Bold";
                        break;
                    case BOLD_ITALIC:
                        fontname = "Verdana-BoldItalic";
                        break;
                    case ITALIC:
                        fontname = "Verdana-Italic";
                        break;
                }
                break;
            case SERIF:
                // set Style
                switch (this.fontStyle) {
                    case NORMAL:
                        fontname = "Georgia";
                        break;
                    case BOLD:
                        fontname = "Georgia-Bold";
                        break;
                    case BOLD_ITALIC:
                        fontname = "Georgia-BoldItalic";
                        break;
                    case ITALIC:
                        fontname = "Georgia-Italic";
                        break;
                }
                break;
        }
        this.font = CTFont.create(fontname, this.textSize, CGAffineTransform.Identity());
    }

    public void drawLine(CGBitmapContext bctx, String text, float x, float y) {
        if (ctLineIsDirty || !text.equals(lastText)) {
            ctLineIsDirty = true;
            createCTLine(text);
        }
        bctx.saveGState();
        bctx.setShouldAntialias(true);
        bctx.setTextPosition(x, y + this.font.getDescent());
        bctx.setBlendMode(CGBlendMode.Overlay);

        if (strokeLine != null) {
            strokeLine.draw(bctx);
        } else {
            fillLine.draw(bctx);
        }
        bctx.restoreGState();
    }

    @Override
    public float getFontHeight() {
        return (float) this.font.getBoundingBox().getHeight() + 4;
    }

    @Override
    public float getFontDescent() {
        return (float) this.font.getDescent();
    }


    private CGLineCap getLineCap(Cap cap) {
        switch (cap) {
            case BUTT:
                return CGLineCap.Butt;
            case ROUND:
                return CGLineCap.Round;
            case SQUARE:
                return CGLineCap.Square;
        }
        return CGLineCap.Butt;
    }


}
