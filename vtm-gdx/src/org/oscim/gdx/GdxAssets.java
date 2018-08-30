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
package org.oscim.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import org.oscim.backend.AssetAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class GdxAssets extends AssetAdapter {
    private static final Logger log = LoggerFactory.getLogger(GdxAssets.class);

    static String pathPrefix = "";

    private GdxAssets(String path) {
        pathPrefix = path;
    }

    @Override
    public InputStream openFileAsStream(String fileName) {
        FileHandle file = Gdx.files.internal(pathPrefix + fileName);
        if (file == null)
            throw new IllegalArgumentException("missing file " + fileName);

        try {
            return file.read();
        } catch (GdxRuntimeException e) {
            log.debug(e.getMessage());
            return null;
        }
    }

    public static void init(String path) {
        AssetAdapter.init(new GdxAssets(path));
    }

    /**
     * Get file path in GDX assets.
     */
    public static String getAssetPath(String fileName) {
        return Gdx.files.internal(pathPrefix + fileName).path();
    }
}
