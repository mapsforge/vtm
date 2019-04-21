/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2018 devemux86
 * Copyright 2018-2019 Gustl22
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
package org.oscim.gdx.lwjgl3;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.utils.SharedLibraryLoader;

import org.oscim.awt.AwtGraphics;
import org.oscim.backend.DateTime;
import org.oscim.backend.DateTimeAdapter;
import org.oscim.backend.GLAdapter;
import org.oscim.core.Tile;
import org.oscim.gdx.GdxAssets;
import org.oscim.gdx.GdxMap;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;
import org.oscim.utils.FastMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GdxMapApp extends GdxMap {

    public static final Logger log = LoggerFactory.getLogger(GdxMapApp.class);

    public static void init() {
        // load native library
        new SharedLibraryLoader().load("vtm-jni");
        // init globals
        AwtGraphics.init();
        GdxAssets.init("assets/");
        DateTimeAdapter.init(new DateTime());
    }

    public static void main(String[] args) {
        init();
        new Lwjgl3Application(new GdxMapApp(), getConfig(null));
    }

    public static void run(GdxMap map) {
        run(map, null, Tile.SIZE);
    }

    public static void run(GdxMap map, Lwjgl3ApplicationConfiguration config, int tileSize) {
        Tile.SIZE = FastMath.clamp(tileSize, 128, 512);

        new Lwjgl3Application(map, (config == null ? getConfig(map.getClass().getSimpleName()) : config));
    }

    protected static Lwjgl3ApplicationConfiguration getConfig(String title) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.disableAudio(true);
        cfg.setTitle(title != null ? title : "vtm-gdx-lwjgl3");

        int[] sizes = new int[]{128, 64, 32, 16};
        String[] paths = new String[sizes.length];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = "res/ic_vtm_" + sizes[i] + ".png";
        }
        cfg.setWindowIcon(Files.FileType.Internal, paths);

        cfg.setWindowedMode(1024, 768);
        cfg.setBackBufferConfig(8, 8, 8, 8, 16, 8, 0);
        //cfg.samples = 2;
        //cfg.useOpenGL3(true, 3, 2);
        cfg.setIdleFPS(10);
        //cfg.forceExit = false;
        return cfg;
    }

    @Override
    protected void initGLAdapter(GLVersion version) {
        if (version.getMajorVersion() >= 3)
            GLAdapter.init(new Lwjgl3GL30());
        else
            GLAdapter.init(new Lwjgl3GL20());
    }

    @Override
    public void createLayers() {
        TileSource tileSource = new OSciMap4TileSource();

        initDefaultLayers(tileSource, false, true, true);

        mMap.setMapPosition(0, 0, 1 << 2);
    }

    @Override
    public void dispose() {
        System.exit(0);
    }
}
