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

import org.oscim.core.BoundingBox;
import org.oscim.core.MapPosition;
import org.oscim.map.Viewport;
import org.oscim.tiling.ITileDataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class MBTilesTileDataSource implements ITileDataSource {
    public static final String SELECT_TILES_FORMAT =
            "SELECT zoom_level, tile_column, tile_row, tile_data " +
            "FROM tiles " +
            "WHERE %s " +
            "ORDER BY zoom_level DESC " +
            "LIMIT 1";

    public static final String WHERE_FORMAT = "zoom_level=? AND tile_column=? AND tile_row=?";
    public static final String SELECT_METADATA = "SELECT name, value FROM metadata";

    protected final SQLiteDatabase mDatabase;
    protected Map<String, String> mMetadata;

    public MBTilesTileDataSource(String path) {
        mDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
    }

    abstract public List<String> getSupportedFormats();

    protected void assertDatabaseFormat() {
        String mbTilesFormat = getFormat();

        if (mbTilesFormat == null) {
            throw new RuntimeException("'metadata.format' field was not found. Is this an MBTiles database?");
        }

        List<String> supportedFormats = getSupportedFormats();

        if (!supportedFormats.contains(mbTilesFormat)) {
            throw new RuntimeException(
                    String.format(
                            "Unsupported MBTiles 'metadata.format: %s'. Supported format(s) are: %s",
                            mbTilesFormat,
                            TextUtils.join(", ", supportedFormats)
                    )
            );
        }
    }

    public String getAttribution() {
        return getMetadata().get("attribution");
    }

    public BoundingBox getBounds() {
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

    public MapPosition getCenter() {
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

    public String getDescription() {
        return getMetadata().get("description");
    }

    public String getFormat() {
        return getMetadata().get("format");
    }

    public Integer getPixelScale() {
        String pixelScale = getMetadata().get("pixel_scale");

        return pixelScale != null ? Integer.parseInt(pixelScale) : null;
    }

    public int getMaxZoom() {
        String maxZoom = getMetadata().get("maxzoom");

        return maxZoom != null ? Integer.parseInt(maxZoom) : Viewport.MAX_ZOOM_LEVEL;
    }

    public int getMinZoom() {
        String minZoom = getMetadata().get("minzoom");

        return minZoom != null ? Integer.parseInt(minZoom) : Viewport.MIN_ZOOM_LEVEL;
    }

    public String getName() {
        return getMetadata().get("name");
    }

    public String getVersion() {
        return getMetadata().get("version");
    }

    public String getId() {
        return getMetadata().get("id");
    }

    public Long getMTime() {
        String mTime = getMetadata().get("mtime");

        return mTime != null ? Long.parseLong(mTime) : null;
    }

    public String getJson() {
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
