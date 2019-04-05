/*
 * Copyright 2019 Andrea Antonello
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
package org.oscim.android.tiling;

import android.content.Context;

import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.TileSource;

/**
 * A tile source for MBTiles raster databases.
 */
public class MBTilesBitmapTileSource extends TileSource {
    private final MBTilesBitmapTileDataSource ds;

    /**
     * Build a tile source.
     *
     * @param dbPath           the path to the mbtiles database.
     * @param alpha            an optional alpha value [0-255] to make the tile transparent.
     * @param transparentColor an optional color that will be made transparent in the bitmap.
     * @throws Exception
     */
    public MBTilesBitmapTileSource(String dbPath, Integer alpha, Integer transparentColor) throws Exception {
        ds = new MBTilesBitmapTileDataSource(dbPath, alpha, transparentColor);
    }

    @Override
    public void close() {
        ds.dispose();
    }


    public String getAttribution() throws Exception {
        return ds.getAttribution();
    }

    public double[] getBounds() throws Exception {
        return ds.getBounds();
    }

    @Override
    public ITileDataSource getDataSource() {
        return ds;
    }

    public String getDescription() throws Exception {
        return ds.getDescription();
    }

    /**
     * Get the image format of the db.
     *
     * @return the image format (jpg, png).
     * @throws Exception
     */
    public String getImageFormat() throws Exception {
        return ds.getImageFormat();
    }

    public int getMaxZoom() throws Exception {
        return ds.getMaxZoom();
    }

    public int getMinZoom() throws Exception {
        return ds.getMinZoom();
    }


    public String getSourceName() throws Exception {
        return ds.getName();
    }


    @Override
    public OpenResult open() {
        return OpenResult.SUCCESS;
    }


    public String getVersion() throws Exception {
        return ds.getVersion();
    }

}