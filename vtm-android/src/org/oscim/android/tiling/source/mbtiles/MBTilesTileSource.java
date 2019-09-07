/*
 * Copyright 2019 Andrea Antonello
 * Copyright 2019 devemux86
 * Copyright 2019 Kostas Tzounopoulos
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
package org.oscim.android.tiling.source.mbtiles;

import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.map.Viewport;
import org.oscim.tiling.TileSource;

/**
 * A tile source for MBTiles Raster and Vector databases.
 * Supports the OpenMapTiles MVT pbf-gzip MBTiles format
 */
public class MBTilesTileSource extends TileSource {
    private final MBTilesTileDataSource mTileDataSource;

    /**
     * Create a MBTiles tile source (Raster/Vector)
     *
     * @param path the path to the MBTiles database.
     */
    public MBTilesTileSource(String path) {
        this(path, null, null, null);
    }

    /**
     * Create an MBTiles tile source with additional parameters
     *
     * @param path             the path to the MBTiles database. (Raster & Vector)
     * @param locale           the database locale to use, e.g. "en", "de" (Vector only)
     * @param alpha            an optional alpha value [0-255] to make the tiles transparent. (Raster only)
     * @param transparentColor an optional color that will be made transparent in the bitmap. (Raster only)
     */
    public MBTilesTileSource(String path, String locale, Integer alpha, Integer transparentColor) {
        mTileDataSource = new MBTilesTileDataSource(path, locale, alpha, transparentColor);
    }

    /**
     * Create a MBTiles tile source (Vector only)
     *
     * @param path the path to the MBTiles database.
     */
    public MBTilesTileSource(String path, String locale) {
        this(path, locale, null, null);
    }

    @Override
    public MBTilesTileDataSource getDataSource() {
        return mTileDataSource;
    }

    @Override
    public OpenResult open() {
        return OpenResult.SUCCESS;
    }

    @Override
    public void close() {
        getDataSource().dispose();
    }

    public String getMetadataAttribution() {
        return mTileDataSource.getAttribution();
    }

    public BoundingBox getMetadataBounds() {
        return mTileDataSource.getBounds();
    }

    public MapPosition getMetadataCenter() {
        return mTileDataSource.getCenter();
    }

    public String getMetadataDescription() {
        return mTileDataSource.getDescription();
    }

    public String getMetadataFormat() {
        return mTileDataSource.getFormat();
    }

    public Integer getMetadataPixelScale() {
        return mTileDataSource.getPixelScale();
    }

    public int getMetadataMaxZoom() {
        return mTileDataSource.getMaxZoom();
    }

    public int getMetadataMinZoom() {
        return mTileDataSource.getMinZoom();
    }

    public String getMetadataName() {
        return mTileDataSource.getName();
    }

    public String getMetadataVersion() {
        return mTileDataSource.getVersion();
    }

    public String getMetadataId() {
        return mTileDataSource.getId();
    }

    public Long getMetadataMTime() {
        return mTileDataSource.getMTime();
    }

    public String getMetadataJson() {
        return mTileDataSource.getJson();
    }
}
