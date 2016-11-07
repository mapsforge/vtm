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
package org.oscim.tiling.source.mapfile;

import org.oscim.layers.tile.MapTile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;

import java.util.ArrayList;
import java.util.List;

public class MultiMapDatabase implements ITileDataSource {

    private final List<MapDatabase> mapDatabases = new ArrayList<>();
    private final MultiMapFileTileSource tileSource;

    public MultiMapDatabase(MultiMapFileTileSource tileSource) {
        this.tileSource = tileSource;
    }

    public boolean add(MapDatabase mapDatabase) {
        if (mapDatabases.contains(mapDatabase)) {
            throw new IllegalArgumentException("Duplicate map database");
        }
        return mapDatabases.add(mapDatabase);
    }

    @Override
    public void query(MapTile tile, ITileDataSink mapDataSink) {
        MultiMapDataSink multiMapDataSink = new MultiMapDataSink(mapDataSink);
        for (MapDatabase mapDatabase : mapDatabases) {
            int[] zoomLevels = tileSource.getZoomsByTileSource().get(mapDatabase.getTileSource());
            if (zoomLevels == null || (zoomLevels[0] <= tile.zoomLevel && tile.zoomLevel <= zoomLevels[1]))
                mapDatabase.query(tile, multiMapDataSink);
        }
        mapDataSink.completed(multiMapDataSink.getResult());
    }

    @Override
    public void dispose() {
        for (MapDatabase mapDatabase : mapDatabases) {
            mapDatabase.dispose();
        }
    }

    @Override
    public void cancel() {
        for (MapDatabase mapDatabase : mapDatabases) {
            mapDatabase.cancel();
        }
    }
}
