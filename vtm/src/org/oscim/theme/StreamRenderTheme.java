/*
 * Copyright 2016-2021 devemux86
 * Copyright 2017 Andrey Novikov
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

import java.io.InputStream;

/**
 * A StreamRenderTheme allows for customizing the rendering style of the map
 * via an XML input stream.
 */
public class StreamRenderTheme implements ThemeFile {
    private static final long serialVersionUID = 1L;

    private final InputStream mInputStream;
    private boolean mMapsforgeTheme;
    private XmlRenderThemeMenuCallback mMenuCallback;
    private final String mRelativePathPrefix;
    private XmlThemeResourceProvider mResourceProvider;

    /**
     * @param relativePathPrefix the prefix for all relative resource paths.
     * @param inputStream        an input stream containing valid render theme XML data.
     * @throws ThemeException if an error occurs while reading the render theme XML.
     */
    public StreamRenderTheme(String relativePathPrefix, InputStream inputStream) throws ThemeException {
        this(relativePathPrefix, inputStream, null);
    }

    /**
     * @param relativePathPrefix the prefix for all relative resource paths.
     * @param inputStream        an input stream containing valid render theme XML data.
     * @param menuCallback       the interface callback to create a settings menu on the fly.
     * @throws ThemeException if an error occurs while reading the render theme XML.
     */
    public StreamRenderTheme(String relativePathPrefix, InputStream inputStream, XmlRenderThemeMenuCallback menuCallback) throws ThemeException {
        mRelativePathPrefix = relativePathPrefix;
        mInputStream = inputStream;
        mMenuCallback = menuCallback;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof StreamRenderTheme)) {
            return false;
        }
        StreamRenderTheme other = (StreamRenderTheme) obj;
        if (mInputStream != other.mInputStream) {
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
        return mInputStream;
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
