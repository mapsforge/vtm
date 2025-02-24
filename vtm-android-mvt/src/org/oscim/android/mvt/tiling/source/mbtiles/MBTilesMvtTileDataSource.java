/*
 * Copyright 2019 Kostas Tzounopoulos
 * Copyright 2022 devemux86
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
package org.oscim.android.mvt.tiling.source.mbtiles;

import android.database.Cursor;
import org.oscim.android.tiling.source.mbtiles.MBTilesTileDataSource;
import org.oscim.android.tiling.source.mbtiles.MBTilesUnsupportedException;
import org.oscim.core.MercatorProjection;
import org.oscim.layers.tile.MapTile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.OverzoomDataSink;
import org.oscim.tiling.QueryResult;
import org.oscim.tiling.source.mvt.TileDecoder;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * A tile data source for MBTiles vector databases.
 */
public class MBTilesMvtTileDataSource extends MBTilesTileDataSource {

    private static final Logger log = Logger.getLogger(MBTilesMvtTileDataSource.class.getName());

    private static final List<String> SUPPORTED_FORMATS = Collections.singletonList("pbf");
    private static final String WHERE_FORMAT = "zoom_level=%d AND tile_column=%d AND tile_row=%d";

    private final String mLanguage;

    private final ThreadLocal<TileDecoder> mThreadLocalDecoders = new ThreadLocal<TileDecoder>() {
        @Override
        protected TileDecoder initialValue() {
            return new TileDecoder(mLanguage);
        }
    };

    /**
     * Create a tile data source for MBTiles vector databases.
     *
     * @param path     the path to the MBTiles database.
     * @param language the language to use when rendering the MBTiles.
     */
    public MBTilesMvtTileDataSource(String path, String language) {
        super(path);
        mLanguage = language != null ? language : "en";

        try {
            assertDatabaseFormat();
        } catch (MBTilesUnsupportedException e) {
            log.severe("Invalid MBTiles database" + e);
        }
    }

    @Override
    public void cancel() {
        // do nothing
    }

    @Override
    public void dispose() {
        if (mDatabase != null && mDatabase.isOpen())
            mDatabase.close();
    }

    @Override
    public List<String> getSupportedFormats() {
        return SUPPORTED_FORMATS;
    }

    private MapTile mapTile(Cursor cursor) {
        int tileX = cursor.getInt(cursor.getColumnIndexOrThrow("tile_column"));
        int tileY = cursor.getInt(cursor.getColumnIndexOrThrow("tile_row"));
        int zoomLevel = cursor.getInt(cursor.getColumnIndexOrThrow("zoom_level"));
        long tmsTileY = MercatorProjection.tileYToTMS(tileY, (byte) zoomLevel);
        return new MapTile(tileX, (int) tmsTileY, zoomLevel);
    }

    /**
     * Overzoom on the DB layer: generate a query for all tiles with lower zoomLevel than the one requested.
     */
    private String overzoomQuery(MapTile tile) {
        long tmsTileY = MercatorProjection.tileYToTMS(tile.tileY, tile.zoomLevel);
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int zoomLevel = tile.zoomLevel - 1; zoomLevel > 0; zoomLevel--) {
            int diff = tile.zoomLevel - zoomLevel;
            sb.append(String.format(Locale.US, WHERE_FORMAT, zoomLevel, tile.tileX >> diff, tmsTileY >> diff));
            if (zoomLevel > 1) // Not the last iteration
                sb.append(") OR (");
        }
        sb.append(")");
        return String.format(SELECT_TILES_FORMAT, sb.toString());
    }

    @Override
    public void query(MapTile requestTile, ITileDataSink requestDataSink) {
        Cursor cursor = null;
        ITileDataSink responseDataSink = requestDataSink;
        try {
            long tmsTileY = MercatorProjection.tileYToTMS(requestTile.tileY, requestTile.zoomLevel);
            cursor = mDatabase.rawQuery(String.format(SELECT_TILES_FORMAT, String.format(Locale.US, WHERE_FORMAT, requestTile.zoomLevel, requestTile.tileX, tmsTileY)), null);

            if (cursor.getCount() == 0) {
                cursor.close();
                cursor = mDatabase.rawQuery(overzoomQuery(requestTile), null);
            }

            if (cursor.moveToFirst()) {
                byte[] bytes = cursor.getBlob(cursor.getColumnIndexOrThrow("tile_data"));

                MapTile responseTile = mapTile(cursor);
                if (requestTile.zoomLevel != responseTile.zoomLevel)
                    responseDataSink = new OverzoomDataSink(requestDataSink, responseTile, requestTile);

                boolean success = mThreadLocalDecoders.get().decode(responseTile, responseDataSink, new GZIPInputStream(new ByteArrayInputStream(bytes)));
                responseDataSink.completed(success ? QueryResult.SUCCESS : QueryResult.FAILED);
            } else
                responseDataSink.completed(QueryResult.TILE_NOT_FOUND);
        } catch (Throwable t) {
            log.severe(t.toString());
            responseDataSink.completed(QueryResult.FAILED);
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }
}
