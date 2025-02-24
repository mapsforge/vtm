/*
 * Copyright 2018-2022 devemux86
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
package org.oscim.tiling;

import org.oscim.layers.tile.MapTile;

import java.util.logging.Logger;

public class OverzoomTileDataSource implements ITileDataSource {

    private static final Logger log = Logger.getLogger(OverzoomTileDataSource.class.getName());

    private final ITileDataSource tileDataSource;
    private final int overZoom;

    public OverzoomTileDataSource(ITileDataSource tileDataSource, int overZoom) {
        this.tileDataSource = tileDataSource;
        this.overZoom = overZoom;
    }

    public ITileDataSource getDataSource() {
        return tileDataSource;
    }

    @Override
    public void query(MapTile tile, ITileDataSink sink) {
        try {
            MapTile mapTile = tile;
            ITileDataSink dataSink = sink;
            int diff = tile.zoomLevel - overZoom;
            if (diff > 0) {
                mapTile = new MapTile(tile.node, tile.tileX >> diff, tile.tileY >> diff, overZoom);
                dataSink = new OverzoomDataSink(sink, mapTile, tile);
            }
            tileDataSource.query(mapTile, dataSink);
        } catch (Throwable t) {
            log.severe(t.toString());
        }
    }

    @Override
    public void dispose() {
        tileDataSource.dispose();
    }

    @Override
    public void cancel() {
        tileDataSource.cancel();
    }
}
