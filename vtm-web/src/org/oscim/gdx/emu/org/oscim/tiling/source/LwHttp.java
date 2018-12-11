/*
 * Copyright 2013 Hannes Janetzek
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

import com.google.gwt.typedarrays.client.Uint8ArrayNative;
import com.google.gwt.typedarrays.shared.Uint8Array;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.google.gwt.xhr.client.XMLHttpRequest.ResponseType;

import org.oscim.core.Tile;
import org.oscim.layers.tile.MapTile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LwHttp implements HttpEngine {
    //static final Logger log = LoggerFactory.getLogger(LwHttp.class);

    private XMLHttpRequest mHttpRequest;

    private ReadyStateChangeHandler mResponseHandler;

    public LwHttp(UrlTileSource tileSource) {
        mTileSource = tileSource;
    }

    static class Buffer extends InputStream {
        Uint8Array mBuffer;
        int mPos;
        int mEnd;

        public Buffer(Uint8Array buf) {
            mBuffer = buf;
            mPos = 0;
            mEnd = buf.byteLength();
        }

        @Override
        public synchronized int read() throws IOException {
            if (mPos < mEnd)
                return mBuffer.get(mPos++);

            return -1;
        }
    }

    @Override
    public void close() {
        if (mHttpRequest == null)
            return;

        mHttpRequest.abort();
        mHttpRequest = null;
    }

    private UrlTileSource mTileSource;

    public void sendRequest(MapTile tile, final UrlTileDataSource dataSource) {

        String url = mTileSource.getTileUrl(tile);

        mHttpRequest = XMLHttpRequest.create();
        mHttpRequest.open("GET", url);
        mHttpRequest.setResponseType(ResponseType.ArrayBuffer);

        mResponseHandler = new ReadyStateChangeHandler() {

            @Override
            public void onReadyStateChange(XMLHttpRequest xhr) {
                int state = xhr.getReadyState();
                //log.debug(mCurrentUrl + "response " + status + "/" + state);

                if (state == XMLHttpRequest.DONE) {
                    if (xhr.getStatus() == 200) {
                        Uint8Array buf = Uint8ArrayNative.create(xhr.getResponseArrayBuffer());
                        dataSource.process(new Buffer(buf));
                    } else {
                        dataSource.process(null);
                    }
                    mHttpRequest = null;
                }
            }
        };

        mHttpRequest.setOnReadyStateChange(mResponseHandler);
        mHttpRequest.send();
    }

    public static class LwHttpFactory implements HttpEngine.Factory {

        @Override
        public HttpEngine create(UrlTileSource tileSource) {
            return new LwHttp(tileSource);
        }
    }

    @Override
    public InputStream read() throws IOException {
        return null;
    }

    @Override
    public void setCache(OutputStream os) {
    }

    @Override
    public boolean requestCompleted(boolean success) {
        //    mHttpRequest.clearOnReadyStateChange();
        //    mHttpRequest = null;
        return true;
    }

    @Override
    public void sendRequest(Tile tile) throws IOException {
    }
}
