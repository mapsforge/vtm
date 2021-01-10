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
import org.oscim.theme.*;
import org.oscim.theme.styles.AreaStyle;
import org.oscim.theme.styles.RenderStyle;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.oscim.tiling.source.mapfile.MapInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipInputStream;

public class MapsforgeActivity extends MapActivity {

    private static final Logger log = LoggerFactory.getLogger(MapsforgeActivity.class);

    static final int SELECT_MAP_FILE = 0;
    private static final int SELECT_THEME_ARCHIVE = 1;
    private static final int SELECT_THEME_DIR = 2;
    static final int SELECT_THEME_FILE = 3;

    private static final Tag ISSEA_TAG = new Tag("natural", "issea");
    private static final Tag NOSEA_TAG = new Tag("natural", "nosea");
    private static final Tag SEA_TAG = new Tag("natural", "sea");

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

        switch (item.getItemId()) {
            case R.id.theme_default:
                if (mTheme != null)
                    mTheme.dispose();
                mTheme = mMap.setTheme(VtmThemes.DEFAULT);
                item.setChecked(true);
                return true;

            case R.id.theme_osmarender:
                if (mTheme != null)
                    mTheme.dispose();
                mTheme = mMap.setTheme(VtmThemes.OSMARENDER);
                item.setChecked(true);
                return true;

            case R.id.theme_osmagray:
                if (mTheme != null)
                    mTheme.dispose();
                mTheme = mMap.setTheme(VtmThemes.OSMAGRAY);
                item.setChecked(true);
                return true;

            case R.id.theme_tubes:
                if (mTheme != null)
                    mTheme.dispose();
                mTheme = mMap.setTheme(VtmThemes.TRONRENDER);
                item.setChecked(true);
                return true;

            case R.id.theme_newtron:
                if (mTheme != null)
                    mTheme.dispose();
                mTheme = mMap.setTheme(VtmThemes.NEWTRON);
                item.setChecked(true);
                return true;

            case R.id.theme_external_archive:
                Intent intent = new Intent(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, SELECT_THEME_ARCHIVE);
                return true;

            case R.id.theme_external:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                    return false;
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, SELECT_THEME_DIR);
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
                log.error(e.getMessage());
                finish();
            }
        } else if (requestCode == SELECT_THEME_ARCHIVE) {
            if (resultCode != Activity.RESULT_OK || data == null)
                return;

            try {
                Uri uri = data.getData();

                final ZipXmlThemeResourceProvider resourceProvider = new ZipXmlThemeResourceProvider(new ZipInputStream(new BufferedInputStream(getContentResolver().openInputStream(uri))));
                final List<String> xmlThemes = resourceProvider.getXmlThemes();
                if (xmlThemes.isEmpty())
                    return;

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.dialog_theme_title);
                builder.setSingleChoiceItems(xmlThemes.toArray(new String[0]), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ThemeFile theme = new ZipRenderTheme(xmlThemes.get(which), resourceProvider);
                        if (mTheme != null)
                            mTheme.dispose();
                        mTheme = mMap.setTheme(theme);
                        mapsforgeTheme(mTheme);
                        mMenu.findItem(R.id.theme_external_archive).setChecked(true);
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
            mapsforgeTheme(mTheme);
            mMenu.findItem(R.id.theme_external).setChecked(true);
        }
    }

    protected void loadTheme(final String styleId) {
        if (mTheme != null)
            mTheme.dispose();
        mTheme = mMap.setTheme(VtmThemes.DEFAULT);
    }

    private void mapsforgeTheme(IRenderTheme theme) {
        if (!theme.isMapsforgeTheme())
            return;

        // Use tessellation with sea and land for Mapsforge themes
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
}
