/*
 * Copyright 2012 Hannes Janetzek
 * Copyright 2017-2022 devemux86
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

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class UrlTileDataSource implements ITileDataSource {

    private static final Logger log = Logger.getLogger(UrlTileDataSource.class.getName());

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
                        sink.completed(QueryResult.SUCCESS);
                        return;
                    }
                } catch (IOException e) {
                    log.fine(tile + " Cache read: " + e);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        }

        QueryResult res = QueryResult.FAILED;

        TileWriter cacheWriter = null;
        try {
            mConn.sendRequest(tile);
            InputStream is = mConn.read();
            if (mUseCache) {
                cacheWriter = cache.writeTile(tile);
                mConn.setCache(cacheWriter.getOutputStream());
            }
            if (mTileDecoder.decode(tile, sink, is))
                res = QueryResult.SUCCESS;
        } catch (SocketException e) {
            log.fine(tile + " Socket Error: " + e);
        } catch (SocketTimeoutException e) {
            log.fine(tile + " Socket Timeout");
            res = QueryResult.DELAYED;
        } catch (UnknownHostException e) {
            log.fine(tile + " Unknown host: " + e);
        } catch (IOException e) {
            log.fine(tile + " Network Error: " + e);
        } catch (Exception e) {
            log.fine(tile + " Error: " + e);
        } catch (Throwable t) {
            log.severe(t.toString());
        } finally {
            boolean ok = (res == QueryResult.SUCCESS);

            if (!mConn.requestCompleted(ok) && ok)
                res = QueryResult.FAILED;

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
