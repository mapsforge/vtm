/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2016-2021 devemux86
 * Copyright 2017 Andrey Novikov
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

import android.content.res.AssetManager;
import android.text.TextUtils;
import org.oscim.theme.IRenderTheme.ThemeException;
import org.oscim.theme.ThemeFile;
import org.oscim.theme.XmlRenderThemeMenuCallback;
import org.oscim.utils.Utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * An AssetsRenderTheme allows for customizing the rendering style of the map
 * via an XML from the Android assets folder.
 */
public class AssetsRenderTheme implements ThemeFile {
    private static final long serialVersionUID = 1L;

    private final AssetManager mAssetManager;
    private final String mFileName;
    private boolean mMapsforgeTheme;
    private XmlRenderThemeMenuCallback mMenuCallback;
    private final String mRelativePathPrefix;

    /**
     * @param assetManager       the Android asset manager.
     * @param relativePathPrefix the prefix for all relative resource paths.
     * @param fileName           the path to the XML render theme file.
     * @throws ThemeException if an error occurs while reading the render theme XML.
     */
    public AssetsRenderTheme(AssetManager assetManager, String relativePathPrefix, String fileName) throws ThemeException {
        this(assetManager, relativePathPrefix, fileName, null);
    }

    /**
     * @param assetManager       the Android asset manager.
     * @param relativePathPrefix the prefix for all relative resource paths.
     * @param fileName           the path to the XML render theme file.
     * @param menuCallback       the interface callback to create a settings menu on the fly.
     * @throws ThemeException if an error occurs while reading the render theme XML.
     */
    public AssetsRenderTheme(AssetManager assetManager, String relativePathPrefix, String fileName, XmlRenderThemeMenuCallback menuCallback) throws ThemeException {
        mAssetManager = assetManager;
        mRelativePathPrefix = relativePathPrefix;
        mFileName = fileName;
        mMenuCallback = menuCallback;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof AssetsRenderTheme)) {
            return false;
        }
        AssetsRenderTheme other = (AssetsRenderTheme) obj;
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
            return mAssetManager.open((TextUtils.isEmpty(mRelativePathPrefix) ? "" : mRelativePathPrefix) + mFileName);
        } catch (IOException e) {
            throw new ThemeException(e.getMessage());
        }
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
}
