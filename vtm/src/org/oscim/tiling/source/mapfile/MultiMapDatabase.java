/*
 * Copyright 2016-2022 devemux86
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

import org.oscim.core.Tile;
import org.oscim.layers.tile.MapTile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.QueryResult;
import org.oscim.tiling.TileDataSink;

import java.util.ArrayList;
import java.util.List;

public class MultiMapDatabase implements ITileDataSource {

    private final boolean deduplicate;
    private final List<MapDatabase> mapDatabases = new ArrayList<>();

    public MultiMapDatabase() {
        this(false);
    }

    public MultiMapDatabase(boolean deduplicate) {
        this.deduplicate = deduplicate;
    }

    public boolean add(MapDatabase mapDatabase) {
        if (mapDatabases.contains(mapDatabase)) {
            throw new IllegalArgumentException("Duplicate map database");
        }
        return mapDatabases.add(mapDatabase);
    }

    @Override
    public void query(MapTile tile, ITileDataSink sink) {
        boolean deduplicate = this.deduplicate;
        if (deduplicate) {
            int n = 0;
            for (MapDatabase mapDatabase : mapDatabases) {
                if (mapDatabase.supportsTile(tile)) {
                    if (++n > 1) {
                        break;
                    }
                }
            }
            deduplicate = n > 1;
        }

        TileDataSink dataSink = new TileDataSink(sink);
        for (int i = 0, n = mapDatabases.size(); i < n; i++) {
            MapDatabase mapDatabase = mapDatabases.get(i);
            if (mapDatabase.supportsTile(tile)) {
                mapDatabase.setDeduplicate(deduplicate);
                dataSink.level = i + 1;
                dataSink.levels = n;
                mapDatabase.query(tile, dataSink);
            }
        }
        sink.completed(QueryResult.SUCCESS);
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

    public MapReadResult readLabels(Tile tile, boolean deduplicate) {
        MapReadResult mapReadResult = new MapReadResult();
        for (MapDatabase mdb : mapDatabases) {
            if (mdb.supportsTile(tile)) {
                MapReadResult result = mdb.readLabels(tile);
                if (result == null) {
                    continue;
                }
                boolean isWater = mapReadResult.isWater & result.isWater;
                mapReadResult.isWater = isWater;
                mapReadResult.add(result, deduplicate);
            }
        }
        return mapReadResult;
    }

    public MapReadResult readLabels(Tile upperLeft, Tile lowerRight, boolean deduplicate) {
        MapReadResult mapReadResult = new MapReadResult();
        for (MapDatabase mdb : mapDatabases) {
            if (mdb.supportsTile(upperLeft)) {
                MapReadResult result = mdb.readLabels(upperLeft, lowerRight);
                if (result == null) {
                    continue;
                }
                boolean isWater = mapReadResult.isWater & result.isWater;
                mapReadResult.isWater = isWater;
                mapReadResult.add(result, deduplicate);
            }
        }
        return mapReadResult;
    }

    public MapReadResult readMapData(Tile tile, boolean deduplicate) {
        MapReadResult mapReadResult = new MapReadResult();
        for (MapDatabase mdb : mapDatabases) {
            if (mdb.supportsTile(tile)) {
                MapReadResult result = mdb.readMapData(tile);
                if (result == null) {
                    continue;
                }
                boolean isWater = mapReadResult.isWater & result.isWater;
                mapReadResult.isWater = isWater;
                mapReadResult.add(result, deduplicate);
            }
        }
        return mapReadResult;
    }

    public MapReadResult readMapData(Tile upperLeft, Tile lowerRight, boolean deduplicate) {
        MapReadResult mapReadResult = new MapReadResult();
        for (MapDatabase mdb : mapDatabases) {
            if (mdb.supportsTile(upperLeft)) {
                MapReadResult result = mdb.readMapData(upperLeft, lowerRight);
                if (result == null) {
                    continue;
                }
                boolean isWater = mapReadResult.isWater & result.isWater;
                mapReadResult.isWater = isWater;
                mapReadResult.add(result, deduplicate);
            }
        }
        return mapReadResult;
    }

    public MapReadResult readPoiData(Tile tile, boolean deduplicate) {
        MapReadResult mapReadResult = new MapReadResult();
        for (MapDatabase mdb : mapDatabases) {
            if (mdb.supportsTile(tile)) {
                MapReadResult result = mdb.readPoiData(tile);
                if (result == null) {
                    continue;
                }
                boolean isWater = mapReadResult.isWater & result.isWater;
                mapReadResult.isWater = isWater;
                mapReadResult.add(result, deduplicate);
            }
        }
        return mapReadResult;
    }

    public MapReadResult readPoiData(Tile upperLeft, Tile lowerRight, boolean deduplicate) {
        MapReadResult mapReadResult = new MapReadResult();
        for (MapDatabase mdb : mapDatabases) {
            if (mdb.supportsTile(upperLeft)) {
                MapReadResult result = mdb.readPoiData(upperLeft, lowerRight);
                if (result == null) {
                    continue;
                }
                boolean isWater = mapReadResult.isWater & result.isWater;
                mapReadResult.isWater = isWater;
                mapReadResult.add(result, deduplicate);
            }
        }
        return mapReadResult;
    }

    public boolean supportsTile(Tile tile) {
        for (MapDatabase mdb : mapDatabases) {
            if (mdb.supportsTile(tile)) {
                return true;
            }
        }
        return false;
    }
}
