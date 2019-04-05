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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.oscim.android.canvas.AndroidGraphics;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.layers.tile.MapTile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import static org.oscim.tiling.QueryResult.FAILED;

/**
 * A tile data source for MBTiles raster databases.
 *
 * @author Andrea Antonello
 */
public class MBTilesBitmapTileDataSource implements ITileDataSource {
    static final Logger log = LoggerFactory.getLogger(MBTilesBitmapTileDataSource.class);

    private final Integer mTransparentColor;
    private Integer alpha;
    private final SQLiteDatabase mDb;

    public final static String TABLE_TILES = "tiles";
    public final static String COL_TILES_ZOOM_LEVEL = "zoom_level";
    public final static String COL_TILES_TILE_COLUMN = "tile_column";
    public final static String COL_TILES_TILE_ROW = "tile_row";
    public final static String COL_TILES_TILE_DATA = "tile_data";
    public final static String SELECTQUERY = "SELECT " + COL_TILES_TILE_DATA + " from " + TABLE_TILES + " where "
            + COL_TILES_ZOOM_LEVEL + "=? AND " + COL_TILES_TILE_COLUMN + "=? AND " + COL_TILES_TILE_ROW + "=?";

    // TABLE METADATA (name TEXT, value TEXT);
    public final static String TABLE_METADATA = "metadata";
    public final static String COL_METADATA_NAME = "name";
    public final static String COL_METADATA_VALUE = "value";

    private final static String SELECT_METADATA = //
            "select " + COL_METADATA_NAME + "," + COL_METADATA_VALUE + " from " + TABLE_METADATA;

    private HashMap<String, String> metadataMap = null;

    /**
     * Build a tile data source.
     *
     * @param dbPath           the path to the mbtiles database.
     * @param alpha            an optional alpha value [0-255] to make the tile transparent.
     * @param transparentColor an optional color that will be made transparent in the bitmap.
     * @throws Exception
     */
    MBTilesBitmapTileDataSource(String dbPath, Integer alpha, Integer transparentColor) throws Exception {

        this.mDb = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);

        this.alpha = alpha;
        this.mTransparentColor = transparentColor;
    }

    private void checkMetadata() throws Exception {
        if (metadataMap == null) {
            metadataMap = new HashMap<>();
            Cursor cursor = null;
            try {
                cursor = mDb.rawQuery(SELECT_METADATA, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String key = cursor.getString(0);
                    String value = cursor.getString(1);
                    metadataMap.put(key, value);
                    cursor.moveToNext();
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
    }

    @Override
    public void cancel() {
        try {
            mDb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        try {
            mDb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAttribution() throws Exception {
        checkMetadata();
        return metadataMap.get("attribution");
    }

    /**
     * @return bounds as [w,e,s,n]
     * @throws Exception
     */
    public double[] getBounds() throws Exception {
        checkMetadata();
        String boundsWSEN = metadataMap.get("bounds");
        if (boundsWSEN != null) {
            String[] split = boundsWSEN.split(",");
            double w = Double.parseDouble(split[0]);
            double s = Double.parseDouble(split[1]);
            double e = Double.parseDouble(split[2]);
            double n = Double.parseDouble(split[3]);
            return new double[]{w, e, s, n};
        } else {
            return null;
        }
    }

    public String getDescription() throws Exception {
        checkMetadata();
        return metadataMap.get("description");
    }

    /**
     * Get the image format of the db.
     *
     * @return the image format (jpg, png).
     * @throws Exception
     */
    public String getImageFormat() throws Exception {
        checkMetadata();
        return metadataMap.get("format");
    }

    public int getMinZoom() throws Exception {
        checkMetadata();
        String minZoomStr = metadataMap.get("minzoom");
        if (minZoomStr != null) {
            return Integer.parseInt(minZoomStr);
        }
        return -1;
    }

    public int getMaxZoom() throws Exception {
        checkMetadata();
        String maxZoomStr = metadataMap.get("maxzoom");
        if (maxZoomStr != null) {
            return Integer.parseInt(maxZoomStr);
        }
        return -1;
    }

    public String getName() throws Exception {
        checkMetadata();
        return metadataMap.get("name");
    }

    /**
     * Get a Tile's image bytes from the database.
     *
     * @param tx    the x tile index.
     * @param tyOsm the y tile index, the osm way.
     * @param zoom  the zoom level.
     * @return the tile image bytes.
     * @throws Exception
     */
    public byte[] getTile(int tx, int tyOsm, int zoom) throws Exception {
        int ty = tyOsm;
        int[] tmsTileXY = osmTile2TmsTile(tx, tyOsm, zoom);
        ty = tmsTileXY[1];


        Cursor cursor = null;
        try {
            cursor = mDb.rawQuery(SELECTQUERY, new String[]{String.valueOf(zoom), String.valueOf(tx), String.valueOf(ty)});
            if (cursor.moveToFirst()) {
                return cursor.getBlob(0);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public String getVersion() throws Exception {
        checkMetadata();
        return metadataMap.get("version");
    }

    private static android.graphics.Bitmap makeBitmapTransparent(android.graphics.Bitmap originalBitmap, int alpha) {
        android.graphics.Bitmap newBitmap = android.graphics.Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), android.graphics.Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint alphaPaint = new Paint();
        alphaPaint.setAlpha(alpha);
        canvas.drawBitmap(originalBitmap, 0, 0, alphaPaint);
        return newBitmap;
    }

    private static android.graphics.Bitmap makeTransparent(android.graphics.Bitmap bit, int colorToRemove) {
        int width = bit.getWidth();
        int height = bit.getHeight();
        android.graphics.Bitmap myBitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);
        int[] allpixels = new int[myBitmap.getHeight() * myBitmap.getWidth()];
        bit.getPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
        myBitmap.setPixels(allpixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < myBitmap.getHeight() * myBitmap.getWidth(); i++) {
            if (allpixels[i] == colorToRemove)
                allpixels[i] = Color.alpha(Color.TRANSPARENT);
        }

        myBitmap.setPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
        return myBitmap;
    }

    private static int[] osmTile2TmsTile(int tx, int ty, int zoom) {
        return new int[]{tx, (int) ((Math.pow(2, zoom) - 1) - ty)};
    }

    @Override
    public void query(MapTile tile, ITileDataSink sink) {
        QueryResult res = FAILED;

        try {
            byte[] imageBytes = getTile(tile.tileX, tile.tileY, tile.zoomLevel);
            if (mTransparentColor != null || alpha != null) {
                android.graphics.Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                if (mTransparentColor != null) {
                    bmp = makeTransparent(bmp, mTransparentColor);
                }
                if (alpha != null) {
                    bmp = makeBitmapTransparent(bmp, alpha);
                }
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, bos);
                imageBytes = bos.toByteArray();
            }

            Bitmap bitmap = AndroidGraphics.decodeBitmap(new ByteArrayInputStream(imageBytes));

            sink.setTileImage(bitmap);
            res = QueryResult.SUCCESS;
        } catch (Exception e) {
            log.debug("{} Error: {}", tile, e.getMessage());
        } finally {
            sink.completed(res);
        }
    }


}