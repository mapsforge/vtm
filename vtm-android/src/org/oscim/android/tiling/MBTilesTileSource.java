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