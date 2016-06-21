/*
 * Copyright 2013 Hannes Janetzek
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
package org.oscim.ios;

import com.badlogic.gdx.utils.SharedLibraryLoader;
import org.oscim.backend.GLAdapter;
import org.oscim.gdx.GdxAssets;
import org.oscim.gdx.GdxMap;
import org.oscim.ios.backend.IosGL;
import org.oscim.ios.backend.IosGraphics;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOSMapApp extends GdxMap {

    public static final Logger log = LoggerFactory.getLogger(IOSMapApp.class);

    public static void init() {
        // load native library
        new SharedLibraryLoader().load("vtm-jni");
        // init globals
		IosGraphics.init();
		GdxAssets.init("assets/");
        GLAdapter.init(new IosGL());
//		GLAdapter.GDX_DESKTOP_QUIRKS = true;
    }

//    @Override
//    public void create() {
//
//    }
//
//    @Override
//    public void resize(int w, int h) {
//
//    }


    @Override
    public void createLayers() {
        TileSource tileSource = new OSciMap4TileSource();

        // TileSource tileSource = new MapFileTileSource();
        // tileSource.setOption("file", "/home/jeff/germany.map");

        initDefaultLayers(tileSource, false, true, true);

        //mMap.getLayers().add(new BitmapTileLayer(mMap, new ImagicoLandcover(), 20));
        //mMap.getLayers().add(new BitmapTileLayer(mMap, new OSMTileSource(), 20));
        //mMap.getLayers().add(new BitmapTileLayer(mMap, new ArcGISWorldShaded(), 20));

        mMap.setMapPosition(0, 0, 1 << 2);
    }
}
