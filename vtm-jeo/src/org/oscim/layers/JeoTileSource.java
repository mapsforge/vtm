/*
 * Copyright 2016 devemux86
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
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
package org.oscim.layers;

import io.jeo.tile.Tile;
import io.jeo.tile.TileDataset;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.layers.tile.MapTile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.TileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.oscim.tiling.QueryResult.*;

public class JeoTileSource extends TileSource {
    static final Logger log = LoggerFactory.getLogger(JeoTileSource.class);

    final TileDataset mTileDataset;

    public JeoTileSource(TileDataset tileDataset) {
        log.debug("load tileset {}", tileDataset.name());
        mTileDataset = tileDataset;
        //mTileDataset.pyramid().
        mZoomMax = 1;
        mZoomMin = 0;
    }

    @Override
    public ITileDataSource getDataSource() {
        return new ITileDataSource() {

            @Override
            public void query(MapTile tile, ITileDataSink sink) {
                log.debug("query {}", tile);
                try {
                    Tile t = mTileDataset.read(tile.zoomLevel, tile.tileX,
                            // flip Y axis
                            (1 << tile.zoomLevel) - 1 - tile.tileY);
                    if (t == null) {
                        log.debug("not found {}", tile);
                        sink.completed(TILE_NOT_FOUND);
                        return;
                    }
                    Bitmap b = CanvasAdapter.decodeBitmap(new ByteArrayInputStream(t.data()));
                    sink.setTileImage(b);
                    log.debug("success {}", tile);
                    sink.completed(SUCCESS);
                    return;

                } catch (IOException e) {
                    e.printStackTrace();
                }
                log.debug("fail {}", tile);
                sink.completed(FAILED);
            }

            @Override
            public void dispose() {

            }

            @Override
            public void cancel() {

            }

        };
    }

    int mRefs;

    @Override
    public OpenResult open() {
        mRefs++;
        return OpenResult.SUCCESS;
    }

    @Override
    public void close() {
        if (--mRefs == 0)
            mTileDataset.close();
    }

}
