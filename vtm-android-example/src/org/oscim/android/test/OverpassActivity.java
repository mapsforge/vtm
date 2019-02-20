/*
 * Copyright 2019 Gustl22
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

import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.buildings.S3DBLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Viewport;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.bitmap.DefaultSources;
import org.oscim.tiling.source.overpass.OverpassTileSource;

public class OverpassActivity extends MapActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TileSource tileSource = OverpassTileSource.builder()
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .zoomMin(15)
                .zoomMax(17)
                .build();
        VectorTileLayer l = mMap.setBaseMap(tileSource);

        TileSource bitmapTileSource = DefaultSources.OPENSTREETMAP
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .zoomMax(15)
                .fadeSteps(new BitmapTileLayer.FadeStep[]{
                        new BitmapTileLayer.FadeStep(15, 16, 1f, 0f),
                        new BitmapTileLayer.FadeStep(16, Viewport.MAX_ZOOM_LEVEL, 0f, 0f)
                })
                .build();
        mMap.layers().add(new BitmapTileLayer(mMap, bitmapTileSource));

        BuildingLayer.RAW_DATA = true;
        mMap.setTheme(VtmThemes.DEFAULT);

        mMap.layers().add(new S3DBLayer(mMap, l));
        mMap.layers().add(new LabelLayer(mMap, l));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
