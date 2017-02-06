/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2016 devemux86
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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.oscim.android.filepicker.FilePicker;
import org.oscim.android.filepicker.FilterByFileExtension;
import org.oscim.android.filepicker.ValidMapFile;
import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.layers.TileGridLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.oscim.tiling.source.mapfile.MapInfo;

public class MapsforgeMapActivity extends MapActivity {
    private static final int SELECT_MAP_FILE = 0;

    private TileGridLayer mGridLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivityForResult(new Intent(this, MapFilePicker.class),
                SELECT_MAP_FILE);
    }

    public static class MapFilePicker extends FilePicker {
        public MapFilePicker() {
            setFileDisplayFilter(new FilterByFileExtension(".map"));
            setFileSelectFilter(new ValidMapFile());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.theme_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.theme_default:
                mMap.setTheme(VtmThemes.DEFAULT);
                item.setChecked(true);
                return true;

            case R.id.theme_tubes:
                mMap.setTheme(VtmThemes.TRONRENDER);
                item.setChecked(true);
                return true;

            case R.id.theme_osmarender:
                mMap.setTheme(VtmThemes.OSMARENDER);
                item.setChecked(true);
                return true;

            case R.id.theme_newtron:
                mMap.setTheme(VtmThemes.NEWTRON);
                item.setChecked(true);
                return true;

            case R.id.gridlayer:
                if (item.isChecked()) {
                    item.setChecked(false);
                    mMap.layers().remove(mGridLayer);
                } else {
                    item.setChecked(true);
                    if (mGridLayer == null)
                        mGridLayer = new TileGridLayer(mMap, getResources().getDisplayMetrics().density);

                    mMap.layers().add(mGridLayer);
                }
                mMap.updateMap(true);
                return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == SELECT_MAP_FILE) {
            if (resultCode != RESULT_OK || intent == null || intent.getStringExtra(FilePicker.SELECTED_FILE) == null) {
                finish();
                return;
            }

            MapFileTileSource tileSource = new MapFileTileSource();
            tileSource.setPreferredLanguage("en");
            String file = intent.getStringExtra(FilePicker.SELECTED_FILE);
            if (tileSource.setMapFile(file)) {

                VectorTileLayer l = mMap.setBaseMap(tileSource);
                loadTheme(null);

                mMap.layers().add(new BuildingLayer(mMap, l));
                mMap.layers().add(new LabelLayer(mMap, l));

                MapInfo info = tileSource.getMapInfo();
                MapPosition pos = new MapPosition();
                pos.setByBoundingBox(info.boundingBox, Tile.SIZE * 4, Tile.SIZE * 4);
                mMap.setMapPosition(pos);

                mPrefs.clear();
            }
        }
    }

    protected void loadTheme(final String styleId) {
        mMap.setTheme(VtmThemes.DEFAULT);
    }
}
