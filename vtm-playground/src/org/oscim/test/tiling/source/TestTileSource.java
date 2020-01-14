/*
 * Copyright 2019 Gustl22
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
package org.oscim.test.tiling.source;

import org.oscim.core.MapElement;
import org.oscim.layers.tile.MapTile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.TileSource;

import java.util.ArrayList;
import java.util.List;

import static org.oscim.tiling.QueryResult.SUCCESS;

public class TestTileSource extends TileSource {

    private TileDataSource mTileDataSource;

    public TestTileSource() {
        super(0, 17);
        mTileDataSource = new TileDataSource();
    }

    public void addMapElement(MapElement e) {
        mTileDataSource.mElems.add(e);
    }

    @Override
    public ITileDataSource getDataSource() {
        return mTileDataSource;
    }

    @Override
    public OpenResult open() {
        return OpenResult.SUCCESS;
    }

    @Override
    public void close() {
    }

    static class TileDataSource implements ITileDataSource {

        final List<MapElement> mElems = new ArrayList<>();

        TileDataSource() {
        }

        @Override
        public void query(MapTile tile, ITileDataSink sink) {
            for (MapElement e : mElems) {
                sink.process(e);
            }

            sink.completed(SUCCESS);
        }

        @Override
        public void dispose() {
        }

        @Override
        public void cancel() {
        }
    }
}
