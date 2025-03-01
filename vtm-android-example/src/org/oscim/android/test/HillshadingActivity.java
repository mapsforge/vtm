/*
 * Copyright 2025 devemux86
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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.hills.DemFolderAndroidContent;
import org.mapsforge.map.layer.hills.AdaptiveClasyHillShading;
import org.mapsforge.map.layer.hills.DemFolder;
import org.oscim.android.cache.TileCache;
import org.oscim.backend.CanvasAdapter;
import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Viewport;
import org.oscim.renderer.BitmapRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.scalebar.*;
import org.oscim.theme.internal.VtmThemes;
import org.oscim.tiling.ITileCache;
import org.oscim.tiling.source.hills.HillshadingTileSource;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.oscim.tiling.source.mapfile.MapInfo;

import java.io.FileInputStream;
import java.util.logging.Logger;

/**
 * Standard map view with hill shading.
 */
public class HillshadingActivity extends MapActivity {

    private static final Logger log = Logger.getLogger(HillshadingActivity.class.getName());

    private static final boolean USE_CACHE = false;

    private static final int SELECT_MAP_FILE = 0;
    private static final int SELECT_DEM_DIR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Select map file
        Toast.makeText(this, "Select map file", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, SELECT_MAP_FILE);
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

                VectorTileLayer tileLayer = mMap.setBaseMap(tileSource);
                mMap.setTheme(VtmThemes.MOTORIDER);

                mMap.layers().add(new BuildingLayer(mMap, tileLayer));
                mMap.layers().add(new LabelLayer(mMap, tileLayer));

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

                // Select DEM folder
                Toast.makeText(this, "Select DEM folder", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
                );
                startActivityForResult(intent, SELECT_DEM_DIR);
            } catch (Exception e) {
                log.severe(e.toString());
                finish();
            }
        } else if (requestCode == SELECT_DEM_DIR) {
            if (resultCode != Activity.RESULT_OK || data == null)
                return;

            Uri uri = data.getData();

            DemFolder demFolder = new DemFolderAndroidContent(uri, this, getContentResolver());
            final AdaptiveClasyHillShading algorithm = new AdaptiveClasyHillShading()
                    // You can make additional behavior adjustments
                    .setAdaptiveZoomEnabled(true)
                    // .setZoomMinOverride(0)
                    // .setZoomMaxOverride(17)
                    .setCustomQualityScale(1);
            HillshadingTileSource hillshadingTileSource = new HillshadingTileSource(Viewport.MIN_ZOOM_LEVEL, Viewport.MAX_ZOOM_LEVEL, demFolder, algorithm, 128, Color.BLACK, AndroidGraphicFactory.INSTANCE);
            if (USE_CACHE) {
                ITileCache tileCache = new TileCache(this, getExternalCacheDir().getAbsolutePath(), "hillshading");
                hillshadingTileSource.setCache(tileCache);
            }
            mMap.layers().add(new BitmapTileLayer(mMap, hillshadingTileSource, 150));
            mMap.clearMap();
        }
    }
}
