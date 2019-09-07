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
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import org.locationtech.jts.geom.Point;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.layers.tile.MapTile;
import org.oscim.map.Viewport;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MBTilesTileDataSource implements ITileDataSource {
    private static final String TABLE_TILES = "tiles";
    private static final String COL_TILES_ZOOM_LEVEL = "zoom_level";
    private static final String COL_TILES_TILE_COLUMN = "tile_column";
    private static final String COL_TILES_TILE_ROW = "tile_row";
    private static final String COL_TILES_TILE_DATA = "tile_data";
    private static final String TABLE_METADATA = "metadata";
    private static final String COL_METADATA_NAME = "name";
    private static final String COL_METADATA_VALUE = "value";

    public static final String SELECT_TILES = "SELECT " + COL_TILES_TILE_DATA + " from " + TABLE_TILES + " where "
            + COL_TILES_ZOOM_LEVEL + "=? AND " + COL_TILES_TILE_COLUMN + "=? AND " + COL_TILES_TILE_ROW + "=?";
    public static final String SELECT_METADATA = "select " + COL_METADATA_NAME + "," + COL_METADATA_VALUE + " from "
            + TABLE_METADATA;

    private final SQLiteDatabase mDatabase;
    private final String mLocale;
    private final Integer mAlpha;
    private final Integer mTransparentColor;
    private Map<String, String> mMetadata;
    private ITileDataSource mTileDataSource;

    public MBTilesTileDataSource(String path, String locale, Integer alpha, Integer transparentColor) {
        mDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        mLocale = locale;
        mAlpha = alpha;
        mTransparentColor = transparentColor;
    }

    @Override
    public void cancel() {
        findWorker().cancel();
    }

    @Override
    public void dispose() {
        findWorker().cancel();
    }

    @Override
    public void query(MapTile requestTile, ITileDataSink requestDataSink) {
        findWorker().query(requestTile, requestDataSink);
    }

    protected ITileDataSource findWorker() {
        if (mTileDataSource != null) {
            return mTileDataSource;
        }

        String mbTilesFormat = getFormat();

        if (mbTilesFormat == null) {
            throw new RuntimeException("Unable to read the '" + TABLE_METADATA + ".format' field of the database.");
        }

        if (MBTilesBitmapTileDataSourceWorker.SUPPORTED_FORMATS.contains(mbTilesFormat)) {
            mTileDataSource = new MBTilesBitmapTileDataSourceWorker(mDatabase, mAlpha, mTransparentColor);

            return mTileDataSource;
        }

        if (MBTilesMvtTileDataSourceWorker.SUPPORTED_FORMATS.contains(mbTilesFormat)) {
            mTileDataSource = new MBTilesMvtTileDataSourceWorker(mDatabase, mLocale);

            return mTileDataSource;
        }

        List<String> supportedFormats = new ArrayList<>();
        supportedFormats.addAll(MBTilesBitmapTileDataSourceWorker.SUPPORTED_FORMATS);
        supportedFormats.addAll(MBTilesMvtTileDataSourceWorker.SUPPORTED_FORMATS);

        throw new RuntimeException(
                String.format(
                        "Unknown MBtiles database format '%s' found in the 'metadata.format' field of the database. "
                        + "Expected one of: '%s'",
                        mbTilesFormat,
                        TextUtils.join(", ", supportedFormats)
                )
        );
    }

    String getAttribution() {
        return getMetadata().get("attribution");
    }

    BoundingBox getBounds() {
        String bounds = getMetadata().get("bounds");

        if (bounds == null) {
            return null;
        }

        String[] split = bounds.split(",");
        double w = Double.parseDouble(split[0]);
        double s = Double.parseDouble(split[1]);
        double e = Double.parseDouble(split[2]);
        double n = Double.parseDouble(split[3]);

        return new BoundingBox(s, w, n, e);
    }

    MapPosition getCenter() {
        String center = getMetadata().get("center");

        if (center == null) {
            return null;
        }

        String[] split = center.split(",");
        double latitude = Double.parseDouble(split[1]);
        double longitude = Double.parseDouble(split[0]);
        int zoomLevel = Integer.parseInt(split[2]);

        MapPosition centerMapPosition = new MapPosition();
        centerMapPosition.setPosition(latitude, longitude);
        centerMapPosition.setZoomLevel(zoomLevel);

        return centerMapPosition;
    }

    String getDescription() {
        return getMetadata().get("description");
    }

    String getFormat() {
        return getMetadata().get("format");
    }

    Integer getPixelScale() {
        String pixelScale = getMetadata().get("pixel_scale");

        return pixelScale != null ? Integer.parseInt(pixelScale) : null;
    }

    int getMaxZoom() {
        String maxZoom = getMetadata().get("maxzoom");

        return maxZoom != null ? Integer.parseInt(maxZoom) : Viewport.MAX_ZOOM_LEVEL;
    }

    int getMinZoom() {
        String minZoom = getMetadata().get("minzoom");

        return minZoom != null ? Integer.parseInt(minZoom) : Viewport.MIN_ZOOM_LEVEL;
    }

    String getName() {
        return getMetadata().get("name");
    }

    String getVersion() {
        return getMetadata().get("version");
    }

    String getId() {
        return getMetadata().get("id");
    }

    Long getMTime() {
        String mTime = getMetadata().get("mtime");

        return mTime != null ? Long.parseLong(mTime) : null;
    }

    String getJson() {
        return getMetadata().get("json");
    }

    private Map<String, String> getMetadata() {
        if (mMetadata != null) {
            return mMetadata;
        }

        mMetadata = new HashMap<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(SELECT_METADATA, null);
            while (cursor.moveToNext()) {
                String key = cursor.getString(0);
                String value = cursor.getString(1);
                mMetadata.put(key, value);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return mMetadata;
    }
}
