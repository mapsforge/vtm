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

import org.oscim.core.BoundingBox;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.TileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiMapFileTileSource extends TileSource implements IMapFileTileSource {

    private static final Logger log = LoggerFactory.getLogger(MultiMapFileTileSource.class);

    private final List<MapFileTileSource> mapFileTileSources = new ArrayList<>();
    private final Map<MapFileTileSource, int[]> zoomsByTileSource = new HashMap<>();

    public MultiMapFileTileSource() {
        this(0, 17);
    }

    public MultiMapFileTileSource(int zoomMin, int zoomMax) {
        super(zoomMin, zoomMax);
    }

    public boolean add(MapFileTileSource mapFileTileSource) {
        if (mapFileTileSources.contains(mapFileTileSource)) {
            throw new IllegalArgumentException("Duplicate map file tile source");
        }
        return mapFileTileSources.add(mapFileTileSource);
    }

    public boolean add(MapFileTileSource mapFileTileSource, int zoomMin, int zoomMax) {
        boolean result = add(mapFileTileSource);
        if (result)
            zoomsByTileSource.put(mapFileTileSource, new int[]{zoomMin, zoomMax});
        return result;
    }

    public BoundingBox getBoundingBox() {
        BoundingBox boundingBox = null;
        for (MapFileTileSource mapFileTileSource : mapFileTileSources) {
            boundingBox = (boundingBox == null) ? mapFileTileSource.getMapInfo().boundingBox : boundingBox.extendBoundingBox(mapFileTileSource.getMapInfo().boundingBox);
        }
        return boundingBox;
    }

    Map<MapFileTileSource, int[]> getZoomsByTileSource() {
        return zoomsByTileSource;
    }

    @Override
    public ITileDataSource getDataSource() {
        MultiMapDatabase multiMapDatabase = new MultiMapDatabase(this);
        for (MapFileTileSource mapFileTileSource : mapFileTileSources) {
            try {
                multiMapDatabase.add(new MapDatabase(mapFileTileSource));
            } catch (IOException e) {
                log.debug(e.getMessage());
            }
        }
        return multiMapDatabase;
    }

    @Override
    public OpenResult open() {
        OpenResult openResult = OpenResult.SUCCESS;
        for (MapFileTileSource mapFileTileSource : mapFileTileSources) {
            OpenResult result = mapFileTileSource.open();
            if (result != OpenResult.SUCCESS)
                openResult = result;
        }
        return openResult;
    }

    @Override
    public void close() {
        for (MapFileTileSource mapFileTileSource : mapFileTileSources) {
            mapFileTileSource.close();
        }
    }

    @Override
    public void setCallback(MapFileTileSource.Callback callback) {
        for (MapFileTileSource mapFileTileSource : mapFileTileSources) {
            mapFileTileSource.setCallback(callback);
        }
    }

    @Override
    public void setPreferredLanguage(String preferredLanguage) {
        for (MapFileTileSource mapFileTileSource : mapFileTileSources) {
            mapFileTileSource.setPreferredLanguage(preferredLanguage);
        }
    }
}
