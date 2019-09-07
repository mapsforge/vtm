/*
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
import android.database.sqlite.SQLiteDatabase;

import org.oscim.core.BoundingBox;
import org.oscim.core.MercatorProjection;
import org.oscim.layers.tile.MapTile;
import org.oscim.map.Viewport;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.OverzoomDataSink;
import org.oscim.tiling.QueryResult;
import org.oscim.tiling.source.mvt.MvtTileDecoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class MBTilesMvtTileDataSourceWorker implements ITileDataSource {
    public final static List<String> SUPPORTED_FORMATS = Arrays.asList("pbf");

    private static final String SELECT_TILES_FORMAT =
            "SELECT zoom_level, tile_column, tile_row, tile_data " +
            "FROM tiles " +
            "WHERE %s " +
            "ORDER BY zoom_level DESC " +
            "LIMIT 1";

    private static final String WHERE_FORMAT = "zoom_level=%d AND tile_column=%d AND tile_row=%d";

    private final String mLocale;
    private final SQLiteDatabase mDatabase;

    private final ThreadLocal<MvtTileDecoder> mThreadLocalMvtTileDecoders = new ThreadLocal<MvtTileDecoder>() {
        @Override
        protected MvtTileDecoder initialValue() {
            return new MvtTileDecoder(mLocale);
        }
    };

    public MBTilesMvtTileDataSourceWorker(SQLiteDatabase database, String locale) {
        mLocale = locale != null ? locale : "en";
        mDatabase = database;
    }

    @Override
    public void query(MapTile requestTile, ITileDataSink requestDataSink) {
        Cursor cursor = null;
        ITileDataSink responseDataSink = requestDataSink;
        try {
            cursor = mDatabase.rawQuery(generateQuery(requestTile), null);

            if (cursor.getCount() == 0) {
                cursor.close();
                cursor = mDatabase.rawQuery(generateOverzoomQuery(requestTile), null);
            }

            if (cursor.moveToFirst()) {
                byte[] tileData = cursor.getBlob(cursor.getColumnIndexOrThrow("tile_data"));

                MapTile responseTile = create(cursor);

                if (requestTile.zoomLevel != responseTile.zoomLevel) {
                    responseDataSink = new OverzoomDataSink(
                            requestDataSink,
                            responseTile,
                            requestTile
                    );
                }

                boolean success = mThreadLocalMvtTileDecoders.get().decode(
                        responseTile,
                        responseDataSink,
                        new GZIPInputStream(new ByteArrayInputStream(tileData))
                );

                responseDataSink.completed(success ? QueryResult.SUCCESS : QueryResult.FAILED);
            } else {
                responseDataSink.completed(QueryResult.TILE_NOT_FOUND);
            }
        } catch (IOException e) {
            responseDataSink.completed(QueryResult.FAILED);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void dispose() {
        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
        }
    }

    @Override
    public void cancel() {
    }

    private String generateQuery(MapTile tile) {
        int tmsTileY = (int) MercatorProjection.tileYToTMS(tile.tileY, tile.zoomLevel);

        return String.format(
                SELECT_TILES_FORMAT,
                String.format(Locale.US, WHERE_FORMAT, tile.zoomLevel, tile.tileX, tmsTileY)
        );
    }

    /**
     * Overzoom on the DB layer: generating a query for all tiles with lower zoomLevel than the
     * one requested
     */
    private String generateOverzoomQuery(MapTile tile) {
        int tmsTileY = (int) MercatorProjection.tileYToTMS(tile.tileY, tile.zoomLevel);

        StringBuilder whereBuilder = new StringBuilder();
        whereBuilder.append("(");

        for (int zoomLevel = tile.zoomLevel - 1; zoomLevel > 0; zoomLevel--) {
            int diff = tile.zoomLevel - zoomLevel;
            whereBuilder.append(
                    String.format(
                            Locale.US,
                            WHERE_FORMAT,
                            zoomLevel,
                            tile.tileX >> diff,
                            tmsTileY >> diff
                    )
            );

            if (zoomLevel > 1) { // Not the last iteration
                whereBuilder.append(") OR (");
            }
        }

        whereBuilder.append(")");

        return String.format(SELECT_TILES_FORMAT, whereBuilder.toString());
    }

    private MapTile create( Cursor cursor) {
        int tileX = cursor.getInt(cursor.getColumnIndexOrThrow("tile_column"));
        int tileY = cursor.getInt(cursor.getColumnIndexOrThrow("tile_row"));
        byte zoomLevel = (byte) cursor.getInt(cursor.getColumnIndexOrThrow("zoom_level"));
        int tmsTileY = (int) MercatorProjection.tileYToTMS(tileY, zoomLevel);

        return new MapTile(tileX, tmsTileY, zoomLevel);
    }
}
