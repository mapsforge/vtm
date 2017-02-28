/*
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
package org.oscim.test;

import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.gdx.GdxMap;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.ThemeLoader;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.oscim.tiling.source.mapfile.MapInfo;

import java.io.File;

public class MapsforgeTest extends GdxMap {

    private static File mapFile;

    @Override
    public void createLayers() {
        MapFileTileSource tileSource = new MapFileTileSource();
        tileSource.setMapFile(mapFile.getAbsolutePath());
        tileSource.setPreferredLanguage("en");

        VectorTileLayer l = mMap.setBaseMap(tileSource);
        ThemeLoader.useAtlas = true;
        mMap.setTheme(VtmThemes.DEFAULT);

        mMap.layers().add(new BuildingLayer(mMap, l));
        mMap.layers().add(new LabelLayer(mMap, l));

        MapInfo info = tileSource.getMapInfo();
        MapPosition pos = new MapPosition();
        pos.setByBoundingBox(info.boundingBox, Tile.SIZE * 4, Tile.SIZE * 4);
        mMap.setMapPosition(pos);
    }

    private static File getMapFile(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("missing argument: <mapFile>");
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            throw new IllegalArgumentException("file does not exist: " + file);
        } else if (!file.isFile()) {
            throw new IllegalArgumentException("not a file: " + file);
        } else if (!file.canRead()) {
            throw new IllegalArgumentException("cannot read file: " + file);
        }
        return file;
    }

    public static void main(String[] args) {
        mapFile = getMapFile(args);

        GdxMapApp.init();
        GdxMapApp.run(new MapsforgeTest());
    }
}
