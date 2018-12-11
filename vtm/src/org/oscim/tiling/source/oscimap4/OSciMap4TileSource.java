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
package org.oscim.tiling.source.oscimap4;

import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.OverzoomTileDataSource;
import org.oscim.tiling.source.UrlTileDataSource;
import org.oscim.tiling.source.UrlTileSource;

public class OSciMap4TileSource extends UrlTileSource {

    private static final String DEFAULT_URL = "http://opensciencemap.org/tiles/vtm";
    private static final String DEFAULT_PATH = "/{Z}/{X}/{Y}.vtm";

    public static class Builder<T extends Builder<T>> extends UrlTileSource.Builder<T> {

        public Builder() {
            super(DEFAULT_URL, DEFAULT_PATH);
            overZoom(17);
        }

        @Override
        public OSciMap4TileSource build() {
            return new OSciMap4TileSource(this);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Builder<?> builder() {
        return new Builder();
    }

    protected OSciMap4TileSource(Builder<?> builder) {
        super(builder);
    }

    public OSciMap4TileSource() {
        this(builder());
    }

    public OSciMap4TileSource(String urlString) {
        this(builder().url(urlString));
    }

    @Override
    public ITileDataSource getDataSource() {
        return new OverzoomTileDataSource(new UrlTileDataSource(this, new TileDecoder(), getHttpEngine()), mOverZoom);
    }
}
