/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
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

public class ThemeLoader {

    public static IRenderTheme load(String renderThemePath) throws ThemeException {
        return load(new ExternalRenderTheme(renderThemePath));
    }

    public static IRenderTheme load(String renderThemePath, XmlRenderThemeMenuCallback menuCallback) throws ThemeException {
        return load(new ExternalRenderTheme(renderThemePath, menuCallback));
    }

    public static IRenderTheme load(ThemeFile theme) throws ThemeException {
        IRenderTheme t = XmlThemeBuilder.read(theme);
        if (t != null)
            t.scaleTextSize(CanvasAdapter.textScale + (CanvasAdapter.dpi / 240 - 1) * 0.5f);
        return t;
    }
}
