/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2018 devemux86
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
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.oscim.android.cache.TileCache;
import org.oscim.android.tiling.MBTilesTileSource;
import org.oscim.backend.canvas.Color;
import org.oscim.core.MapPosition;
import org.oscim.layers.TileGridLayer;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.map.Layers;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;
import org.oscim.tiling.source.mvt.OpenMapTilesMvtTileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MBTilesMapActivity extends MapActivity {
    static final Logger log = LoggerFactory.getLogger(MBTilesMapActivity.class);

    static final boolean USE_CACHE = false;


    private TileCache mCache;

    public MBTilesMapActivity(int contentView) {
        super(contentView);
    }

    public MBTilesMapActivity() {
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
                        .setMessage("Thi sample needs an mbtiles db named test.mbtiles in the root folder.")
                        .setIcon(android.R.drawable.ic_dialog_alert).setCancelable(true);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                String mbtilesPath = mbtilesFile.getAbsolutePath();
                MBTilesTileSource mbTilesTileSource = new MBTilesTileSource(this, mbtilesPath, 128, null);
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

    /**
     * Copy a file.
     *
     * @param fis source file.
     * @param fos dest file.
     * @throws IOException if something goes wrong.
     */
    public static void copyFile(InputStream fis, OutputStream fos) throws IOException {
        try {
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } finally {
            if (fis != null)
                fis.close();
            if (fos != null)
                fos.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCache != null)
            mCache.dispose();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.theme_menu, menu);
        return true;
    }
}
