/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2016 devemux86
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

import android.content.Context;
import android.text.TextUtils;

import org.oscim.theme.IRenderTheme.ThemeException;
import org.oscim.theme.ThemeFile;
import org.oscim.theme.XmlRenderThemeMenuCallback;

import java.io.IOException;
import java.io.InputStream;

/**
 * An AssetsRenderTheme allows for customizing the rendering style of the map
 * via an XML from the Android assets folder.
 */
public class AssetsRenderTheme implements ThemeFile {
    private static final long serialVersionUID = 1L;

    private final InputStream mInputStream;
    private final XmlRenderThemeMenuCallback mMenuCallback;
    private final String mRelativePathPrefix;

    /**
     * @param context            the Android context.
     * @param relativePathPrefix the prefix for all relative resource paths.
     * @param fileName           the path to the XML render theme file.
     * @throws ThemeException if an error occurs while reading the render theme XML.
     */
    public AssetsRenderTheme(Context context, String relativePathPrefix, String fileName) throws ThemeException {
        this(context, relativePathPrefix, fileName, null);
    }

    /**
     * @param context            the Android context.
     * @param relativePathPrefix the prefix for all relative resource paths.
     * @param fileName           the path to the XML render theme file.
     * @param menuCallback       the interface callback to create a settings menu on the fly.
     * @throws ThemeException if an error occurs while reading the render theme XML.
     */
    public AssetsRenderTheme(Context context, String relativePathPrefix, String fileName, XmlRenderThemeMenuCallback menuCallback) throws ThemeException {
        mRelativePathPrefix = relativePathPrefix;
        mMenuCallback = menuCallback;

        try {
            mInputStream = context.getAssets().open((TextUtils.isEmpty(mRelativePathPrefix) ? "" : mRelativePathPrefix) + fileName);
        } catch (IOException e) {
            throw new ThemeException(e.getMessage());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof AssetsRenderTheme)) {
            return false;
        }
        AssetsRenderTheme other = (AssetsRenderTheme) obj;
        if (mInputStream != other.mInputStream) {
            return false;
        }
        if (mRelativePathPrefix != other.mRelativePathPrefix) {
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
}
