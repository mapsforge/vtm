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
import org.oscim.android.mvt.tiling.source.mbtiles.MBTilesMvtTileSource;
import org.oscim.android.tiling.source.mbtiles.MBTilesTileSource;
import org.oscim.android.tiling.source.mbtiles.MBTilesUnsupportedException;
import org.oscim.core.BoundingBox;
import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.internal.VtmThemes;

import java.io.File;

/**
 * An example activity making use of vector MBTiles.
 */
public class MBTilesMvtActivity extends MapActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File file = new File(getExternalMediaDirs()[0], "vector.mbtiles");
        if (!file.exists()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(R.string.warning)
                    .setMessage(getResources().getString(R.string.startup_message_mbtiles))
                    .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.show();
            return;
        }

        MBTilesTileSource tileSource;
        try {
            tileSource = new MBTilesMvtTileSource(file.getAbsolutePath(), "en");
        } catch (MBTilesUnsupportedException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(R.string.warning)
                    .setMessage(e.toString())
                    .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.show();
            return;
        }

        VectorTileLayer l = mMap.setBaseMap(tileSource);
        mMap.setTheme(VtmThemes.OPENMAPTILES);

        mMap.layers().add(new BuildingLayer(mMap, l));
        mMap.layers().add(new LabelLayer(mMap, l));

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
