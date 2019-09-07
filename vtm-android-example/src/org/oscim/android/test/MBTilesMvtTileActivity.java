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
package org.oscim.android.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;

import org.oscim.android.cache.TileCache;
import org.oscim.android.tiling.source.mbtiles.MBTilesMvtTileDataSourceWorker;
import org.oscim.android.tiling.source.mbtiles.MBTilesTileSource;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.mvt.OpenMapTilesMvtTileSource;

import java.io.File;

public class MBTilesMvtTileActivity extends MapActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File file = new File(getExternalFilesDir(null), "vector.mbtiles");
        if (!file.exists()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(R.string.warning)
                    .setMessage(getResources().getString(R.string.startup_message_mbtiles, file.getName(), file.getAbsolutePath()))
                    .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.show();

            return;
        }

        MBTilesTileSource tileSource = new MBTilesTileSource(file.getAbsolutePath(), "en");

        if (!MBTilesMvtTileDataSourceWorker.SUPPORTED_FORMATS.contains(tileSource.getMetadataFormat())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(R.string.warning)
                    .setMessage(
                            getResources().getString(
                                    R.string.unknown_format_mbtiles,
                                    tileSource.getMetadataFormat(),
                                    file.getAbsolutePath(),
                                    TextUtils.join(", ", MBTilesMvtTileDataSourceWorker.SUPPORTED_FORMATS)
                            )
                    )
                    .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.show();

            return;
        }

        VectorTileLayer vectorTileLayer = mMap.setBaseMap(tileSource);
        mMap.setTheme(VtmThemes.OPENMAPTILES);

        mMap.layers().add(new BuildingLayer(mMap, vectorTileLayer));
        mMap.layers().add(new LabelLayer(mMap, vectorTileLayer));

        MapPosition pos = new MapPosition();
        mMap.getMapPosition(pos);
        if (pos.x == 0.5 && pos.y == 0.5) {
            BoundingBox bbox = tileSource.getMetadataBounds();
            if (bbox != null) {
                pos.setByBoundingBox(bbox, Tile.SIZE * 4, Tile.SIZE * 4);
                mMap.setMapPosition(pos);
            }
        }
    }
}
