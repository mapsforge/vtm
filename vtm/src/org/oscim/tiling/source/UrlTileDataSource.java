/*
 * Copyright 2012 Hannes Janetzek
 * Copyright 2017 devemux86
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
package org.oscim.tiling.source;

import org.oscim.layers.tile.MapTile;
import org.oscim.tiling.ITileCache;
import org.oscim.tiling.ITileCache.TileReader;
import org.oscim.tiling.ITileCache.TileWriter;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.QueryResult;
import org.oscim.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import static org.oscim.tiling.QueryResult.DELAYED;
import static org.oscim.tiling.QueryResult.FAILED;
import static org.oscim.tiling.QueryResult.SUCCESS;

public class UrlTileDataSource implements ITileDataSource {
    static final Logger log = LoggerFactory.getLogger(UrlTileDataSource.class);

    protected final HttpEngine mConn;
    protected final ITileDecoder mTileDecoder;
    protected final UrlTileSource mTileSource;
    protected final boolean mUseCache;

    public UrlTileDataSource(UrlTileSource tileSource, ITileDecoder tileDecoder, HttpEngine conn) {
        mTileDecoder = tileDecoder;
        mTileSource = tileSource;
        mUseCache = (tileSource.tileCache != null);
        mConn = conn;
    }

    @Override
    public void query(MapTile tile, ITileDataSink sink) {
        ITileCache cache = mTileSource.tileCache;

        if (mUseCache) {
            TileReader c = cache.getTile(tile);
            if (c != null) {
                InputStream is = c.getInputStream();
                try {
                    if (mTileDecoder.decode(tile, sink, is)) {
                        sink.completed(SUCCESS);
                        return;
                    }
                } catch (IOException e) {
                    log.debug("{} Cache read: {}", tile, e);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        }

        QueryResult res = FAILED;

        TileWriter cacheWriter = null;
        try {
            mConn.sendRequest(tile);
            InputStream is = mConn.read();
            if (mUseCache) {
                cacheWriter = cache.writeTile(tile);
                mConn.setCache(cacheWriter.getOutputStream());
            }
            if (mTileDecoder.decode(tile, sink, is))
                res = SUCCESS;
        } catch (SocketException e) {
            log.debug("{} Socket Error: {}", tile, e.getMessage());
        } catch (SocketTimeoutException e) {
            log.debug("{} Socket Timeout", tile);
            res = DELAYED;
        } catch (UnknownHostException e) {
            log.debug("{} Unknown host: {}", tile, e.getMessage());
        } catch (IOException e) {
            log.debug("{} Network Error: {}", tile, e.getMessage());
        } catch (Exception e) {
            log.debug("{} Error: {}", tile, e.getMessage());
        } finally {
            boolean ok = (res == SUCCESS);

            if (!mConn.requestCompleted(ok) && ok)
                res = FAILED;

            if (cacheWriter != null)
                cacheWriter.complete(ok);

            sink.completed(res);
        }
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
