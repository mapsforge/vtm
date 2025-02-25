/*
 * Copyright 2012 Hannes Janetzek
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
package org.oscim.tiling.source;

import org.oscim.layers.tile.LoadDelayTask;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.TileLoader;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import static org.oscim.tiling.QueryResult.FAILED;
import static org.oscim.tiling.QueryResult.SUCCESS;

public class UrlTileDataSource implements ITileDataSource {
    private static final Logger log = Logger.getLogger(UrlTileDataSource.class.getName());

    protected final LwHttp mConn;
    protected final ITileDecoder mTileDecoder;
    protected final UrlTileSource mTileSource;

    private ITileDataSink mSink;
    private MapTile mTile;

    public UrlTileDataSource(UrlTileSource tileSource, ITileDecoder tileDecoder, HttpEngine conn) {
        mTileSource = tileSource;
        mTileDecoder = tileDecoder;
        mConn = (LwHttp) conn;
    }

    @Override
    public void query(MapTile tile, ITileDataSink sink) {
        mTile = tile;
        mSink = sink;
        mConn.sendRequest(tile, this);
    }

    public void process(final InputStream is) {
        if (is == null) {
            log.fine(mTile + " no inputstream");
            mSink.completed(FAILED);
            mTile = null;
            mSink = null;
            return;
        }

        TileLoader.postLoadDelay(new LoadDelayTask<InputStream>(mTile, mSink, is) {
            @Override
            public void continueLoading() {
                boolean win = false;
                if (tile.state(MapTile.State.LOADING)) {
                    try {
                        win = mTileDecoder.decode(tile, sink, data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (win) {
                    sink.completed(SUCCESS);
                } else {
                    sink.completed(FAILED);
                    log.fine(tile + " decode failed");
                }
            }
        });
        mTile = null;
        mSink = null;
    }

    @Override
    public void dispose() {
        mConn.close();
    }

    @Override
    public void cancel() {
        mConn.close();
    }

}
