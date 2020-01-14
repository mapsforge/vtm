/*
 * Copyright 2019 Kostas Tzounopoulos
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
package org.oscim.android.mvt.tiling.source.mbtiles;

import org.oscim.android.tiling.source.mbtiles.MBTilesTileSource;

/**
 * A tile source for MBTiles vector databases.
 */
public class MBTilesMvtTileSource extends MBTilesTileSource {

    /**
     * Create a tile source for MBTiles vector databases.
     *
     * @param path the path to the MBTiles database.
     */
    public MBTilesMvtTileSource(String path) {
        this(path, null);
    }

    /**
     * Create a tile source for MBTiles vector databases.
     *
     * @param path     the path to the MBTiles database.
     * @param language the language to use when rendering the MBTiles.
     */
    public MBTilesMvtTileSource(String path, String language) {
        super(new MBTilesMvtTileDataSource(path, language));
    }
}
