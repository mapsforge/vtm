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
import org.oscim.android.tiling.source.mbtiles.MBTilesBitmapTileSource;
import org.oscim.android.tiling.source.mbtiles.MBTilesTileSource;
import org.oscim.core.BoundingBox;
import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;

import java.io.File;

/**
 * An example activity making use of raster MBTiles.
 */
public class MBTilesBitmapActivity extends BitmapTileActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File file = new File(getExternalFilesDir(null), "raster.mbtiles");
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

        MBTilesTileSource tileSource = new MBTilesBitmapTileSource(file.getAbsolutePath(), 128, null);

        BitmapTileLayer bitmapLayer = new BitmapTileLayer(mMap, tileSource);
        mMap.layers().add(bitmapLayer);

        /* set initial position on first run */
        MapPosition pos = new MapPosition();
        mMap.getMapPosition(pos);
        if (pos.x == 0.5 && pos.y == 0.5) {
            BoundingBox bbox = tileSource.getDataSource().getBounds();
            if (bbox != null) {
                pos.setByBoundingBox(bbox, Tile.SIZE * 4, Tile.SIZE * 4);
                mMap.setMapPosition(pos);
            }
        }
    }
}
