/*
 * Copyright 2014 Hannes Janetzek
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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.jsonp.client.JsonpRequest;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.MapTile.State;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.source.geojson.GeoJsonTileDecoder;
import org.oscim.tiling.source.geojson.GeoJsonTileSource;

import java.io.InputStream;
import java.util.logging.Logger;

import static org.oscim.tiling.QueryResult.FAILED;
import static org.oscim.tiling.QueryResult.SUCCESS;

public class JsonTileDataSource implements ITileDataSource {
    private static final Logger log = Logger.getLogger(JsonTileDataSource.class.getName());

    protected final GeoJsonTileDecoder mTileDecoder;
    protected final UrlTileSource mTileSource;

    public JsonTileDataSource(GeoJsonTileSource tileSource) {
        mTileSource = tileSource;
        mTileDecoder = new GeoJsonTileDecoder(tileSource);
    }

    UrlTileSource getTileSource() {
        return mTileSource;
    }

    private ITileDataSink mSink;
    private MapTile mTile;

    @Override
    public void query(MapTile tile, ITileDataSink sink) {
        mTile = tile;
        mSink = sink;

        try {
            doGet(mTileSource.getTileUrl(tile));
        } catch (Exception e) {
            e.printStackTrace();
            sink.completed(FAILED);
        }
    }

    public void process(InputStream is) {
    }

    boolean mFinished;

    @Override
    public void dispose() {
        mFinished = true;
    }

    @Override
    public void cancel() {
        mFinished = true;
    }

    JsonpRequest<JavaScriptObject> mRequestHandle;

    void doGet(final String url) {
        JsonpRequestBuilder builder = new JsonpRequestBuilder();
        //builder.setCallbackParam("json_callback");

        mRequestHandle = builder.requestObject(url, new AsyncCallback<JavaScriptObject>() {
            @Override
            public void onFailure(Throwable caught) {

                mSink.completed(FAILED);
                log.fine("fail! " + mRequestHandle + " " + caught.toString());
                //mRequestHandle.cancel();
            }

            @Override
            public void onSuccess(JavaScriptObject jso) {
                if (mTile.state(State.NONE)) {
                    log.fine("tile cleared " + url);
                    mSink.completed(FAILED);
                    return;
                }

                if (jso == null) {
                    log.fine("Couldn't retrieve JSON for " + url);
                    mSink.completed(FAILED);
                    return;
                }

                try {
                    if (mTileDecoder.decode(mTile, mSink, jso)) {
                        mSink.completed(SUCCESS);
                        return;
                    }
                } catch (Exception e) {
                    log.fine("Couldn't retrieve JSON for " + url + " " + e);
                    // FIXME need to check where it might be thrown
                    mSink.completed(FAILED);
                }
            }
        });
    }
}
