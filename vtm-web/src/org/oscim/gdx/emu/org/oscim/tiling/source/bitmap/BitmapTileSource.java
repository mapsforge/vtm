/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2018 devemux86
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
package org.oscim.tiling.source.bitmap;

import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import org.oscim.gdx.client.GwtBitmap;
import org.oscim.layers.tile.LoadDelayTask;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.TileLoader;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.QueryResult;
import org.oscim.tiling.source.UrlTileSource;

public class BitmapTileSource extends UrlTileSource {

    public static class Builder<T extends Builder<T>> extends UrlTileSource.Builder<T> {

        public Builder() {
            super(null, "/{Z}/{X}/{Y}.png");
        }

        @Override
        public BitmapTileSource build() {
            return new BitmapTileSource(this);
        }
    }

    protected BitmapTileSource(Builder<?> builder) {
        super(builder);
    }

    @SuppressWarnings("rawtypes")
    public static Builder<?> builder() {
        return new Builder();
    }

    /**
     * Create BitmapTileSource for 'url'
     * <p/>
     * By default path will be formatted as: url/z/x/y.png
     * Use e.g. setExtension(".jpg") to overide ending or
     * implement getUrlString() for custom formatting.
     */

    public BitmapTileSource(String url, int zoomMin, int zoomMax) {
        super(url, "/{Z}/{X}/{Y}.png", zoomMin, zoomMax);
    }

    public BitmapTileSource(String url, int zoomMin, int zoomMax, String extension) {
        super(url, "/{Z}/{X}/{Y}" + extension, zoomMin, zoomMax);
    }

    public BitmapTileSource(String url, String tilePath, int zoomMin, int zoomMax) {
        super(url, tilePath, zoomMin, zoomMax);
    }

    @Override
    public ITileDataSource getDataSource() {
        return new BitmapTileDataSource(this);
    }

    public class BitmapTileDataSource implements ITileDataSource {

        protected final UrlTileSource mTileSource;

        public BitmapTileDataSource(BitmapTileSource bitmapTileSource) {
            mTileSource = bitmapTileSource;
        }

        @Override
        public void query(final MapTile tile, final ITileDataSink sink) {

            String url = mTileSource.getTileUrl(tile);

            SafeUri uri = UriUtils.fromTrustedString(url);

            final Image img = new Image();
            img.setVisible(false);

            /* As if researching CORS issues doesnt result in
             * enough headache...
             *
             * Here are some more special Chrome/Webkit quirks:
             * MUST SET CORS BEFORE URL! */
            img.getElement().setAttribute("crossorigin", "anonymous");
            img.setUrl(uri);

            RootPanel.get().add(img);

            img.addLoadHandler(new LoadHandler() {
                @Override
                public void onLoad(LoadEvent event) {
                    TileLoader.postLoadDelay(new LoadDelayTask<Image>(tile, sink, img) {

                        @Override
                        public void continueLoading() {
                            if (!tile.state(MapTile.State.LOADING)) {
                                sink.completed(QueryResult.FAILED);
                                RootPanel.get().remove(data);
                            } else {
                                sink.setTileImage(new GwtBitmap(data));
                                sink.completed(QueryResult.SUCCESS);
                            }
                        }
                    });
                }
            });

            img.addErrorHandler(new ErrorHandler() {

                @Override
                public void onError(ErrorEvent event) {
                    sink.completed(QueryResult.FAILED);
                    RootPanel.get().remove(img);
                }
            });
        }

        @Override
        public void dispose() {
        }

        @Override
        public void cancel() {
        }

    }
}
