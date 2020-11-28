/*
 * Copyright 2017 Longri
 * Copyright 2017-2020 devemux86
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
import org.oscim.backend.Platform;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.renderer.atlas.TextureAtlas;
import org.oscim.renderer.atlas.TextureRegion;
import org.oscim.theme.IRenderTheme.ThemeException;
import org.oscim.theme.rule.Rule;
import org.oscim.theme.styles.RenderStyle;
import org.oscim.theme.styles.SymbolStyle;
import org.oscim.theme.styles.SymbolStyle.SymbolBuilder;
import org.oscim.utils.IOUtils;
import org.oscim.utils.TextureAtlasUtils;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlAtlasThemeBuilder extends XmlThemeBuilder {

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
            Map<Object, TextureRegion> outputMap = new HashMap<>();
            List<TextureAtlas> atlasList = new ArrayList<>();
            XmlAtlasThemeBuilder renderThemeHandler = new XmlAtlasThemeBuilder(theme, pullParser, themeCallback, outputMap, atlasList);
            inputStream = theme.getRenderThemeAsStream();
            pullParser.setInput(inputStream, null);
            renderThemeHandler.processRenderTheme();
            TextureAtlasUtils.createTextureRegions(renderThemeHandler.bitmapMap, outputMap, atlasList,
                    true, CanvasAdapter.platform == Platform.IOS);
            return replaceThemeSymbols(renderThemeHandler.mRenderTheme, outputMap);
        } catch (Exception e) {
            throw new ThemeException(e.getMessage());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private static IRenderTheme replaceThemeSymbols(RenderTheme renderTheme, Map<Object, TextureRegion> regionMap) {
        SymbolBuilder<?> symbolBuilder = SymbolStyle.builder();
        for (Rule rule : renderTheme.getRules()) {
            replaceRuleSymbols(rule, regionMap, symbolBuilder);
        }
        return renderTheme;
    }

    private static void replaceRuleSymbols(Rule rule, Map<Object, TextureRegion> regionMap, SymbolBuilder<?> b) {
        for (int i = 0, n = rule.styles.length; i < n; i++) {
            RenderStyle style = rule.styles[i];
            if (style instanceof SymbolStyle) {
                SymbolStyle symbol = (SymbolStyle) style;
                TextureRegion region = regionMap.get(symbol.hash);
                if (region != null)
                    rule.styles[i] = b.set(symbol)
                            .bitmap(null)
                            .texture(region)
                            .build();
            }
        }
        for (Rule subRule : rule.subRules) {
            replaceRuleSymbols(subRule, regionMap, b);
        }
    }

    private final Map<Object, TextureRegion> regionMap;
    private final List<TextureAtlas> atlasList;

    private final Map<Object, Bitmap> bitmapMap = new HashMap<>();

    public XmlAtlasThemeBuilder(ThemeFile theme, XmlPullParser pullParser,
                                Map<Object, TextureRegion> regionMap, List<TextureAtlas> atlasList) {
        this(theme, pullParser, null, regionMap, atlasList);
    }

    public XmlAtlasThemeBuilder(ThemeFile theme, XmlPullParser pullParser, ThemeCallback themeCallback,
                                Map<Object, TextureRegion> regionMap, List<TextureAtlas> atlasList) {
        super(theme, pullParser, themeCallback);
        this.regionMap = regionMap;
        this.atlasList = atlasList;
    }

    @Override
    RenderTheme createTheme(Rule[] rules) {
        return new AtlasRenderTheme(mMapBackground, mTextScale, rules, mLevels, mMapsforgeTheme, regionMap, atlasList);
    }

    @Override
    SymbolStyle buildSymbol(SymbolBuilder<?> b, String src, Bitmap bitmap) {
        // we need to hash with the width/height included as the same symbol could be required
        // in a different size and must be cached with a size-specific hash
        String absoluteName = CanvasAdapter.getAbsoluteFile(mTheme.getRelativePathPrefix(), src).getAbsolutePath();
        int hash = new StringBuilder().append(absoluteName).append(b.symbolWidth).append(b.symbolHeight).append(b.symbolPercent).toString().hashCode();
        bitmapMap.put(hash, bitmap);
        return b.hash(hash).build();
    }
}
