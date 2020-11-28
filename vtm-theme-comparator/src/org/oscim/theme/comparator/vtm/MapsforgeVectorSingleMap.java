/*
 * Copyright 2017 Longri
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
package org.oscim.theme.comparator.vtm;

import com.badlogic.gdx.files.FileHandle;

import org.oscim.layers.tile.TileLayer;
import org.oscim.layers.tile.vector.OsmTileLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.map.Map;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.mapfile.MapFileTileSource;

class MapsforgeVectorSingleMap {
    private VectorTileLayer vectorTileLayer;
    private final FileHandle mapFile;
    private MapFileTileSource tileSource;

    MapsforgeVectorSingleMap(FileHandle mapFile) {
        this.mapFile = mapFile;
    }

    TileSource getVectorTileSource() {
        if (tileSource == null) {
            tileSource = new MapFileTileSource();
            tileSource.setMapFile(mapFile.path());
            tileSource.setPreferredLanguage("en");
        }
        return tileSource;
    }

    TileLayer getTileLayer(Map map) {
        if (vectorTileLayer == null) {
            if (tileSource == null) {
                tileSource = new MapFileTileSource();
                tileSource.setMapFile(mapFile.path());
                tileSource.setPreferredLanguage("en");
            }
            vectorTileLayer = new OsmTileLayer(map, tileSource);
        }
        return vectorTileLayer;
    }
}
