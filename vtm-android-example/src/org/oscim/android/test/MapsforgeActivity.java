/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2016-2021 devemux86
 * Copyright 2017 Longri
 * Copyright 2018 Gustl22
 * Copyright 2021 eddiemuc
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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.Menu;
import android.view.MenuItem;
import org.oscim.android.theme.ContentRenderTheme;
import org.oscim.android.theme.ContentResolverResourceProvider;
import org.oscim.backend.CanvasAdapter;
import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.layers.TileGridLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.buildings.S3DBLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.renderer.BitmapRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.scalebar.*;
import org.oscim.theme.IRenderTheme;
import org.oscim.theme.ThemeFile;
import org.oscim.theme.ZipRenderTheme;
import org.oscim.theme.ZipXmlThemeResourceProvider;
import org.oscim.theme.internal.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.oscim.tiling.source.mapfile.MapInfo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

public class MapsforgeActivity extends MapActivity {

    private static final Logger log = Logger.getLogger(MapsforgeActivity.class.getName());

    static final int SELECT_MAP_FILE = 0;
    private static final int SELECT_THEME_ARCHIVE = 1;
    private static final int SELECT_THEME_DIR = 2;
    static final int SELECT_THEME_FILE = 3;

    private TileGridLayer mGridLayer;
    private Menu mMenu;
    private final boolean mS3db;
    IRenderTheme mTheme;
    VectorTileLayer mTileLayer;
    private Uri mThemeDirUri;

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
        int itemId = item.getItemId();
        if (itemId == R.id.theme_motorider) {
            if (mTheme != null)
                mTheme.dispose();
            mTheme = mMap.setTheme(VtmThemes.MOTORIDER);
            item.setChecked(true);
            return true;
        } else if (itemId == R.id.theme_biker) {
            if (mTheme != null)
                mTheme.dispose();
            mTheme = mMap.setTheme(VtmThemes.BIKER);
            item.setChecked(true);
            return true;
        } else if (itemId == R.id.theme_default) {
            if (mTheme != null)
                mTheme.dispose();
            mTheme = mMap.setTheme(VtmThemes.DEFAULT);
            item.setChecked(true);
            return true;
        } else if (itemId == R.id.theme_osmarender) {
            if (mTheme != null)
                mTheme.dispose();
            mTheme = mMap.setTheme(VtmThemes.OSMARENDER);
            item.setChecked(true);
            return true;
        } else if (itemId == R.id.theme_external_archive) {
            Intent intent = new Intent(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, SELECT_THEME_ARCHIVE);
            return true;
        } else if (itemId == R.id.theme_external) {
            Intent intent;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                return false;
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, SELECT_THEME_DIR);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SELECT_MAP_FILE) {
            if (resultCode != Activity.RESULT_OK || data == null) {
                finish();
                return;
            }

            try {
                Uri uri = data.getData();

                MapFileTileSource tileSource = new MapFileTileSource();
                //tileSource.setPreferredLanguage("en");
                FileInputStream fis = (FileInputStream) getContentResolver().openInputStream(uri);
                tileSource.setMapFileInputStream(fis);

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
            } catch (Exception e) {
                log.severe(e.toString());
                finish();
            }
        } else if (requestCode == SELECT_THEME_ARCHIVE) {
            if (resultCode != Activity.RESULT_OK || data == null)
                return;

            try {
                final Uri uri = data.getData();

                final List<String> xmlThemes = ZipXmlThemeResourceProvider.scanXmlThemes(new ZipInputStream(new BufferedInputStream(getContentResolver().openInputStream(uri))));
                if (xmlThemes.isEmpty())
                    return;

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.dialog_theme_title);
                builder.setSingleChoiceItems(xmlThemes.toArray(new String[0]), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            dialog.dismiss();
                            ThemeFile theme = new ZipRenderTheme(xmlThemes.get(which), new ZipXmlThemeResourceProvider(new ZipInputStream(new BufferedInputStream(getContentResolver().openInputStream(uri)))));
                            if (mTheme != null)
                                mTheme.dispose();
                            mTheme = mMap.setTheme(theme);
                            mMenu.findItem(R.id.theme_external_archive).setChecked(true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == SELECT_THEME_DIR) {
            if (resultCode != Activity.RESULT_OK || data == null)
                return;

            mThemeDirUri = data.getData();

            // Now we have the directory for resources, but we need to let the user also select a theme file
            Intent intent = new Intent(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, mThemeDirUri);
            startActivityForResult(intent, SELECT_THEME_FILE);
        } else if (requestCode == SELECT_THEME_FILE) {
            if (resultCode != Activity.RESULT_OK || data == null)
                return;

            Uri uri = data.getData();
            ThemeFile theme = new ContentRenderTheme(getContentResolver(), uri);
            theme.setResourceProvider(new ContentResolverResourceProvider(getContentResolver(), mThemeDirUri));

            if (mTheme != null)
                mTheme.dispose();
            mTheme = mMap.setTheme(theme);
            mMenu.findItem(R.id.theme_external).setChecked(true);
        }
    }

    protected void loadTheme(final String styleId) {
        if (mTheme != null)
            mTheme.dispose();
        mTheme = mMap.setTheme(VtmThemes.MOTORIDER);
    }
}
