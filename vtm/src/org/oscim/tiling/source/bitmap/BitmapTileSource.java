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

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.Tile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.source.ITileDecoder;
import org.oscim.tiling.source.UrlTileDataSource;
import org.oscim.tiling.source.UrlTileSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class BitmapTileSource extends UrlTileSource {
    private static final Logger log = Logger.getLogger(BitmapTileSource.class.getName());

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
        this(url, "/{Z}/{X}/{Y}.png", zoomMin, zoomMax);
    }

    public BitmapTileSource(String url, int zoomMin, int zoomMax, String extension) {
        this(url, "/{Z}/{X}/{Y}" + extension, zoomMin, zoomMax);
    }

    public BitmapTileSource(String url, String tilePath, int zoomMin, int zoomMax) {
        super(builder()
                .url(url)
                .tilePath(tilePath)
                .zoomMin(zoomMin)
                .zoomMax(zoomMax));
    }

    @Override
    public ITileDataSource getDataSource() {
        return new UrlTileDataSource(this, new TileDecoder(), getHttpEngine());
    }

    public static class TileDecoder implements ITileDecoder {

        @Override
        public boolean decode(Tile tile, ITileDataSink sink, InputStream is)
                throws IOException {

            Bitmap bitmap = CanvasAdapter.decodeBitmap(is);
            if (!bitmap.isValid()) {
                log.fine(tile + " invalid bitmap");
                return false;
            }
            sink.setTileImage(bitmap);

            return true;
        }
    }
}
