/*
 * Copyright 2018-2020 devemux86
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
import org.oscim.android.MapView;
import org.oscim.backend.CanvasAdapter;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.renderer.GLViewport;
import org.oscim.scalebar.DefaultMapScaleBar;
import org.oscim.scalebar.MapScaleBar;
import org.oscim.scalebar.MapScaleBarLayer;
import org.oscim.theme.IRenderTheme;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;

import java.io.FileInputStream;

/**
 * A very basic Android app example.
 * <p>
 * You'll need a map with filename berlin.map from download.mapsforge.org in device storage.
 */
public class GettingStarted extends Activity {

    // Request code for selecting a map file
    private static final int SELECT_MAP_FILE = 0;

    private MapView mapView;
    private IRenderTheme theme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Map view
        mapView = new MapView(this);
        setContentView(mapView);

        // Open map
        Intent intent = new Intent(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, SELECT_MAP_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_MAP_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                openMap(uri);
            }
        }
    }

    private void openMap(Uri uri) {
        try {
            // Tile source
            MapFileTileSource tileSource = new MapFileTileSource();
            FileInputStream fis = (FileInputStream) getContentResolver().openInputStream(uri);
            tileSource.setMapFileInputStream(fis);

            // Vector layer
            VectorTileLayer tileLayer = mapView.map().setBaseMap(tileSource);

            // Building layer
            mapView.map().layers().add(new BuildingLayer(mapView.map(), tileLayer));

            // Label layer
            mapView.map().layers().add(new LabelLayer(mapView.map(), tileLayer));

            // Render theme
            theme = mapView.map().setTheme(VtmThemes.DEFAULT);

            // Scale bar
            MapScaleBar mapScaleBar = new DefaultMapScaleBar(mapView.map());
            MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mapView.map(), mapScaleBar);
            mapScaleBarLayer.getRenderer().setPosition(GLViewport.Position.BOTTOM_LEFT);
            mapScaleBarLayer.getRenderer().setOffset(5 * CanvasAdapter.getScale(), 0);
            mapView.map().layers().add(mapScaleBarLayer);

            // Note: this map position is specific to Berlin area
            mapView.map().setMapPosition(52.517037, 13.38886, 1 << 12);
        } catch (Exception e) {
            /*
             * In case of map file errors avoid crash, but developers should handle these cases!
             */
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        theme.dispose();
        super.onDestroy();
    }
}
