/*
 * Copyright 2016-2017 devemux86
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
package org.oscim.test;

import org.oscim.core.MapPosition;
import org.oscim.gdx.GdxMapApp;
import org.oscim.gdx.poi3d.Poi3DLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.renderer.MapRenderer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

public class Gdx3DTest extends GdxMapApp {

    @Override
    public void createLayers() {
        MapRenderer.setBackgroundColor(0xff888888);

        TileSource ts = OSciMap4TileSource.builder()
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();
        // initDefaultLayers(ts, false, false, false);

        VectorTileLayer mMapLayer = mMap.setBaseMap(ts);
        mMap.setTheme(VtmThemes.DEFAULT);
        // mMap.setTheme(VtmThemes.TRONRENDER);

        mMap.layers().add(new BuildingLayer(mMap, mMapLayer));

        // mMap.getLayers().add(new GenericLayer(mMap, new GridRenderer()));

        // ts = new OSciMap4TileSource("http://opensciencemap.org/tiles/s3db");
        // VectorTileLayer tl = new VectorTileLayer(mMap, 16, 16, 20);
        // tl.setTileSource(ts);
        // tl.setRenderTheme(ThemeLoader.load(VtmThemes.DEFAULT));
        // mMap.getLayers().add(tl);
        // mMap.getLayers().add(new BuildingLayer(mMap, tl.getTileLayer()));

        mMap.layers().add(new Poi3DLayer(mMap, mMapLayer));

        mMap.layers().add(new LabelLayer(mMap, mMapLayer));

        MapPosition pos = MapPreferences.getMapPosition();
        if (pos != null)
            mMap.setMapPosition(pos);
        else
            mMap.setMapPosition(53.1, 8.8, 1 << 15);
    }

    @Override
    public void dispose() {
        MapPreferences.saveMapPosition(mMap.getMapPosition());
        super.dispose();
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new Gdx3DTest());
    }
}
