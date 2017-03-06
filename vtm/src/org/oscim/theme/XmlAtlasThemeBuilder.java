package org.oscim.theme;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.Platform;
import org.oscim.backend.XMLReaderAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.renderer.atlas.TextureAtlas;
import org.oscim.renderer.atlas.TextureRegion;
import org.oscim.theme.rule.Rule;
import org.oscim.theme.styles.RenderStyle;
import org.oscim.theme.styles.SymbolStyle;
import org.oscim.utils.TextureAtlasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

import java.util.*;

/**
 * Created by Longri on 28.02.2017.
 */
public class XmlAtlasThemeBuilder extends XmlThemeBuilder {
    private static final Logger log = LoggerFactory.getLogger(XmlThemeBuilder.class);

    private final Map<Object, Bitmap> bitmapMap = new HashMap<>();
    private final Map<Object, TextureRegion> outputMap;
    private final List<TextureAtlas> atlasList;

    public XmlAtlasThemeBuilder(ThemeFile theme, ThemeCallback themeCallback, Map<Object
            , TextureRegion> outputMap, List<TextureAtlas> atlasList) {
        super(theme, themeCallback);
        this.outputMap = outputMap;
        this.atlasList = atlasList;
    }

    /**
     * @param theme         an input theme containing valid render theme XML data.
     * @param themeCallback the theme callback.
     * @return a new RenderTheme which is created by parsing the XML data from the input theme.
     * @throws IRenderTheme.ThemeException if an error occurs while parsing the render theme XML.
     */
    public static IRenderTheme read(ThemeFile theme, ThemeCallback themeCallback) throws IRenderTheme.ThemeException {

        Map<Object, TextureRegion> outputMap = new HashMap<>();
        List<TextureAtlas> atlasList = new ArrayList<>();
        XmlAtlasThemeBuilder renderThemeHandler = new XmlAtlasThemeBuilder(theme, themeCallback, outputMap, atlasList);

        try {
            new XMLReaderAdapter().parse(renderThemeHandler, theme.getRenderThemeAsStream());
        } catch (Exception e) {
            throw new IRenderTheme.ThemeException(e.getMessage());
        }


        TextureAtlasUtils.createTextureRegions(renderThemeHandler.bitmapMap, outputMap, atlasList,
                true, CanvasAdapter.platform == Platform.IOS);

        return replaceSymbolStylesOnTheme(outputMap, renderThemeHandler.mRenderTheme);
    }


    private static IRenderTheme replaceSymbolStylesOnTheme(Map<Object, TextureRegion> regionMap, RenderTheme theme) {
        SymbolStyle.SymbolBuilder<?> symbolBuilder = new SymbolStyle.SymbolBuilder();
        Rule[] rules = theme.getRules();
        for (Rule rule : rules) {
            replaceSymbolStylesOnRules(regionMap, symbolBuilder, rule);
        }
        return theme;
    }

    private static void replaceSymbolStylesOnRules(Map<Object, TextureRegion> regionMap,
                                                   SymbolStyle.SymbolBuilder<?> symbolBuilder, Rule rule) {

        for (int i = 0, n = rule.styles.length; i < n; i++) {
            RenderStyle style = rule.styles[i];
            if (style instanceof SymbolStyle) {
                String sourceName = ((SymbolStyle) style).sourceName;

                TextureRegion region = regionMap.get(sourceName);
                if (region != null) {
                    symbolBuilder = symbolBuilder.reset();
                    rule.styles[i] = symbolBuilder.texture(region).build();
                }

            }
        }
        for (Rule subRule : rule.subRules) {
            replaceSymbolStylesOnRules(regionMap, symbolBuilder, subRule);
        }

    }


    /**
     * @return a new Symbol with the given rendering attributes.
     */
    protected SymbolStyle createSymbol(String elementName, Attributes attributes) {
        SymbolStyle.SymbolBuilder<?> b = mSymbolBuilder.reset();
        String src = null;

        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getLocalName(i);
            String value = attributes.getValue(i);

            if ("src".equals(name))
                src = value;

            else if ("cat".equals(name))
                b.cat(value);

            else if ("symbol-width".equals(name))
                b.symbolWidth = (int) (Integer.parseInt(value) * mScale);

            else if ("symbol-height".equals(name))
                b.symbolHeight = (int) (Integer.parseInt(value) * mScale);

            else if ("symbol-percent".equals(name))
                b.symbolPercent = Integer.parseInt(value);

            else
                logUnknownAttribute(elementName, name, value, i);
        }

        validateExists("src", src, elementName);

        String lowSrc = src.toLowerCase(Locale.ENGLISH);
        if (lowSrc.endsWith(".png") || lowSrc.endsWith(".svg")) {
            try {
                Bitmap bitmap = CanvasAdapter.getBitmapAsset(mTheme.getRelativePathPrefix(), src, b.symbolWidth, b.symbolHeight, b.symbolPercent);
                if (bitmap != null) {
                    //create a property depends name! (needed if the same image used with different sizes)
                    String sourceName = lowSrc + b.symbolWidth + "/" + b.symbolHeight + "/" + b.symbolPercent;
                    bitmapMap.put(sourceName, bitmap);
                    return b.sourceName(sourceName).build();
                }

            } catch (Exception e) {
                log.debug(e.getMessage());
            }
            return null;
        }
        return b.texture(getAtlasRegion(src)).build();
    }

    @Override
    public void endDocument() {

        Rule[] rules = new Rule[mRulesList.size()];
        for (int i = 0, n = rules.length; i < n; i++)
            rules[i] = mRulesList.get(i).onComplete(null);

        mRenderTheme = new AtlasRenderTheme(mMapBackground, mTextScale, rules, mLevels, this.outputMap, this.atlasList);

        mRulesList.clear();
        mStyles.clear();
        mRuleStack.clear();
        mElementStack.clear();

        mTextureAtlas = null;
    }
}
