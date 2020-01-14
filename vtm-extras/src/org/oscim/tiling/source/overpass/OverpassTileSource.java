/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2018 devemux86
 * Copyright 2019 Gustl22
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
package org.oscim.tiling.source.overpass;

import org.oscim.core.BoundingBox;
import org.oscim.core.Tile;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.OverzoomTileDataSource;
import org.oscim.tiling.source.UrlTileDataSource;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.utils.overpass.OverpassAPIReader;

public class OverpassTileSource extends UrlTileSource {

    private static final String DEFAULT_URL = "https://www.overpass-api.de/api/interpreter?data=[out:json];";
    private static final String DEFAULT_PATH = "(node{{bbox}};way{{bbox}};>;);out%20body;";

    public static class Builder<T extends Builder<T>> extends UrlTileSource.Builder<T> {

        public Builder() {
            super(DEFAULT_URL, DEFAULT_PATH);
            zoomMax(17);
        }

        @Override
        public OverpassTileSource build() {
            return new OverpassTileSource(this);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Builder<?> builder() {
        return new Builder();
    }

    public OverpassTileSource(Builder<?> builder) {
        super(builder);

        setUrlFormatter(new TileUrlFormatter() {
            @Override
            public String formatTilePath(UrlTileSource tileSource, Tile tile) {
                BoundingBox bb = tile.getBoundingBox();

                String query = OverpassAPIReader.query(
                        bb.getMinLongitude(),
                        bb.getMaxLongitude(),
                        bb.getMaxLatitude(),
                        bb.getMinLatitude(),
                        DEFAULT_PATH);

                /*String encoded;
                try {
                    query = URLEncoder.encode(query, "utf-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                    return null;
                }*/
                return query;
            }
        });
    }

    @Override
    public ITileDataSource getDataSource() {
        return new OverzoomTileDataSource(new UrlTileDataSource(this, new TileDecoder(), getHttpEngine()), mOverZoom);
    }
}
