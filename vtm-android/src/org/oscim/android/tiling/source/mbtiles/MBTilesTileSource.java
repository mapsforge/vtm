/*
 * Copyright 2019 Andrea Antonello
 * Copyright 2019 devemux86
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
package org.oscim.android.tiling.source.mbtiles;

import org.oscim.tiling.TileSource;

/**
 * A tile source for MBTiles databases.
 */
public abstract class MBTilesTileSource extends TileSource {

    private final MBTilesTileDataSource mTileDataSource;

    public MBTilesTileSource(MBTilesTileDataSource tileDataSource) {
        mTileDataSource = tileDataSource;
    }

    @Override
    public void close() {
        getDataSource().dispose();
    }

    @Override
    public MBTilesTileDataSource getDataSource() {
        return mTileDataSource;
    }

    @Override
    public OpenResult open() {
        return OpenResult.SUCCESS;
    }
}
