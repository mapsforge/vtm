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

import org.oscim.core.BoundingBox;
import org.oscim.core.Tile;
import org.oscim.layers.tile.MapTile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.QueryResult;
import org.oscim.tiling.TileDataSink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class MultiMapFile implements ITileDataSource {

    private static final Logger log = Logger.getLogger(MultiMapFile.class.getName());

    private final boolean deduplicate;
    private final List<MapFile> mapFiles = new ArrayList<>();

    public MultiMapFile() {
        this(false);
    }

    public MultiMapFile(boolean deduplicate) {
        this.deduplicate = deduplicate;
    }

    public boolean add(MapFile mapFile) {
        if (mapFiles.contains(mapFile)) {
            throw new IllegalArgumentException("Duplicate map file");
        }
        mapFiles.add(mapFile);
        Collections.sort(mapFiles, new Comparator<MapFile>() {
            @Override
            public int compare(MapFile md1, MapFile md2) {
                // Reverse order
                return -Integer.compare(md1.getPriority(), md2.getPriority());
            }
        });
        return true;
    }

    @Override
    public void query(MapTile tile, ITileDataSink sink) {
        try {
            boolean deduplicate = this.deduplicate;
            if (deduplicate) {
                int n = 0;
                for (MapFile mapFile : mapFiles) {
                    if (mapFile.supportsTile(tile)) {
                        if (++n > 1) {
                            break;
                        }
                    }
                }
                deduplicate = n > 1;
            }

            TileDataSink dataSink = new TileDataSink(sink);
            boolean isTileFilled = false;
            for (int i = 0, n = mapFiles.size(); i < n; i++) {
                MapFile mapFile = mapFiles.get(i);
                if (isTileFilled && mapFile.getPriority() < 0) {
                    break;
                }
                if (mapFile.supportsTile(tile)) {
                    mapFile.setDeduplicate(deduplicate);
                    dataSink.level = i + 1;
                    dataSink.levels = n;
                    mapFile.query(tile, dataSink);
                }
                if (mapFile.supportsFullTile(tile)) {
                    isTileFilled = true;
                }
            }
            sink.completed(QueryResult.SUCCESS);
        } catch (Throwable t) {
            log.severe(t.toString());
            sink.completed(QueryResult.FAILED);
        }
    }

    @Override
    public void dispose() {
        for (MapFile mapFile : mapFiles) {
            mapFile.dispose();
        }
    }

    @Override
    public void cancel() {
        for (MapFile mapFile : mapFiles) {
            mapFile.cancel();
        }
    }

    public MapReadResult readNamedItems(Tile tile, boolean deduplicate) {
        MapReadResult mapReadResult = new MapReadResult();
        boolean isTileFilled = false;
        for (MapFile mdb : mapFiles) {
            if (isTileFilled && mdb.getPriority() < 0) {
                break;
            }
            if (mdb.supportsTile(tile)) {
                MapReadResult result = mdb.readNamedItems(tile);
                if (result == null) {
                    continue;
                }
                boolean isWater = mapReadResult.isWater & result.isWater;
                mapReadResult.isWater = isWater;
                mapReadResult.add(result, deduplicate);
            }
            if (mdb.supportsFullTile(tile)) {
                isTileFilled = true;
            }
        }
        return mapReadResult;
    }

    public MapReadResult readNamedItems(Tile upperLeft, Tile lowerRight, boolean deduplicate) {
        MapReadResult mapReadResult = new MapReadResult();
        boolean isTileFilled = false;
        for (MapFile mdb : mapFiles) {
            if (isTileFilled && mdb.getPriority() < 0) {
                break;
            }
            if (mdb.supportsArea(upperLeft.getBoundingBox().extendBoundingBox(lowerRight.getBoundingBox()),
                    upperLeft.zoomLevel)) {
                MapReadResult result = mdb.readNamedItems(upperLeft, lowerRight);
                if (result == null) {
                    continue;
                }
                boolean isWater = mapReadResult.isWater & result.isWater;
                mapReadResult.isWater = isWater;
                mapReadResult.add(result, deduplicate);
            }
            if (mdb.supportsFullArea(upperLeft.getBoundingBox().extendBoundingBox(lowerRight.getBoundingBox()),
                    upperLeft.zoomLevel)) {
                isTileFilled = true;
            }
        }
        return mapReadResult;
    }

    public MapReadResult readMapData(Tile tile, boolean deduplicate) {
        MapReadResult mapReadResult = new MapReadResult();
        boolean isTileFilled = false;
        for (MapFile mdb : mapFiles) {
            if (isTileFilled && mdb.getPriority() < 0) {
                break;
            }
            if (mdb.supportsTile(tile)) {
                MapReadResult result = mdb.readMapData(tile);
                if (result == null) {
                    continue;
                }
                boolean isWater = mapReadResult.isWater & result.isWater;
                mapReadResult.isWater = isWater;
                mapReadResult.add(result, deduplicate);
            }
            if (mdb.supportsFullTile(tile)) {
                isTileFilled = true;
            }
        }
        return mapReadResult;
    }

    public MapReadResult readMapData(Tile upperLeft, Tile lowerRight, boolean deduplicate) {
        MapReadResult mapReadResult = new MapReadResult();
        boolean isTileFilled = false;
        for (MapFile mdb : mapFiles) {
            if (isTileFilled && mdb.getPriority() < 0) {
                break;
            }
            if (mdb.supportsArea(upperLeft.getBoundingBox().extendBoundingBox(lowerRight.getBoundingBox()),
                    upperLeft.zoomLevel)) {
                MapReadResult result = mdb.readMapData(upperLeft, lowerRight);
                if (result == null) {
                    continue;
                }
                boolean isWater = mapReadResult.isWater & result.isWater;
                mapReadResult.isWater = isWater;
                mapReadResult.add(result, deduplicate);
            }
            if (mdb.supportsFullArea(upperLeft.getBoundingBox().extendBoundingBox(lowerRight.getBoundingBox()),
                    upperLeft.zoomLevel)) {
                isTileFilled = true;
            }
        }
        return mapReadResult;
    }

    public MapReadResult readPoiData(Tile tile, boolean deduplicate) {
        MapReadResult mapReadResult = new MapReadResult();
        boolean isTileFilled = false;
        for (MapFile mdb : mapFiles) {
            if (isTileFilled && mdb.getPriority() < 0) {
                break;
            }
            if (mdb.supportsTile(tile)) {
                MapReadResult result = mdb.readPoiData(tile);
                if (result == null) {
                    continue;
                }
                boolean isWater = mapReadResult.isWater & result.isWater;
                mapReadResult.isWater = isWater;
                mapReadResult.add(result, deduplicate);
            }
            if (mdb.supportsFullTile(tile)) {
                isTileFilled = true;
            }
        }
        return mapReadResult;
    }

    public MapReadResult readPoiData(Tile upperLeft, Tile lowerRight, boolean deduplicate) {
        MapReadResult mapReadResult = new MapReadResult();
        boolean isTileFilled = false;
        for (MapFile mdb : mapFiles) {
            if (isTileFilled && mdb.getPriority() < 0) {
                break;
            }
            if (mdb.supportsArea(upperLeft.getBoundingBox().extendBoundingBox(lowerRight.getBoundingBox()),
                    upperLeft.zoomLevel)) {
                MapReadResult result = mdb.readPoiData(upperLeft, lowerRight);
                if (result == null) {
                    continue;
                }
                boolean isWater = mapReadResult.isWater & result.isWater;
                mapReadResult.isWater = isWater;
                mapReadResult.add(result, deduplicate);
            }
            if (mdb.supportsFullArea(upperLeft.getBoundingBox().extendBoundingBox(lowerRight.getBoundingBox()),
                    upperLeft.zoomLevel)) {
                isTileFilled = true;
            }
        }
        return mapReadResult;
    }

    public boolean supportsTile(Tile tile) {
        for (MapFile mdb : mapFiles) {
            if (mdb.supportsTile(tile)) {
                return true;
            }
        }
        return false;
    }

    public boolean supportsFullTile(Tile tile) {
        for (MapFile mdb : mapFiles) {
            if (mdb.supportsFullTile(tile)) {
                return true;
            }
        }
        return false;
    }

    public boolean supportsArea(BoundingBox boundingBox, int zoomLevel) {
        for (MapFile mdb : mapFiles) {
            if (mdb.supportsArea(boundingBox, zoomLevel)) {
                return true;
            }
        }
        return false;
    }

    public boolean supportsFullArea(BoundingBox boundingBox, int zoomLevel) {
        for (MapFile mdb : mapFiles) {
            if (mdb.supportsFullArea(boundingBox, zoomLevel)) {
                return true;
            }
        }
        return false;
    }
}
