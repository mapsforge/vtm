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

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.MercatorProjection;
import org.oscim.layers.tile.MapTile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * A tile data source for MBTiles Raster databases.
 */
public class MBTilesBitmapTileDataSource extends MBTilesTileDataSource {
    private final static List<String> SUPPORTED_FORMATS = Arrays.asList("png", "jpg", "jpeg");
    protected static final Logger log = LoggerFactory.getLogger(MBTilesBitmapTileDataSource.class);

    private final Integer mAlpha;
    private final Integer mTransparentColor;

    /**
     * Create a MBTiles tile data source for Raster databases
     *
     * @param databasePath     the MBTiles database path
     * @param alpha            an optional alpha value [0-255] to make the tiles transparent.
     * @param transparentColor an optional color that will be made transparent in the bitmap.
     */
    public MBTilesBitmapTileDataSource(String databasePath, Integer alpha, Integer transparentColor) {
        super(databasePath);

        try {
            assertDatabaseFormat();
        } catch (UnsupportedMBTilesDatabaseException e) {
            log.error("Invalid MBTiles database", e);
        }

        mAlpha = alpha;
        mTransparentColor = transparentColor;
    }

    @Override
    public List<String> getSupportedFormats() {
        return SUPPORTED_FORMATS;
    }

    private static android.graphics.Bitmap processAlpha(android.graphics.Bitmap bitmap, int alpha) {
        android.graphics.Bitmap newBitmap = android.graphics.Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), android.graphics.Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        paint.setAlpha(alpha);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return newBitmap;
    }

    private static android.graphics.Bitmap processTransparentColor(android.graphics.Bitmap bitmap, int colorToRemove) {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] == colorToRemove)
                pixels[i] = Color.alpha(Color.TRANSPARENT);
        }
        android.graphics.Bitmap newBitmap = android.graphics.Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), android.graphics.Bitmap.Config.ARGB_8888);
        newBitmap.setPixels(pixels, 0, newBitmap.getWidth(), 0, 0, newBitmap.getWidth(), newBitmap.getHeight());
        return newBitmap;
    }

    @Override
    public void query(MapTile tile, ITileDataSink sink) {
        QueryResult res = QueryResult.FAILED;
        try {
            byte[] bytes = readTile(tile.tileX, tile.tileY, tile.zoomLevel);

            if (mTransparentColor != null || mAlpha != null) {
                android.graphics.Bitmap androidBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (mTransparentColor != null)
                    androidBitmap = processTransparentColor(androidBitmap, mTransparentColor);
                if (mAlpha != null)
                    androidBitmap = processAlpha(androidBitmap, mAlpha);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                androidBitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, bos);
                bytes = bos.toByteArray();
            }

            Bitmap bitmap = CanvasAdapter.decodeBitmap(new ByteArrayInputStream(bytes));
            sink.setTileImage(bitmap);
            res = QueryResult.SUCCESS;
        } catch (Exception e) {
            log.debug("{} invalid bitmap", tile);
        } finally {
            sink.completed(res);
        }
    }

    @Override
    public void cancel() {
        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
        }
    }

    @Override
    public void dispose() {
        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
        }
    }

    /**
     * Read a Tile's image bytes from the MBTiles database.
     *
     * @param tileX     the x tile index.
     * @param tileY     the y tile index (OSM notation).
     * @param zoomLevel the zoom level.
     * @return the tile image bytes.
     */
    private byte[] readTile(int tileX, int tileY, byte zoomLevel) {
        long tmsTileY = MercatorProjection.tileYToTMS(tileY, zoomLevel);

        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(
                    String.format(MBTilesTileDataSource.SELECT_TILES_FORMAT, MBTilesTileDataSource.WHERE_FORMAT),
                    new String[]{String.valueOf(zoomLevel), String.valueOf(tileX), String.valueOf(tmsTileY)}
            );

            if (cursor.moveToFirst()) {
                return cursor.getBlob(cursor.getColumnIndexOrThrow("tile_data"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }
}
