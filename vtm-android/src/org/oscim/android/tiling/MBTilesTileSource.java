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
 *
 * @author Andrea Antonello
 */
public class MBTilesTileSource extends TileSource {
    private final MBTilesTileDataSource ds;

    /**
     * Build a tile source.
     *
     * @param context          the context to use.
     * @param dbPath           the path to the mbtiles database.
     * @param alpha            an optional alpha value [0-255] to make the tile transparent.
     * @param transparentColor an optional color that will be made transparent in the bitmap.
     * @throws Exception
     */
    public MBTilesTileSource(Context context, String dbPath, Integer alpha, Integer transparentColor) throws Exception {
        ds = new MBTilesTileDataSource(context, dbPath, alpha, transparentColor);
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

    public String getSourceName() throws Exception {
        return ds.getName();
    }

    public String getDescription() throws Exception {
        return ds.getDescription();
    }

    public String getAttribution() throws Exception {
        return ds.getAttribution();
    }

    public String getVersion() throws Exception {
        return ds.getVersion();
    }

    public int getMinZoom() throws Exception {
        return ds.getMinZoom();
    }

    public int getMaxZoom() throws Exception {
        return ds.getMaxZoom();
    }

    public double[] getBounds() throws Exception {
        return ds.getBounds();
    }


    @Override
    public ITileDataSource getDataSource() {
        return ds;
    }

    @Override
    public OpenResult open() {
        return OpenResult.SUCCESS;
    }

    @Override
    public void close() {
        ds.dispose();
    }

}