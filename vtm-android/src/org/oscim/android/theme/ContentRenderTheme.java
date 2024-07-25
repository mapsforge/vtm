/*
 * Copyright 2020-2021 devemux86
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
package org.oscim.android.theme;

import android.content.ContentResolver;
import android.net.Uri;
import org.oscim.theme.IRenderTheme.ThemeException;
import org.oscim.theme.ThemeFile;
import org.oscim.theme.XmlRenderThemeMenuCallback;
import org.oscim.theme.XmlThemeResourceProvider;

import java.io.IOException;
import java.io.InputStream;

/**
 * An ContentRenderTheme allows for customizing the rendering style of the map
 * via an XML from the Android content providers.
 */
public class ContentRenderTheme implements ThemeFile {
    private static final long serialVersionUID = 1L;

    private final ContentResolver mContentResolver;
    private boolean mMapsforgeTheme;
    private XmlRenderThemeMenuCallback mMenuCallback;
    private XmlThemeResourceProvider mResourceProvider;
    private final Uri mUri;

    /**
     * @param contentResolver the Android content resolver.
     * @param uri             the XML render theme URI.
     * @throws ThemeException if an error occurs while reading the render theme XML.
     */
    public ContentRenderTheme(ContentResolver contentResolver, Uri uri) throws ThemeException {
        this(contentResolver, uri, null);
    }

    /**
     * @param contentResolver the Android content resolver.
     * @param uri             the XML render theme URI.
     * @param menuCallback    the interface callback to create a settings menu on the fly.
     * @throws ThemeException if an error occurs while reading the render theme XML.
     */
    public ContentRenderTheme(ContentResolver contentResolver, Uri uri, XmlRenderThemeMenuCallback menuCallback) throws ThemeException {
        mContentResolver = contentResolver;
        mUri = uri;
        mMenuCallback = menuCallback;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof ContentRenderTheme)) {
            return false;
        }
        ContentRenderTheme other = (ContentRenderTheme) obj;
        if (getRenderThemeAsStream() != other.getRenderThemeAsStream()) {
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
        return "";
    }

    @Override
    public InputStream getRenderThemeAsStream() throws ThemeException {
        try {
            return mContentResolver.openInputStream(mUri);
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
