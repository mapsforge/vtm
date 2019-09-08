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
import org.oscim.core.MapPosition;
import org.oscim.tiling.TileSource;

/**
 * A tile source for MBTiles databases.
 */
abstract public class MBTilesTileSource extends TileSource {
    protected final MBTilesTileDataSource mTileDataSource;

    public MBTilesTileSource(MBTilesTileDataSource tileDataSource) {
        mTileDataSource = tileDataSource;
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
