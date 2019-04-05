/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2018 devemux86
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
package org.oscim.android.test;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Environment;

import org.oscim.android.tiling.MBTilesBitmapTileSource;
import org.oscim.core.MapPosition;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;

import java.io.File;

/**
 * An example activity making use of mbtiles.
 */
public class MBTilesBitmapTileActivity extends MapActivity {
    public MBTilesBitmapTileActivity(int contentView) {
        super(contentView);
    }

    public MBTilesBitmapTileActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // add a OSM layer just to see where we are
        UrlTileSource tileSource = DefaultSources.OPENSTREETMAP.build();
        mMap.layers().add(new BitmapTileLayer(mMap, tileSource));

        try {
            File mbtilesFile = new File(Environment.getExternalStorageDirectory(), "test.mbtiles");
            if (!mbtilesFile.exists()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Missing map")
                        .setMessage("This sample needs an mbtiles db named test.mbtiles in the root folder.")
                        .setIcon(android.R.drawable.ic_dialog_alert).setCancelable(true);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                String mbtilesPath = mbtilesFile.getAbsolutePath();
                MBTilesBitmapTileSource mbTilesTileSource = new MBTilesBitmapTileSource(mbtilesPath, 128, null);
                BitmapTileLayer bitmapLayer = new BitmapTileLayer(mMap, mbTilesTileSource);
                mMap.layers().add(bitmapLayer);

                double[] wesn = mbTilesTileSource.getBounds();
                double lat = 46.0;
                double lon = 11.0;
                if (wesn != null) {
                    lat = wesn[2] + (wesn[3] - wesn[2]) / 2.0;
                    lon = wesn[0] + (wesn[1] - wesn[0]) / 2.0;
                }

                /* set initial position on first run */
                MapPosition pos = new MapPosition();
                mMap.getMapPosition(pos);
                if (pos.x == 0.5 && pos.y == 0.5)
                    mMap.setMapPosition(lat, lon, Math.pow(2, 16));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
