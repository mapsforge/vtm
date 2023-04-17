/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2020 devemux86
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

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import org.oscim.core.MapPosition;
import org.oscim.layers.TileGridLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

import java.io.File;

public class BaseMapActivity extends MapActivity {

    static final boolean USE_CACHE = false;

    VectorTileLayer mBaseLayer;
    TileSource mTileSource;
    TileGridLayer mGridLayer;

    public BaseMapActivity(int contentView) {
        super(contentView);
    }

    public BaseMapActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (USE_CACHE) {
            // Cache the tiles into file system
            File cacheDirectory = new File(getExternalCacheDir(), "tiles");
            int cacheSize = 10 * 1024 * 1024; // 10 MB
            Cache cache = new Cache(cacheDirectory, cacheSize);
            builder.cache(cache);
        }

        mTileSource = OSciMap4TileSource.builder()
                .httpFactory(new OkHttpEngine.OkHttpFactory(builder))
                .build();

        mBaseLayer = mMap.setBaseMap(mTileSource);

        /* set initial position on first run */
        MapPosition pos = new MapPosition();
        mMap.getMapPosition(pos);
        if (pos.x == 0.5 && pos.y == 0.5)
            mMap.setMapPosition(53.08, 8.83, Math.pow(2, 16));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.theme_default) {
            mMap.setTheme(VtmThemes.DEFAULT);
            item.setChecked(true);
            return true;
        } else if (itemId == R.id.theme_osmarender) {
            mMap.setTheme(VtmThemes.OSMARENDER);
            item.setChecked(true);
            return true;
        } else if (itemId == R.id.theme_osmagray) {
            mMap.setTheme(VtmThemes.OSMAGRAY);
            item.setChecked(true);
            return true;
        } else if (itemId == R.id.theme_tubes) {
            mMap.setTheme(VtmThemes.TRONRENDER);
            item.setChecked(true);
            return true;
        } else if (itemId == R.id.theme_newtron) {
            mMap.setTheme(VtmThemes.NEWTRON);
            item.setChecked(true);
            return true;
        } else if (itemId == R.id.gridlayer) {
            if (item.isChecked()) {
                item.setChecked(false);
                mMap.layers().remove(mGridLayer);
            } else {
                item.setChecked(true);
                if (mGridLayer == null)
                    mGridLayer = new TileGridLayer(mMap);

                mMap.layers().add(mGridLayer);
            }
            mMap.updateMap(true);
            return true;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.theme_menu, menu);
        return true;
    }
}
