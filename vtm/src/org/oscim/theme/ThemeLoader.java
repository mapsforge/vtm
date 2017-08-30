/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
 * Copyright 2017 Longri
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
import org.oscim.theme.IRenderTheme.ThemeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

public class ThemeLoader {

    private static final Logger log = LoggerFactory.getLogger(ThemeLoader.class);

    public static boolean USE_ATLAS;
    public static boolean POT_TEXTURES;

    public static IRenderTheme load(String renderThemePath) throws ThemeException {
        return load(new ExternalRenderTheme(renderThemePath));
    }

    public static IRenderTheme load(String renderThemePath, XmlRenderThemeMenuCallback menuCallback) throws ThemeException {
        return load(new ExternalRenderTheme(renderThemePath, menuCallback));
    }

    public static IRenderTheme load(String renderThemePath, ThemeCallback themeCallback) throws ThemeException {
        return load(new ExternalRenderTheme(renderThemePath), themeCallback);
    }

    public static IRenderTheme load(String renderThemePath, XmlRenderThemeMenuCallback menuCallback, ThemeCallback themeCallback) throws ThemeException {
        return load(new ExternalRenderTheme(renderThemePath, menuCallback), themeCallback);
    }

    public static IRenderTheme load(ThemeFile theme) throws ThemeException {
        return load(theme, null);
    }



    public static IRenderTheme load(ThemeFile theme, ThemeCallback themeCallback) throws ThemeException {
        IRenderTheme t = null;

        try {
            if(ThemeUtils.isMapsforgeTheme(theme.getRenderThemeAsStream())){
                t = USE_ATLAS ? XmlMapsforgeAtlasThemeBuilder.read(theme, themeCallback) : XmlMapsforgeThemeBuilder.read(theme, themeCallback);
            }else{
                t = USE_ATLAS ? XmlAtlasThemeBuilder.read(theme, themeCallback) : XmlThemeBuilder.read(theme, themeCallback);
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

        if (t != null)
            t.scaleTextSize(CanvasAdapter.textScale + (CanvasAdapter.dpi / CanvasAdapter.DEFAULT_DPI - 1));
        return t;
    }
}
