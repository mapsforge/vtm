/*
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
package org.oscim.test;

import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.gdx.GdxMap;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.oscim.tiling.source.mapfile.MultiMapFileTileSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapsforgeMultiTest extends GdxMap {

    private final List<File> mapFiles;

    private MapsforgeMultiTest(List<File> mapFiles) {
        this.mapFiles = mapFiles;
    }

    @Override
    public void createLayers() {
        MultiMapFileTileSource tileSource = new MultiMapFileTileSource();
        for (File mapFile : mapFiles) {
            MapFileTileSource mapFileTileSource = new MapFileTileSource();
            mapFileTileSource.setMapFile(mapFile.getAbsolutePath());
            tileSource.add(mapFileTileSource);
        }
        tileSource.setPreferredLanguage("en");

        VectorTileLayer l = mMap.setBaseMap(tileSource);
        mMap.setTheme(VtmThemes.DEFAULT);

        mMap.layers().add(new BuildingLayer(mMap, l));
        mMap.layers().add(new LabelLayer(mMap, l));

        MapPosition pos = new MapPosition();
        pos.setByBoundingBox(tileSource.getBoundingBox(), Tile.SIZE * 4, Tile.SIZE * 4);
        mMap.setMapPosition(pos);
    }

    private static List<File> getMapFiles(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("missing argument: <mapFile>");
        }

        List<File> result = new ArrayList<>();
        for (String arg : args) {
            File mapFile = new File(arg);
            if (!mapFile.exists()) {
                throw new IllegalArgumentException("file does not exist: " + mapFile);
            } else if (!mapFile.isFile()) {
                throw new IllegalArgumentException("not a file: " + mapFile);
            } else if (!mapFile.canRead()) {
                throw new IllegalArgumentException("cannot read file: " + mapFile);
            }
            result.add(mapFile);
        }
        return result;
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new MapsforgeMultiTest(getMapFiles(args)));
    }
}
