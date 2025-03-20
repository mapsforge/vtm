/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 * Copyright 2013 Hannes Janetzek
 * Copyright 2014 Ludwig M Brinckmann
 * Copyright 2016-2021 devemux86
 * Copyright 2016-2017 Longri
 * Copyright 2016-2020 Andrey Novikov
 * Copyright 2018-2019 Gustl22
 * Copyright 2018 Izumi Kawashima
 * Copyright 2019 Murray Hughes
 * Copyright 2021 eddiemuc
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
package org.oscim.theme;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint.Cap;
import org.oscim.backend.canvas.Paint.FontFamily;
import org.oscim.backend.canvas.Paint.FontStyle;
import org.oscim.core.Tag;
import org.oscim.renderer.atlas.TextureAtlas;
import org.oscim.renderer.atlas.TextureAtlas.Rect;
import org.oscim.renderer.atlas.TextureRegion;
import org.oscim.renderer.bucket.TextureItem;
import org.oscim.theme.IRenderTheme.ThemeException;
import org.oscim.theme.rule.Rule;
import org.oscim.theme.rule.Rule.Closed;
import org.oscim.theme.rule.Rule.Selector;
import org.oscim.theme.rule.RuleBuilder;
import org.oscim.theme.styles.*;
import org.oscim.theme.styles.AreaStyle.AreaBuilder;
import org.oscim.theme.styles.CircleStyle.CircleBuilder;
import org.oscim.theme.styles.ExtrusionStyle.ExtrusionBuilder;
import org.oscim.theme.styles.LineStyle.LineBuilder;
import org.oscim.theme.styles.SymbolStyle.SymbolBuilder;
import org.oscim.theme.styles.TextStyle.TextBuilder;
import org.oscim.utils.FastMath;
import org.oscim.utils.IOUtils;
import org.oscim.utils.Parameters;
import org.oscim.utils.Utils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

public class XmlThemeBuilder {

    private static final Logger log = Logger.getLogger(XmlThemeBuilder.class.getName());

    private static final int RENDER_THEME_VERSION_MAPSFORGE = 6;
    private static final int RENDER_THEME_VERSION_VTM = 1;
    private static XmlPullParserFactory xmlPullParserFactory = null;

    private enum Element {
        RENDER_THEME, RENDERING_INSTRUCTION, RULE, STYLE, ATLAS, RECT, RENDERING_STYLE, TAG_TRANSFORM
    }

    private static final String ELEMENT_NAME_RENDER_THEME = "rendertheme";
    private static final String ELEMENT_NAME_STYLE_MENU = "stylemenu";
    private static final String ELEMENT_NAME_MATCH_MAPSFORGE = "rule";
    private static final String ELEMENT_NAME_MATCH_VTM = "m";
    private static final String UNEXPECTED_ELEMENT_STACK_NOT_EMPTY = "Stack not empty, unexpected element: ";
    private static final String UNEXPECTED_ELEMENT_RENDERING_INSTRUCTION_PARENT_ELEMENT_MISMATCH = "Rendering instruction:: Parent element mismatch: unexpected element: ";
    private static final String UNEXPECTED_ELEMENT_RULE_PARENT_ELEMENT_MISMATCH = "Rule:: Parent element mismatch: unexpected element: ";
    private static final String UNEXPECTED_ELEMENT_STYLE_PARENT_ELEMENT_MISMATCH = "Style:: Parent element mismatch: unexpected element: ";
    private static final String UNEXPECTED_ELEMENT_ATLAS_PARENT_ELEMENT_MISMATCH = "Atlas:: Parent element mismatch: unexpected element: ";
    private static final String UNEXPECTED_ELEMENT_RECT_PARENT_ELEMENT_MISMATCH = "Rect:: Parent element mismatch: unexpected element: ";
    private static final String UNEXPECTED_ELEMENT_TAG_TRANSFORM_PARENT_ELEMENT_MISMATCH = "Tag transform:: Parent element mismatch: unexpected element: ";

    private static final String LINE_STYLE = "L";
    private static final String OUTLINE_STYLE = "O";
    private static final String AREA_STYLE = "A";

    private static final int DEFAULT_PRIORITY = Integer.MAX_VALUE / 2;

    /**
     * @param theme an input theme containing valid render theme XML data.
     * @return a new RenderTheme which is created by parsing the XML data from the input theme.
     * @throws ThemeException if an error occurs while parsing the render theme XML.
     */
    public static IRenderTheme read(ThemeFile theme) throws ThemeException {
        return read(theme, null);
    }

    /**
     * @param theme         an input theme containing valid render theme XML data.
     * @param themeCallback the theme callback.
     * @return a new RenderTheme which is created by parsing the XML data from the input theme.
     * @throws ThemeException if an error occurs while parsing the render theme XML.
     */
    public static IRenderTheme read(ThemeFile theme, ThemeCallback themeCallback) throws ThemeException {
        InputStream inputStream = null;
        try {
            XmlPullParser pullParser = getXmlPullParserFactory().newPullParser();
            XmlThemeBuilder renderThemeHandler = new XmlThemeBuilder(theme, pullParser, themeCallback);
            inputStream = theme.getRenderThemeAsStream();
            pullParser.setInput(inputStream, null);
            renderThemeHandler.processRenderTheme();
            return renderThemeHandler.mRenderTheme;
        } catch (Exception e) {
            throw new ThemeException(e.toString(), e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static XmlPullParserFactory getXmlPullParserFactory() throws XmlPullParserException {
        if (xmlPullParserFactory == null) {
            xmlPullParserFactory = XmlPullParserFactory.newInstance();
        }
        return xmlPullParserFactory;
    }

    public static void setXmlPullParserFactory(XmlPullParserFactory xmlPullParserFactory) {
        XmlThemeBuilder.xmlPullParserFactory = xmlPullParserFactory;
    }

    /**
     * Logs the given information about an unknown XML attribute.
     *
     * @param element        the XML element name.
     * @param name           the XML attribute name.
     * @param value          the XML attribute value.
     * @param attributeIndex the XML attribute index position.
     */
    private static void logUnknownAttribute(String element, String name, String value, int attributeIndex) {
        log.warning("unknown attribute in element " + element + " " + attributeIndex + " : " + name + " = " + value);
    }

    private final ArrayList<RuleBuilder> mRulesList = new ArrayList<>();
    private final Stack<Element> mElementStack = new Stack<>();
    private final Stack<RuleBuilder> mRuleStack = new Stack<>();
    private final Map<String, RenderStyle<?>> mStyles = new HashMap<>(10);

    private final Map<String, TextStyle.TextBuilder<?>> mTextStyles = new HashMap<>(10);
    private final Map<String, SymbolStyle.SymbolBuilder<?>> mSymbolStyles = new HashMap<>(10);

    private final AreaBuilder<?> mAreaBuilder = AreaStyle.builder();
    private final CircleBuilder<?> mCircleBuilder = CircleStyle.builder();
    private final ExtrusionBuilder<?> mExtrusionBuilder = ExtrusionStyle.builder();
    private final LineBuilder<?> mLineBuilder = LineStyle.builder();
    private final SymbolBuilder<?> mSymbolBuilder = SymbolStyle.builder();
    private final TextBuilder<?> mTextBuilder = TextStyle.builder();

    private RuleBuilder mCurrentRule;
    private TextureAtlas mTextureAtlas;

    int mLevels = 0;
    int mMapBackground = 0xffffffff;
    private float mStrokeScale = 1;
    float mTextScale = 1;

    private final XmlPullParser mPullParser;
    private String qName;
    final ThemeFile mTheme;
    private final ThemeCallback mThemeCallback;
    RenderTheme mRenderTheme;

    private final float mScale;

    private Set<String> mCategories;
    private XmlRenderThemeStyleLayer mCurrentLayer;
    private XmlRenderThemeStyleMenu mRenderThemeStyleMenu;

    private final Map<String, String> mTransformKeyMap = new HashMap<>();
    private final Map<Tag, Tag> mTransformTagMap = new HashMap<>();

    public XmlThemeBuilder(ThemeFile theme, XmlPullParser pullParser) {
        this(theme, pullParser, null);
    }

    public XmlThemeBuilder(ThemeFile theme, XmlPullParser pullParser, ThemeCallback themeCallback) {
        mTheme = theme;
        mPullParser = pullParser;
        mThemeCallback = themeCallback;
        mScale = CanvasAdapter.getScale();
    }

    public void processRenderTheme() throws XmlPullParserException, IOException {
        int eventType = mPullParser.getEventType();
        do {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                // no-op
            } else if (eventType == XmlPullParser.START_TAG) {
                startElement();
            } else if (eventType == XmlPullParser.END_TAG) {
                endElement();
            } else if (eventType == XmlPullParser.TEXT) {
                // not implemented
            }
            eventType = mPullParser.next();
        } while (eventType != XmlPullParser.END_DOCUMENT);
        endDocument();
    }

    public void endDocument() {
        if (mTheme.isMapsforgeTheme()) {
            // Building rule for Mapsforge themes
            mRulesList.add(buildingRule());
        }

        Rule[] rules = new Rule[mRulesList.size()];
        for (int i = 0, n = rules.length; i < n; i++)
            rules[i] = mRulesList.get(i).onComplete(mTheme.isMapsforgeTheme() ? new int[1] : null);

        mRenderTheme = createTheme(rules);

        mRulesList.clear();
        mStyles.clear();
        mTextStyles.clear();
        mSymbolStyles.clear();
        mRuleStack.clear();
        mElementStack.clear();

        mTextureAtlas = null;
    }

    RenderTheme createTheme(Rule[] rules) {
        return new RenderTheme(mMapBackground, mTextScale, rules, mLevels, mTransformKeyMap, mTransformTagMap, mTheme.isMapsforgeTheme());
    }

    public void endElement() {
        qName = mPullParser.getName();

        mElementStack.pop();

        if (ELEMENT_NAME_MATCH_MAPSFORGE.equals(qName) || ELEMENT_NAME_MATCH_VTM.equals(qName)) {
            mRuleStack.pop();
            if (mRuleStack.empty()) {
                if (isVisible(mCurrentRule)) {
                    mRulesList.add(mCurrentRule);
                }
            } else {
                mCurrentRule = mRuleStack.peek();
            }
        } else if (ELEMENT_NAME_STYLE_MENU.equals(qName)) {
            // when we are finished parsing the menu part of the file, we can get the
            // categories to render from the initiator. This allows the creating action
            // to select which of the menu options to choose
            if (null != mTheme.getMenuCallback()) {
                // if there is no callback, there is no menu, so the categories will be null
                mCategories = mTheme.getMenuCallback().getCategories(mRenderThemeStyleMenu);
            }
        }
    }

    public void startElement() throws ThemeException {
        qName = mPullParser.getName();

        try {
            if (ELEMENT_NAME_RENDER_THEME.equals(qName)) {
                checkState(qName, Element.RENDER_THEME);
                createRenderTheme(qName);

            } else if (ELEMENT_NAME_MATCH_MAPSFORGE.equals(qName) || ELEMENT_NAME_MATCH_VTM.equals(qName)) {
                checkState(qName, Element.RULE);
                RuleBuilder rule = createRule(qName);
                if (!mRuleStack.empty() && isVisible(rule)) {
                    mCurrentRule.addSubRule(rule);
                }
                mCurrentRule = rule;
                mRuleStack.push(mCurrentRule);

            } else if ("style-text".equals(qName)) {
                checkState(qName, Element.STYLE);
                handleTextElement(qName, true, false);

            } else if ("style-symbol".equals(qName)) {
                checkState(qName, Element.STYLE);
                handleSymbolElement(qName, true);

            } else if ("style-area".equals(qName)) {
                checkState(qName, Element.STYLE);
                handleAreaElement(qName, true);

            } else if ("style-line".equals(qName)) {
                checkState(qName, Element.STYLE);
                handleLineElement(qName, true, false);

            } else if ("outline-layer".equals(qName)) {
                checkState(qName, Element.RENDERING_INSTRUCTION);
                LineStyle line = createLine(null, qName, mLevels++, true, false);
                mStyles.put(OUTLINE_STYLE + line.style, line);

            } else if ("area".equals(qName)) {
                checkState(qName, Element.RENDERING_INSTRUCTION);
                handleAreaElement(qName, false);

            } else if ("caption".equals(qName)) {
                checkState(qName, Element.RENDERING_INSTRUCTION);
                handleTextElement(qName, false, true);

            } else if ("circle".equals(qName)) {
                checkState(qName, Element.RENDERING_INSTRUCTION);
                CircleStyle circle = createCircle(qName, mLevels++);
                if (isVisible(circle))
                    mCurrentRule.addStyle(circle);

            } else if ("line".equals(qName)) {
                checkState(qName, Element.RENDERING_INSTRUCTION);
                handleLineElement(qName, false, false);

            } else if ("text".equals(qName) || "pathText".equals(qName)) {
                checkState(qName, Element.RENDERING_INSTRUCTION);
                handleTextElement(qName, false, false);

            } else if ("symbol".equals(qName)) {
                checkState(qName, Element.RENDERING_INSTRUCTION);
                handleSymbolElement(qName, false);

            } else if ("outline".equals(qName)) {
                checkState(qName, Element.RENDERING_INSTRUCTION);
                LineStyle outline = createOutline(getStringAttribute("use"));
                if (outline != null && isVisible(outline))
                    mCurrentRule.addStyle(outline);

            } else if ("extrusion".equals(qName)) {
                checkState(qName, Element.RENDERING_INSTRUCTION);
                ExtrusionStyle extrusion = createExtrusion(qName, mLevels++);
                if (isVisible(extrusion))
                    mCurrentRule.addStyle(extrusion);

            } else if ("lineSymbol".equals(qName)) {
                checkState(qName, Element.RENDERING_INSTRUCTION);
                handleLineElement(qName, false, true);

            } else if ("atlas".equals(qName)) {
                checkState(qName, Element.ATLAS);
                createAtlas(qName);

            } else if ("rect".equals(qName)) {
                checkState(qName, Element.RECT);
                createTextureRegion(qName);

            } else if ("cat".equals(qName)) {
                checkState(qName, Element.RENDERING_STYLE);
                mCurrentLayer.addCategory(getStringAttribute("id"));

            } else if ("layer".equals(qName)) {
                // render theme menu layer
                checkState(qName, Element.RENDERING_STYLE);
                boolean enabled = false;
                if (getStringAttribute("enabled") != null) {
                    enabled = Boolean.parseBoolean(getStringAttribute("enabled"));
                }
                boolean visible = Boolean.parseBoolean(getStringAttribute("visible"));
                mCurrentLayer = mRenderThemeStyleMenu.createLayer(getStringAttribute("id"), visible, enabled);
                String parent = getStringAttribute("parent");
                if (null != parent) {
                    XmlRenderThemeStyleLayer parentEntry = mRenderThemeStyleMenu.getLayer(parent);
                    if (null != parentEntry) {
                        for (String cat : parentEntry.getCategories()) {
                            mCurrentLayer.addCategory(cat);
                        }
                        for (XmlRenderThemeStyleLayer overlay : parentEntry.getOverlays()) {
                            mCurrentLayer.addOverlay(overlay);
                        }
                    }
                }

            } else if ("name".equals(qName)) {
                // render theme menu name
                checkState(qName, Element.RENDERING_STYLE);
                mCurrentLayer.addTranslation(getStringAttribute("lang"), getStringAttribute("value"));

            } else if ("overlay".equals(qName)) {
                // render theme menu overlay
                checkState(qName, Element.RENDERING_STYLE);
                XmlRenderThemeStyleLayer overlay = mRenderThemeStyleMenu.getLayer(getStringAttribute("id"));
                if (overlay != null) {
                    mCurrentLayer.addOverlay(overlay);
                }

            } else if ("stylemenu".equals(qName)) {
                checkState(qName, Element.RENDERING_STYLE);
                mRenderThemeStyleMenu = new XmlRenderThemeStyleMenu(getStringAttribute("id"),
                        getStringAttribute("defaultlang"), getStringAttribute("defaultvalue"));

            } else if ("tag-transform".equals(qName)) {
                checkState(qName, Element.TAG_TRANSFORM);
                tagTransform(qName);

            } else if ("hillshading".equals(qName)) {
                checkState(qName, Element.RULE);
                // no-op

            } else {
                log.severe("unknown element: " + qName);
                throw new XmlPullParserException("unknown element: " + qName);
            }
        } catch (XmlPullParserException | IOException e) {
            throw new ThemeException(e.toString(), e);
        }
    }

    private RuleBuilder createRule(String qName) {
        String cat = null;
        int element = Rule.Element.ANY;
        int closed = Closed.ANY;
        String keys = null;
        String values = null;
        byte zoomMin = 0;
        byte zoomMax = Byte.MAX_VALUE;
        int selector = 0;

        for (int i = 0, n = mPullParser.getAttributeCount(); i < n; ++i) {
            String name = mPullParser.getAttributeName(i);
            String value = mPullParser.getAttributeValue(i);

            if ("e".equals(name)) {
                String val = value.toUpperCase(Locale.ENGLISH);
                if ("WAY".equals(val))
                    element = Rule.Element.WAY;
                else if ("NODE".equals(val))
                    element = Rule.Element.NODE;
            } else if ("k".equals(name)) {
                if (mTheme.isMapsforgeTheme()) {
                    if (!"*".equals(value))
                        keys = value;
                } else
                    keys = value;
            } else if ("v".equals(name)) {
                if (mTheme.isMapsforgeTheme()) {
                    if (!"*".equals(value))
                        values = value;
                } else
                    values = value;
            } else if ("cat".equals(name)) {
                cat = value;
            } else if ("closed".equals(name)) {
                String val = value.toUpperCase(Locale.ENGLISH);
                if ("YES".equals(val))
                    closed = Closed.YES;
                else if ("NO".equals(val))
                    closed = Closed.NO;
            } else if ("zoom-min".equals(name)) {
                zoomMin = Byte.parseByte(value);
            } else if ("zoom-max".equals(name)) {
                zoomMax = Byte.parseByte(value);
            } else if ("select".equals(name)) {
                if ("first".equals(value))
                    selector |= Selector.FIRST;
                if ("when-matched".equals(value))
                    selector |= Selector.WHEN_MATCHED;
            } else {
                logUnknownAttribute(qName, name, value, i);
            }
        }

        if (closed == Closed.YES)
            element = Rule.Element.POLY;
        else if (closed == Closed.NO)
            element = Rule.Element.LINE;

        validateNonNegative("zoom-min", zoomMin);
        validateNonNegative("zoom-max", zoomMax);
        if (zoomMin > zoomMax)
            throw new ThemeException("zoom-min must be less or equal zoom-max: " + zoomMin);

        RuleBuilder b = RuleBuilder.create(keys, values);
        b.cat(cat);
        b.zoom(zoomMin, zoomMax);
        b.element(element);
        b.select(selector);
        return b;
    }

    private TextureRegion getAtlasRegion(String src) {
        if (mTextureAtlas == null)
            return null;

        TextureRegion texture = mTextureAtlas.getTextureRegion(src);

        if (texture == null)
            log.fine("missing texture atlas item '" + src + "'");

        return texture;
    }

    private void handleLineElement(String qName, boolean isStyle, boolean hasSymbol) {

        String use = getStringAttribute("use");
        LineStyle style = null;

        if (use != null) {
            style = (LineStyle) mStyles.get(LINE_STYLE + use);
            if (style == null) {
                log.fine("missing line style 'use': " + use);
                return;
            }
        }

        LineStyle line = createLine(style, qName, mLevels++, false, hasSymbol);

        if (isStyle) {
            mStyles.put(LINE_STYLE + line.style, line);
        } else {
            if (isVisible(line)) {
                mCurrentRule.addStyle(line);
                /* Note 'outline' will not be inherited, it's just a
                 * shortcut to add the outline RenderInstruction. */
                String outlineValue = getStringAttribute("outline");
                if (outlineValue != null) {
                    LineStyle outline = createOutline(outlineValue);
                    if (outline != null)
                        mCurrentRule.addStyle(outline);
                }
            }
        }
    }

    /**
     * @param line      optional: line style defaults
     * @param level     the drawing level of this instruction.
     * @param isOutline is outline layer
     * @return a new Line with the given rendering attributes.
     */
    private LineStyle createLine(LineStyle line, String elementName, int level, boolean isOutline, boolean hasSymbol) {
        LineBuilder<?> b = mLineBuilder.set(line);
        b.isOutline(isOutline);
        b.level(level);
        b.themeCallback(mThemeCallback);
        String src = null;

        for (int i = 0, n = mPullParser.getAttributeCount(); i < n; ++i) {
            String name = mPullParser.getAttributeName(i);
            String value = mPullParser.getAttributeValue(i);

            if ("id".equals(name))
                b.style = value;

            else if ("cat".equals(name))
                b.cat(value);

            else if ("src".equals(name))
                src = value;

            else if ("use".equals(name))
                ;// ignore

            else if ("outline".equals(name))
                ;// ignore

            else if ("stroke".equals(name))
                b.color(value);

            else if ("width".equals(name) || "stroke-width".equals(name)) {
                b.strokeWidth = Float.parseFloat(value) * mScale * mStrokeScale * CanvasAdapter.lineScale;
                if (line == null) {
                    if (!isOutline)
                        validateNonNegative("width", b.strokeWidth);
                } else {
                    /* use stroke width relative to 'line' */
                    b.strokeWidth += line.width;
                    if (b.strokeWidth <= 0)
                        b.strokeWidth = 1;
                }
            } else if ("cap".equals(name) || "stroke-linecap".equals(name))
                b.cap = Cap.valueOf(value.toUpperCase(Locale.ENGLISH));

            else if ("fix".equals(name))
                b.fixed = Boolean.parseBoolean(value);

            else if ("stipple".equals(name))
                b.stipple = (int) (Integer.parseInt(value) * mScale * mStrokeScale * CanvasAdapter.lineScale);

            else if ("stipple-stroke".equals(name))
                b.stippleColor(value);

            else if ("stipple-width".equals(name))
                b.stippleWidth = Float.parseFloat(value);

            else if ("fade".equals(name))
                b.fadeScale = Integer.parseInt(value);

            else if ("min".equals(name))
                ; //min = Float.parseFloat(value);

            else if ("blur".equals(name))
                b.blur = Float.parseFloat(value);

            else if ("style".equals(name))
                ; // ignore

            else if ("dasharray".equals(name) || "stroke-dasharray".equals(name)) {
                b.dashArray = parseFloatArray(value);
                for (int j = 0; j < b.dashArray.length; ++j) {
                    b.dashArray[j] = b.dashArray[j] * mScale * mStrokeScale * CanvasAdapter.lineScale;
                }

            } else if ("symbol-width".equals(name))
                b.symbolWidth = (int) (Integer.parseInt(value) * mScale);

            else if ("symbol-height".equals(name))
                b.symbolHeight = (int) (Integer.parseInt(value) * mScale);

            else if ("symbol-percent".equals(name))
                b.symbolPercent = Integer.parseInt(value);

            else if ("symbol-scaling".equals(name))
                ; // no-op

            else if ("repeat-start".equals(name))
                b.repeatStart = Float.parseFloat(value) * mScale;

            else if ("repeat-gap".equals(name))
                b.repeatGap = Float.parseFloat(value) * mScale;

            else if ("align-center".equals(name))
                ; // no-op

            else if ("curve".equals(name))
                ; // no-op

            else if ("display".equals(name))
                ; // no-op

            else if ("dy".equals(name))
                ; // no-op

            else if ("position".equals(name))
                ; // no-op

            else if ("priority".equals(name))
                ; // no-op

            else if ("repeat".equals(name))
                ; // no-op

            else if ("rotate".equals(name))
                ; // no-op

            else if ("scale".equals(name))
                ; // no-op

            else if ("stroke-linejoin".equals(name))
                ; // no-op

            else
                logUnknownAttribute(elementName, name, value, i);
        }

        if (b.dashArray != null) {
            // Stroke dash array
            if (b.dashArray.length == 1) {
                b.randomOffset = false;
                b.stipple = b.dashArray[0] < 1 ? 1 : (int) b.dashArray[0];
                b.stippleWidth = 1;
                b.stippleColor = Color.TRANSPARENT;
                b.dashArray = null;
            } else {
                // Min dash is 1
                float factor = 1;
                for (float f : b.dashArray) {
                    if (0 < f && f < 1)
                        factor = Math.max(factor, 1 / f);
                }
                // Odd number of entries is duplicated
                if (b.dashArray.length % 2 != 0) {
                    float[] newDashArray = new float[b.dashArray.length * 2];
                    System.arraycopy(b.dashArray, 0, newDashArray, 0, b.dashArray.length);
                    System.arraycopy(b.dashArray, 0, newDashArray, b.dashArray.length, b.dashArray.length);
                    b.dashArray = newDashArray;
                }
                int width = 0;
                int height = b.strokeWidth < 1 ? 1 : (int) b.strokeWidth;
                for (float f : b.dashArray) {
                    f *= factor;
                    width += f;
                }
                Bitmap bitmap = CanvasAdapter.newBitmap(width, height, 0);
                Canvas canvas = CanvasAdapter.newCanvas();
                canvas.setBitmap(bitmap);
                int x = 0;
                boolean transparent = false;
                for (float f : b.dashArray) {
                    f *= factor;
                    canvas.fillRectangle(x, 0, f, height, transparent ? Color.TRANSPARENT : Color.WHITE);
                    x += f;
                    transparent = !transparent;
                }
                b.texture = new TextureItem(Utils.potBitmap(bitmap));
                b.texture.mipmap = true;
                b.randomOffset = false;
                b.stipple = width;
                b.stippleWidth = 1;
                b.stippleColor = b.fillColor;
            }
        } else {
            // Line symbol or pattern
            if (src != null) {
                b.symbolPercent *= 2;
                float symbolScale = hasSymbol && Parameters.SYMBOL_SCALING == Parameters.SymbolScaling.ALL ? CanvasAdapter.symbolScale : 1;
                b.texture = Utils.loadTexture(mTheme.getRelativePathPrefix(), src, mTheme.getResourceProvider(), b.symbolWidth, b.symbolHeight, (int) (b.symbolPercent * symbolScale), mThemeCallback);
            }
            if (b.texture != null) {
                int width = (int) (b.texture.width + (hasSymbol ? b.repeatGap : 0));
                int height = b.texture.height;
                Bitmap bitmap = CanvasAdapter.newBitmap(width, height, 0);
                Canvas canvas = CanvasAdapter.newCanvas();
                canvas.setBitmap(bitmap);
                canvas.drawBitmap(b.texture.bitmap, (hasSymbol ? b.repeatStart : 0), 0);
                b.texture = new TextureItem(Utils.potBitmap(bitmap));
                b.texture.mipmap = true;
                if (hasSymbol) {
                    b.fixed = true;
                    b.strokeWidth = height * 0.25f;
                }
                b.randomOffset = false;
                b.stipple = width;
                b.stippleWidth = 1;
                b.stippleColor = Color.WHITE;
            }
        }

        return b.build();
    }

    private void handleAreaElement(String qName, boolean isStyle) {

        String use = getStringAttribute("use");
        AreaStyle style = null;

        if (use != null) {
            style = (AreaStyle) mStyles.get(AREA_STYLE + use);
            if (style == null) {
                log.fine("missing area style 'use': " + use);
                return;
            }
        }

        AreaStyle area = createArea(style, qName, mLevels++);

        if (isStyle) {
            mStyles.put(AREA_STYLE + area.style, area);
        } else {
            if (isVisible(area))
                mCurrentRule.addStyle(area);
        }
    }

    /**
     * @return a new Area with the given rendering attributes.
     */
    private AreaStyle createArea(AreaStyle area, String elementName, int level) {
        AreaBuilder<?> b = mAreaBuilder.set(area);
        b.level(level);
        b.themeCallback(mThemeCallback);
        String src = null;

        for (int i = 0, n = mPullParser.getAttributeCount(); i < n; ++i) {
            String name = mPullParser.getAttributeName(i);
            String value = mPullParser.getAttributeValue(i);

            if ("id".equals(name))
                b.style = value;

            else if ("cat".equals(name))
                b.cat(value);

            else if ("use".equals(name))
                ;// ignore

            else if ("src".equals(name))
                src = value;

            else if ("fill".equals(name))
                b.color(value);

            else if ("stroke".equals(name))
                b.strokeColor(value);

            else if ("stroke-width".equals(name)) {
                float strokeWidth = Float.parseFloat(value);
                validateNonNegative("stroke-width", strokeWidth);
                b.strokeWidth = strokeWidth * mScale * mStrokeScale * CanvasAdapter.lineScale;

            } else if ("fade".equals(name))
                b.fadeScale = Integer.parseInt(value);

            else if ("blend".equals(name))
                b.blendScale = Integer.parseInt(value);

            else if ("blend-fill".equals(name))
                b.blendColor(value);

            else if ("mesh".equals(name))
                b.mesh(Boolean.parseBoolean(value));

            else if ("symbol-width".equals(name))
                b.symbolWidth = (int) (Integer.parseInt(value) * mScale);

            else if ("symbol-height".equals(name))
                b.symbolHeight = (int) (Integer.parseInt(value) * mScale);

            else if ("symbol-percent".equals(name))
                b.symbolPercent = Integer.parseInt(value);

            else if ("symbol-scaling".equals(name))
                ; // no-op

            else
                logUnknownAttribute(elementName, name, value, i);
        }

        if (src != null)
            b.texture = Utils.loadTexture(mTheme.getRelativePathPrefix(), src, mTheme.getResourceProvider(), b.symbolWidth, b.symbolHeight, b.symbolPercent, mThemeCallback);

        return b.build();
    }

    private LineStyle createOutline(String style) {
        if (style != null) {
            LineStyle line = (LineStyle) mStyles.get(OUTLINE_STYLE + style);
            if (line != null && line.outline) {
                String cat = null;

                for (int i = 0, n = mPullParser.getAttributeCount(); i < n; ++i) {
                    String name = mPullParser.getAttributeName(i);
                    String value = mPullParser.getAttributeValue(i);

                    if ("cat".equals(name)) {
                        cat = value;
                        break;
                    }
                }

                return line
                        .setCat(cat);
            }
        }
        log.fine("BUG not an outline style: " + style);
        return null;
    }

    private void createAtlas(String elementName) throws IOException {
        String img = null;

        for (int i = 0, n = mPullParser.getAttributeCount(); i < n; ++i) {
            String name = mPullParser.getAttributeName(i);
            String value = mPullParser.getAttributeValue(i);

            if ("img".equals(name)) {
                img = value;
            } else {
                logUnknownAttribute(elementName, name, value, i);
            }
        }
        validateExists("img", img, elementName);

        Bitmap bitmap = CanvasAdapter.getBitmapAsset(mTheme.getRelativePathPrefix(), img, mThemeCallback);
        if (bitmap != null)
            mTextureAtlas = new TextureAtlas(bitmap);
    }

    private void createTextureRegion(String elementName) {
        if (mTextureAtlas == null)
            return;

        String regionName = null;
        Rect r = null;

        for (int i = 0, n = mPullParser.getAttributeCount(); i < n; ++i) {
            String name = mPullParser.getAttributeName(i);
            String value = mPullParser.getAttributeValue(i);

            if ("id".equals(name)) {
                regionName = value;
            } else if ("pos".equals(name)) {
                String[] pos = value.split(" ");
                if (pos.length == 4) {
                    r = new Rect(Integer.parseInt(pos[0]),
                            Integer.parseInt(pos[1]),
                            Integer.parseInt(pos[2]),
                            Integer.parseInt(pos[3]));
                }
            } else {
                logUnknownAttribute(elementName, name, value, i);
            }
        }
        validateExists("id", regionName, elementName);
        validateExists("pos", r, elementName);

        mTextureAtlas.addTextureRegion(regionName.intern(), r);
    }

    private void checkElement(String elementName, Element element) throws XmlPullParserException {
        Element parentElement;
        switch (element) {
            case RENDER_THEME:
                if (!mElementStack.empty()) {
                    throw new XmlPullParserException(UNEXPECTED_ELEMENT_STACK_NOT_EMPTY + elementName);
                }
                return;

            case RULE:
                parentElement = mElementStack.peek();
                if (parentElement != Element.RENDER_THEME
                        && parentElement != Element.RULE) {
                    throw new XmlPullParserException(UNEXPECTED_ELEMENT_RULE_PARENT_ELEMENT_MISMATCH + elementName);
                }
                return;

            case STYLE:
                parentElement = mElementStack.peek();
                if (parentElement != Element.RENDER_THEME) {
                    throw new XmlPullParserException(UNEXPECTED_ELEMENT_STYLE_PARENT_ELEMENT_MISMATCH + elementName);
                }
                return;

            case RENDERING_INSTRUCTION:
                if (mElementStack.peek() != Element.RULE) {
                    throw new XmlPullParserException(UNEXPECTED_ELEMENT_RENDERING_INSTRUCTION_PARENT_ELEMENT_MISMATCH + elementName);
                }
                return;

            case ATLAS:
                parentElement = mElementStack.peek();
                if (parentElement != Element.RENDER_THEME) {
                    throw new XmlPullParserException(UNEXPECTED_ELEMENT_ATLAS_PARENT_ELEMENT_MISMATCH + elementName);
                }
                return;

            case RECT:
                parentElement = mElementStack.peek();
                if (parentElement != Element.ATLAS) {
                    throw new XmlPullParserException(UNEXPECTED_ELEMENT_RECT_PARENT_ELEMENT_MISMATCH + elementName);
                }
                return;

            case RENDERING_STYLE:
                return;

            case TAG_TRANSFORM:
                parentElement = mElementStack.peek();
                if (parentElement != Element.RENDER_THEME) {
                    throw new XmlPullParserException(UNEXPECTED_ELEMENT_TAG_TRANSFORM_PARENT_ELEMENT_MISMATCH + elementName);
                }
                return;
        }

        throw new XmlPullParserException("unknown enum value: " + element);
    }

    private void checkState(String elementName, Element element) throws XmlPullParserException {
        checkElement(elementName, element);
        mElementStack.push(element);
    }

    private void createRenderTheme(String elementName) {
        Integer version = null;
        int mapBackground = Color.WHITE;
        float baseStrokeWidth = 1;
        float baseTextScale = 1;

        for (int i = 0, n = mPullParser.getAttributeCount(); i < n; ++i) {
            String name = mPullParser.getAttributeName(i);
            String value = mPullParser.getAttributeValue(i);

            if ("xmlns:xsi".equals(name))
                continue;
            if ("xsi:schemaLocation".equals(name))
                continue;

            if ("xmlns".equals(name))
                mTheme.setMapsforgeTheme("http://mapsforge.org/renderTheme".equals(value));

            else if ("version".equals(name))
                version = Integer.parseInt(value);

            else if ("map-background".equals(name)) {
                mapBackground = Color.parseColor(value);
                if (mThemeCallback != null)
                    mapBackground = mThemeCallback.getColor(null, mapBackground);

            } else if ("base-stroke-width".equals(name))
                baseStrokeWidth = Float.parseFloat(value);

            else if ("base-text-scale".equals(name) || "base-text-size".equals(name))
                baseTextScale = Float.parseFloat(value);

            else if ("map-background-outside".equals(name))
                ; // no-op

            else
                logUnknownAttribute(elementName, name, value, i);

        }

        validateExists("version", version, elementName);

        int renderThemeVersion = mTheme.isMapsforgeTheme() ? RENDER_THEME_VERSION_MAPSFORGE : RENDER_THEME_VERSION_VTM;
        if (version > renderThemeVersion)
            throw new ThemeException("invalid render theme version:" + version);

        validateNonNegative("base-stroke-width", baseStrokeWidth);
        validateNonNegative("base-text-scale", baseTextScale);

        mMapBackground = mapBackground;
        mStrokeScale = baseStrokeWidth;
        mTextScale = baseTextScale;
    }

    private void handleTextElement(String qName, boolean isStyle, boolean isCaption) {

        String style = getStringAttribute("use");
        TextBuilder<?> pt = null;

        if (style != null) {
            pt = mTextStyles.get(style);
            if (pt == null) {
                log.fine("missing text style: " + style);
                return;
            }
        }

        TextBuilder<?> b = createText(qName, isCaption, pt);
        if (isStyle) {
            log.fine("put style " + b.style);
            mTextStyles.put(b.style, TextStyle.builder().from(b));
        } else {
            TextStyle text = b.buildInternal();
            if (isVisible(text))
                mCurrentRule.addStyle(text);
        }
    }

    /**
     * @param caption ...
     * @return a new Text with the given rendering attributes.
     */
    private TextBuilder<?> createText(String elementName, boolean caption, TextBuilder<?> style) {
        TextBuilder<?> b;
        if (style == null) {
            b = mTextBuilder.reset();
            b.caption = caption;
        } else
            b = mTextBuilder.from(style);
        b.themeCallback(mThemeCallback);
        String symbol = null;

        if (mTheme.isMapsforgeTheme()) {
            // Reset default priority
            b.priority = DEFAULT_PRIORITY;
        }

        for (int i = 0, n = mPullParser.getAttributeCount(); i < n; ++i) {
            String name = mPullParser.getAttributeName(i);
            String value = mPullParser.getAttributeValue(i);

            if ("id".equals(name))
                b.style = value;

            else if ("cat".equals(name))
                b.cat(value);

            else if ("k".equals(name))
                b.textKey = value.intern();

            else if ("font-family".equals(name))
                b.fontFamily = FontFamily.valueOf(value.toUpperCase(Locale.ENGLISH));

            else if ("style".equals(name) || "font-style".equals(name))
                b.fontStyle = FontStyle.valueOf(value.toUpperCase(Locale.ENGLISH));

            else if ("size".equals(name) || "font-size".equals(name))
                b.fontSize = Float.parseFloat(value);

            else if ("bg-fill".equals(name))
                b.bgFillColor = Color.parseColor(value);

            else if ("fill".equals(name))
                b.fillColor = Color.parseColor(value);

            else if ("stroke".equals(name))
                b.strokeColor = Color.parseColor(value);

            else if ("stroke-width".equals(name))
                b.strokeWidth = Float.parseFloat(value) * mScale;

            else if ("caption".equals(name))
                b.caption = Boolean.parseBoolean(value);

            else if ("priority".equals(name)) {
                b.priority = Integer.parseInt(value);

                if (mTheme.isMapsforgeTheme()) {
                    // Mapsforge: higher priorities are drawn first (0 = default priority)
                    // VTM: lower priorities are drawn first (0 = highest priority)
                    b.priority = FastMath.clamp(DEFAULT_PRIORITY - b.priority, 0, Integer.MAX_VALUE);
                }

            } else if ("area-size".equals(name))
                b.areaSize = Float.parseFloat(value);

            else if ("dy".equals(name))
                // NB: minus..
                b.dy = -Float.parseFloat(value) * mScale * CanvasAdapter.symbolScale;

            else if ("symbol".equals(name))
                symbol = value;

            else if ("use".equals(name))
                ;/* ignore */

            else if ("symbol-width".equals(name))
                b.symbolWidth = (int) (Integer.parseInt(value) * mScale);

            else if ("symbol-height".equals(name))
                b.symbolHeight = (int) (Integer.parseInt(value) * mScale);

            else if ("symbol-percent".equals(name))
                b.symbolPercent = Integer.parseInt(value);

            else if ("symbol-scaling".equals(name))
                ; // no-op

            else if ("position".equals(name)) {
                // Until implement position..
                if (b.dy == 0) {
                    value = "above".equals(value) ? "20" : "-20";
                    // NB: minus..
                    b.dy = -Float.parseFloat(value) * mScale * CanvasAdapter.symbolScale;
                }

            } else if ("display".equals(name))
                ; // no-op

            else if ("repeat".equals(name))
                ; // no-op

            else if ("repeat-gap".equals(name))
                ; // no-op

            else if ("repeat-start".equals(name))
                ; // no-op

            else if ("symbol-id".equals(name))
                ; // no-op

            else if ("text-orientation".equals(name))
                ; // no-op

            else if ("text-transform".equals(name))
                ; // no-op

            else if ("text-wrap-width".equals(name))
                ; // no-op

            else
                logUnknownAttribute(elementName, name, value, i);
        }

        validateExists("k", b.textKey, elementName);
        validateNonNegative("size", b.fontSize);
        validateNonNegative("stroke-width", b.strokeWidth);

        if (symbol != null && symbol.length() > 0) {
            String lowValue = symbol.toLowerCase(Locale.ENGLISH);
            if (lowValue.endsWith(".png") || lowValue.endsWith(".svg")) {
                try {
                    b.bitmap = CanvasAdapter.getBitmapAsset(mTheme.getRelativePathPrefix(), symbol, mTheme.getResourceProvider(), b.symbolWidth, b.symbolHeight, (int) (b.symbolPercent * CanvasAdapter.symbolScale), mThemeCallback);
                } catch (Exception e) {
                    log.severe(symbol + ": " + e);
                }
            } else
                b.texture = getAtlasRegion(symbol);
        }

        return b;
    }

    /**
     * @param level the drawing level of this instruction.
     * @return a new Circle with the given rendering attributes.
     */
    private CircleStyle createCircle(String elementName, int level) {
        CircleBuilder<?> b = mCircleBuilder.reset();
        b.level(level);
        b.themeCallback(mThemeCallback);

        for (int i = 0, n = mPullParser.getAttributeCount(); i < n; ++i) {
            String name = mPullParser.getAttributeName(i);
            String value = mPullParser.getAttributeValue(i);

            if ("r".equals(name) || "radius".equals(name))
                b.radius(Float.parseFloat(value) * mScale * mStrokeScale * CanvasAdapter.lineScale);

            else if ("cat".equals(name))
                b.cat(value);

            else if ("scale-radius".equals(name))
                b.scaleRadius(Boolean.parseBoolean(value));

            else if ("fill".equals(name))
                b.color(Color.parseColor(value));

            else if ("stroke".equals(name))
                b.strokeColor(Color.parseColor(value));

            else if ("stroke-width".equals(name))
                b.strokeWidth(Float.parseFloat(value) * mScale * mStrokeScale * CanvasAdapter.lineScale);

            else
                logUnknownAttribute(elementName, name, value, i);
        }

        validateExists("radius", b.radius, elementName);
        validateNonNegative("radius", b.radius);
        validateNonNegative("stroke-width", b.strokeWidth);

        return b.build();
    }

    private void handleSymbolElement(String qName, boolean isStyle) {

        String style = getStringAttribute("use");
        SymbolBuilder<?> ps = null;

        if (style != null) {
            ps = mSymbolStyles.get(style);
            if (ps == null) {
                log.fine("missing symbol style: " + style);
                return;
            }
        }

        SymbolBuilder<?> b = createSymbol(qName, ps);
        if (isStyle) {
            log.fine("put style " + b.style);
            mSymbolStyles.put(b.style, SymbolStyle.builder().from(b));
        } else {
            SymbolStyle symbol = buildSymbol(b);
            if (symbol != null && isVisible(symbol))
                mCurrentRule.addStyle(symbol);
        }
    }

    /**
     * @return a new Symbol with the given rendering attributes.
     */
    private SymbolBuilder<?> createSymbol(String elementName, SymbolBuilder<?> style) {
        SymbolBuilder<?> b;
        if (style == null)
            b = mSymbolBuilder.reset();
        else
            b = mSymbolBuilder.from(style);
        b.themeCallback(mThemeCallback);

        for (int i = 0, n = mPullParser.getAttributeCount(); i < n; ++i) {
            String name = mPullParser.getAttributeName(i);
            String value = mPullParser.getAttributeValue(i);

            if ("id".equals(name))
                b.style = value;

            else if ("src".equals(name))
                b.src(value);

            else if ("cat".equals(name))
                b.cat(value);

            else if ("use".equals(name))
                ;// ignore

            else if ("symbol-width".equals(name))
                b.symbolWidth = (int) (Integer.parseInt(value) * mScale);

            else if ("symbol-height".equals(name))
                b.symbolHeight = (int) (Integer.parseInt(value) * mScale);

            else if ("symbol-percent".equals(name))
                b.symbolPercent = Integer.parseInt(value);

            else if ("symbol-scaling".equals(name))
                ; // no-op

            else if ("billboard".equals(name))
                b.billboard(Boolean.parseBoolean(value));

            else if ("repeat".equals(name))
                b.repeat(Boolean.parseBoolean(value));

            else if ("repeat-start".equals(name))
                b.repeatStart = Float.parseFloat(value) * mScale;

            else if ("repeat-gap".equals(name))
                b.repeatGap = Float.parseFloat(value) * mScale;

            else if ("rotate".equals(name))
                b.rotate(Boolean.parseBoolean(value));

            else if ("display".equals(name))
                ; // no-op

            else if ("priority".equals(name))
                ; // no-op

            else
                logUnknownAttribute(elementName, name, value, i);
        }

        validateExists("src", b.src, elementName);

        return b;
    }

    private SymbolStyle buildSymbol(SymbolBuilder<?> b) {
        String lowSrc = b.src.toLowerCase(Locale.ENGLISH);
        if (lowSrc.endsWith(".png") || lowSrc.endsWith(".svg")) {
            try {
                float symbolScale = 1;
                switch (Parameters.SYMBOL_SCALING) {
                    case ALL:
                        symbolScale = CanvasAdapter.symbolScale;
                        break;
                    case POI:
                        if (!b.repeat)
                            symbolScale = CanvasAdapter.symbolScale;
                        break;
                }
                Bitmap bitmap = CanvasAdapter.getBitmapAsset(mTheme.getRelativePathPrefix(), b.src, mTheme.getResourceProvider(), b.symbolWidth, b.symbolHeight, (int) (b.symbolPercent * symbolScale), mThemeCallback);
                if (bitmap != null)
                    return buildSymbol(b, b.src, bitmap);
            } catch (Exception e) {
                log.severe(b.src + ": " + e);
            }
            return null;
        }
        return b.texture(getAtlasRegion(b.src)).build();
    }

    SymbolStyle buildSymbol(SymbolBuilder<?> b, String src, Bitmap bitmap) {
        return b.bitmap(bitmap).build();
    }

    private ExtrusionStyle createExtrusion(String elementName, int level) {
        ExtrusionBuilder<?> b = mExtrusionBuilder.reset();
        b.level(level);
        b.themeCallback(mThemeCallback);

        for (int i = 0, n = mPullParser.getAttributeCount(); i < n; ++i) {
            String name = mPullParser.getAttributeName(i);
            String value = mPullParser.getAttributeValue(i);

            if ("cat".equals(name))
                b.cat(value);

            else if ("side-color".equals(name))
                b.colorSide(Color.parseColor(value));

            else if ("top-color".equals(name))
                b.colorTop(Color.parseColor(value));

            else if ("line-color".equals(name))
                b.colorLine(Color.parseColor(value));

            else if ("hsv-h".equals(name))
                b.hsvHue(Double.parseDouble(value));

            else if ("hsv-s".equals(name))
                b.hsvSaturation(Double.parseDouble(value));

            else if ("hsv-v".equals(name))
                b.hsvValue(Double.parseDouble(value));

            else if ("default-height".equals(name))
                b.defaultHeight(Integer.parseInt(value));

            else
                logUnknownAttribute(elementName, name, value, i);
        }

        return b.build();
    }

    private String getStringAttribute(String name) {
        int n = mPullParser.getAttributeCount();
        for (int i = 0; i < n; i++) {
            if (mPullParser.getAttributeName(i).equals(name)) {
                return mPullParser.getAttributeValue(i);
            }
        }
        return null;
    }

    /**
     * A style is visible if categories is not set or the style has no category
     * or the categories contain the style's category.
     */
    private boolean isVisible(RenderStyle<?> renderStyle) {
        return mCategories == null || renderStyle.cat == null || mCategories.contains(renderStyle.cat);
    }

    /**
     * A rule is visible if categories is not set or the rule has no category
     * or the categories contain the rule's category.
     */
    private boolean isVisible(RuleBuilder rule) {
        return mCategories == null || rule.cat == null || mCategories.contains(rule.cat);
    }

    private static float[] parseFloatArray(String dashString) {
        String[] dashEntries = dashString.split(",");
        float[] dashIntervals = new float[dashEntries.length];
        for (int i = 0; i < dashEntries.length; ++i) {
            dashIntervals[i] = Float.parseFloat(dashEntries[i]);
        }
        return dashIntervals;
    }

    private void tagTransform(String qName) {
        String k, v, libK, libV;
        k = v = libK = libV = null;

        for (int i = 0, n = mPullParser.getAttributeCount(); i < n; ++i) {
            String name = mPullParser.getAttributeName(i);
            String value = mPullParser.getAttributeValue(i);

            switch (name) {
                case "k":
                    k = value;
                    break;
                case "v":
                    v = value;
                    break;
                case "k-lib":
                    libK = value;
                    break;
                case "v-lib":
                    libV = value;
                    break;
                default:
                    logUnknownAttribute(qName, name, value, i);
            }
        }

        if (k == null || k.isEmpty() || libK == null || libK.isEmpty()) {
            log.fine("empty key in element " + qName);
            return;
        }

        if (v == null && libV == null) {
            mTransformKeyMap.put(k, libK);
        } else {
            mTransformTagMap.put(new Tag(k, v), new Tag(libK, libV));
        }
    }

    private static void validateNonNegative(String name, float value) {
        if (value < 0)
            throw new ThemeException(name + " must not be negative: "
                    + value);
    }

    private static void validateExists(String name, Object obj, String elementName) {
        if (obj == null)
            throw new ThemeException("missing attribute " + name
                    + " for element: " + elementName);
    }

    /**
     * Building rule for Mapsforge themes.
     */
    private RuleBuilder buildingRule() {
        boolean dark = false;
        if (mTheme instanceof ExternalRenderTheme)
            dark = ((ExternalRenderTheme) mTheme).mPath.toLowerCase(Locale.ROOT).contains("dark");
        else if (mTheme instanceof ZipRenderTheme)
            dark = ((ZipRenderTheme) mTheme).mXmlTheme.toLowerCase(Locale.ROOT).contains("dark");
        ExtrusionBuilder<?> b = mExtrusionBuilder.reset();
        b.level(mLevels++);
        b.themeCallback(mThemeCallback);
        b.colorLine(dark ? 0x50606060 : 0xffd9d8d6);
        b.colorSide(dark ? 0xea404040 : 0xeaecebe9);
        b.colorTop(dark ? 0xea404040 : 0xeaf9f8f6);
        RuleBuilder rule = new RuleBuilder(RuleBuilder.RuleType.POSITIVE, new String[]{Tag.KEY_BUILDING, Tag.KEY_BUILDING_PART}, new String[]{});
        rule.element(Rule.Element.WAY).zoom((byte) 17, Byte.MAX_VALUE).style(b);
        return rule;
    }
}
