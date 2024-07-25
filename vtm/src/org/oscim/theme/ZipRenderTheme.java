/*
 * Copyright 2021 devemux86
 * Copyright 2021 eddiemuc
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

import org.oscim.theme.IRenderTheme.ThemeException;
import org.oscim.utils.Utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * A ZipRenderTheme allows for customizing the rendering style of the map
 * via an XML from an archive.
 */
public class ZipRenderTheme implements ThemeFile {
    private static final long serialVersionUID = 1L;

    private boolean mMapsforgeTheme;
    private XmlRenderThemeMenuCallback mMenuCallback;
    private final String mRelativePathPrefix;
    private XmlThemeResourceProvider mResourceProvider;
    protected final String mXmlTheme;

    /**
     * @param xmlTheme         the XML theme path in the archive.
     * @param resourceProvider the custom provider to retrieve resources internally referenced by "src" attribute (e.g. images, icons).
     * @throws ThemeException if an error occurs while reading the render theme XML.
     */
    public ZipRenderTheme(String xmlTheme, XmlThemeResourceProvider resourceProvider) throws ThemeException {
        this(xmlTheme, resourceProvider, null);
    }

    /**
     * @param xmlTheme         the XML theme path in the archive.
     * @param resourceProvider the custom provider to retrieve resources internally referenced by "src" attribute (e.g. images, icons).
     * @param menuCallback     the interface callback to create a settings menu on the fly.
     * @throws ThemeException if an error occurs while reading the render theme XML.
     */
    public ZipRenderTheme(String xmlTheme, XmlThemeResourceProvider resourceProvider, XmlRenderThemeMenuCallback menuCallback) throws ThemeException {
        mXmlTheme = xmlTheme;
        mResourceProvider = resourceProvider;
        mMenuCallback = menuCallback;

        mRelativePathPrefix = xmlTheme.substring(0, xmlTheme.lastIndexOf("/") + 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof ZipRenderTheme)) {
            return false;
        }
        ZipRenderTheme other = (ZipRenderTheme) obj;
        if (getRenderThemeAsStream() != other.getRenderThemeAsStream()) {
            return false;
        }
        if (!Utils.equals(mRelativePathPrefix, other.mRelativePathPrefix)) {
            return false;
        }
        return true;
    }

    @Override
    public XmlRenderThemeMenuCallback getMenuCallback() {
        return mMenuCallback;
    }

    @Override
    public String getRelativePathPrefix() {
        return mRelativePathPrefix;
    }

    @Override
    public InputStream getRenderThemeAsStream() throws ThemeException {
        try {
            return mResourceProvider.createInputStream(mRelativePathPrefix, mXmlTheme.substring(mXmlTheme.lastIndexOf("/") + 1));
        } catch (IOException e) {
            throw new ThemeException(e.toString(), e);
        }
    }

    @Override
    public XmlThemeResourceProvider getResourceProvider() {
        return mResourceProvider;
    }

    @Override
    public boolean isMapsforgeTheme() {
        return mMapsforgeTheme;
    }

    @Override
    public void setMapsforgeTheme(boolean mapsforgeTheme) {
        mMapsforgeTheme = mapsforgeTheme;
    }

    @Override
    public void setMenuCallback(XmlRenderThemeMenuCallback menuCallback) {
        mMenuCallback = menuCallback;
    }

    @Override
    public void setResourceProvider(XmlThemeResourceProvider resourceProvider) {
        mResourceProvider = resourceProvider;
    }
}
