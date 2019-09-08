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
 * Create a Vector MBTiles tile source (OpenMapTiles MVT with gzipped-pbf tiles)
 */
public class MBTilesMvtTileSource extends MBTilesTileSource {
    public MBTilesMvtTileSource(String databasePath) {
        this(databasePath, null);
    }

    /**
     * Create a MBTiles tile data source for Raster databases
     *
     * @param databasePath     the MBTiles database path
     * @param locale           the locale to use when rendering the MBTiles
     */
    public MBTilesMvtTileSource(String databasePath, String locale) {
        super(new MBTilesMvtTileDataSource(databasePath, locale));
    }
}
