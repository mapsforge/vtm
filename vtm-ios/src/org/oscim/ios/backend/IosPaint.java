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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class IosPaint implements Paint {
    static final Logger log = LoggerFactory.getLogger(IosPaint.class);

    public IosPaint() {
        log.info("create new Paint");
    }

    CGLineCap cap = CGLineCap.Butt;
    float strokeWidth;
    Style style;
    float textSize;
    FontFamily fontFamily;
    FontStyle fontStyle;
    //    UIColor color;
//    UIColor strokeColor;
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
        synchronized (attribs){
            attribs.setForegroundColor(getUiColor(color));
        }
    }

    public void setStrokeColor(int color) {
        if (strokeColorInt == color) return;
        this.strokeColorInt = color;
        synchronized (attribs){
            attribs.setStrokeColor(getUiColor(color));
        }
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


    CTLine fillLine;
    boolean ctLineIsDirty = true;
    String lastText = "";

    private final NSAttributedStringAttributes attribs = new NSAttributedStringAttributes();

    private void createCTLine(String text) {
        if (ctLineIsDirty) {




            synchronized (attribs) {

//                if (fillLine != null) fillLine.dispose();

                float strokeWidthPercent = -(this.strokeWidth / this.textSize * 50);
                attribs.setStrokeWidth(strokeWidthPercent);
                NSAttributedString attributedString = new NSAttributedString(text, attribs);
                fillLine = CTLine.create(attributedString);
                attributedString.dispose();
            }



            /*
            The sign of the value for NSStrokeWidthAttributeName is interpreted as a mode;
            it indicates whether the attributed string is to be filled, stroked, or both.
            Specifically, a zero value displays a fill only, while a positive value displays a stroke only.
            A negative value allows displaying both a fill and stroke.

            !!!!!
            NOTE: The value of NSStrokeWidthAttributeName is interpreted as a percentage of the font point size.
             */


            lastText = text;
            ctLineIsDirty = false;
        }
    }

    static final String DEFAULT_FONT_NAME = UIFont.getSystemFont(1).getFontDescriptor().getPostscriptName();
    static final String DEFAULT_FONT_NAME_BOLD = UIFont.getBoldSystemFont(1).getFontDescriptor().getPostscriptName();
    static final String DEFAULT_FONT_NAME_ITALIC = UIFont.getItalicSystemFont(1).getFontDescriptor().getPostscriptName();

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
                        fontname = "CourierNewPS-BoldMT";
                        break;
                    case BOLD:
                        fontname = "CourierNewPS-BoldMT";
                        break;
                    case BOLD_ITALIC:
                        fontname = "CourierNewPS-BoldMT";
                        break;
                    case ITALIC:
                        fontname = "CourierNewPS-BoldMT";
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

        synchronized (attribs) {

            String key = fontname + this.textSize;

            //try to get buffered font
            UIFont font = fontHashMap.get(key);

            if (font == null) {

                CTFont ctFont = CTFont.create(fontname, this.textSize, CGAffineTransform.Identity());

                descent = (float) ctFont.getDescent();
                fontHeight = (float) ctFont.getBoundingBox().getHeight();

                font = ctFont.as(UIFont.class);
                log.info("Put Font to buffer :" + key);
                fontHashMap.put(key, font);
            }

            CTFont ctFont = font.as(CTFont.class);
            descent = (float) ctFont.getDescent();
            fontHeight = (float) ctFont.getBoundingBox().getHeight();

            attribs.setFont(font);
        }
    }

    private float descent;
    private float fontHeight;


    private final static HashMap<String, UIFont> fontHashMap = new HashMap<>();


    public void drawLine(CGBitmapContext bctx, String text, float x, float y) {
        if (ctLineIsDirty || !text.equals(lastText)) {
            ctLineIsDirty = true;
            createCTLine(text);
        }
        bctx.saveGState();
        bctx.setShouldAntialias(true);
        bctx.setTextPosition(x, y + descent);
        bctx.setBlendMode(CGBlendMode.Overlay);


        fillLine.draw(bctx);

        bctx.restoreGState();
    }

    @Override
    public float getFontHeight() {
        return fontHeight;
    }

    @Override
    public float getFontDescent() {
        return descent;
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
