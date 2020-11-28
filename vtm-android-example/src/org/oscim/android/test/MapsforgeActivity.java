/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2016-2020 devemux86
 * Copyright 2017 Longri
 * Copyright 2018 Gustl22
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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import org.oscim.android.theme.ContentRenderTheme;
import org.oscim.backend.CanvasAdapter;
import org.oscim.core.MapElement;
import org.oscim.core.MapPosition;
import org.oscim.core.Tag;
import org.oscim.core.Tile;
import org.oscim.layers.TileGridLayer;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.buildings.S3DBLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.renderer.BitmapRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.bucket.RenderBuckets;
import org.oscim.scalebar.*;
import org.oscim.theme.ThemeFile;
import org.oscim.theme.VtmThemes;
import org.oscim.theme.styles.AreaStyle;
import org.oscim.theme.styles.RenderStyle;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.oscim.tiling.source.mapfile.MapInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;

public class MapsforgeActivity extends MapActivity {

    private static final Logger log = LoggerFactory.getLogger(MapsforgeActivity.class);

    static final int SELECT_MAP_FILE = 0;
    static final int SELECT_THEME_FILE = 1;

    private static final Tag ISSEA_TAG = new Tag("natural", "issea");
    private static final Tag NOSEA_TAG = new Tag("natural", "nosea");
    private static final Tag SEA_TAG = new Tag("natural", "sea");

    private TileGridLayer mGridLayer;
    private Menu mMenu;
    private final boolean mS3db;
    VectorTileLayer mTileLayer;

    public MapsforgeActivity() {
        this(false);
    }

    public MapsforgeActivity(boolean s3db) {
        super();
        mS3db = s3db;
    }

    public MapsforgeActivity(boolean s3db, int contentView) {
        super(contentView);
        mS3db = s3db;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, SELECT_MAP_FILE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.theme_menu, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.theme_default:
                mMap.setTheme(VtmThemes.DEFAULT);
                item.setChecked(true);
                return true;

            case R.id.theme_osmarender:
                mMap.setTheme(VtmThemes.OSMARENDER);
                item.setChecked(true);
                return true;

            case R.id.theme_osmagray:
                mMap.setTheme(VtmThemes.OSMAGRAY);
                item.setChecked(true);
                return true;

            case R.id.theme_tubes:
                mMap.setTheme(VtmThemes.TRONRENDER);
                item.setChecked(true);
                return true;

            case R.id.theme_newtron:
                mMap.setTheme(VtmThemes.NEWTRON);
                item.setChecked(true);
                return true;

            case R.id.theme_external:
                Intent intent = new Intent(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, SELECT_THEME_FILE);
                return true;

            case R.id.gridlayer:
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SELECT_MAP_FILE) {
            if (resultCode != Activity.RESULT_OK || data == null) {
                finish();
                return;
            }

            MapFileTileSource tileSource = new MapFileTileSource();
            //tileSource.setPreferredLanguage("en");

            try {
                Uri uri = data.getData();
                FileInputStream fis = (FileInputStream) getContentResolver().openInputStream(uri);
                tileSource.setMapFileInputStream(fis);
            } catch (IOException e) {
                log.error(e.getMessage());
                finish();
                return;
            }

            mTileLayer = mMap.setBaseMap(tileSource);
            loadTheme(null);

            if (mS3db)
                mMap.layers().add(new S3DBLayer(mMap, mTileLayer));
            else
                mMap.layers().add(new BuildingLayer(mMap, mTileLayer));
            mMap.layers().add(new LabelLayer(mMap, mTileLayer));

            DefaultMapScaleBar mapScaleBar = new DefaultMapScaleBar(mMap);
            mapScaleBar.setScaleBarMode(DefaultMapScaleBar.ScaleBarMode.BOTH);
            mapScaleBar.setDistanceUnitAdapter(MetricUnitAdapter.INSTANCE);
            mapScaleBar.setSecondaryDistanceUnitAdapter(ImperialUnitAdapter.INSTANCE);
            mapScaleBar.setScaleBarPosition(MapScaleBar.ScaleBarPosition.BOTTOM_LEFT);

            MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mMap, mapScaleBar);
            BitmapRenderer renderer = mapScaleBarLayer.getRenderer();
            renderer.setPosition(GLViewport.Position.BOTTOM_LEFT);
            renderer.setOffset(5 * CanvasAdapter.getScale(), 0);
            mMap.layers().add(mapScaleBarLayer);

            MapInfo info = tileSource.getMapInfo();
            if (!info.boundingBox.contains(mMap.getMapPosition().getGeoPoint())) {
                MapPosition pos = new MapPosition();
                pos.setByBoundingBox(info.boundingBox, Tile.SIZE * 4, Tile.SIZE * 4);
                mMap.setMapPosition(pos);
                mPrefs.clear();
            }
        } else if (requestCode == SELECT_THEME_FILE) {
            if (resultCode != Activity.RESULT_OK || data == null)
                return;

            Uri uri = data.getData();
            ThemeFile theme = new ContentRenderTheme(getContentResolver(), "", uri);

            // Use tessellation with sea and land for Mapsforge themes
            if (theme.isMapsforgeTheme()) {
                mTileLayer.addHook(new VectorTileLayer.TileLoaderThemeHook() {
                    @Override
                    public boolean process(MapTile tile, RenderBuckets buckets, MapElement element, RenderStyle style, int level) {
                        if (element.tags.contains(ISSEA_TAG) || element.tags.contains(SEA_TAG) || element.tags.contains(NOSEA_TAG)) {
                            if (style instanceof AreaStyle)
                                ((AreaStyle) style).mesh = true;
                        }
                        return false;
                    }

                    @Override
                    public void complete(MapTile tile, boolean success) {
                    }
                });
            }

            mMap.setTheme(theme);
            mMenu.findItem(R.id.theme_external).setChecked(true);
        }
    }

    protected void loadTheme(final String styleId) {
        mMap.setTheme(VtmThemes.DEFAULT);
    }
}
