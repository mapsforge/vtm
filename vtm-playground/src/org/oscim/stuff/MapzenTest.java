/*
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
package org.oscim.stuff;

import com.badlogic.gdx.Input;

import org.oscim.gdx.GdxMap;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.IRenderTheme.ThemeException;
import org.oscim.theme.StreamRenderTheme;
import org.oscim.theme.ThemeLoader;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

public class MapzenTest extends GdxMap {

    @Override
    protected boolean onKeyDown(int keycode) {
        if (keycode == Input.Keys.A) {
            loadTheme();
        }

        return super.onKeyDown(keycode);
    }

    @Override
    public void createLayers() {
        UrlTileSource tileSource = OSciMap4TileSource.builder()
                .url("https://vector.mapzen.com/osm/v0.8/all")
                .apiKey("vector-tiles-xxxxxxx") // Put a proper API key
                .zoomMax(18)
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();

        VectorTileLayer l = mMap.setBaseMap(tileSource);

        loadTheme();

        mMap.layers().add(new BuildingLayer(mMap, l));
        mMap.layers().add(new LabelLayer(mMap, l));

        mMap.setMapPosition(53.08, 8.82, 1 << 17);
    }

    private void loadTheme() {
        try {
            mMap.setTheme(ThemeLoader.load(new StreamRenderTheme("", getClass().getResourceAsStream("/assets/styles/mapzen.xml"))));
        } catch (ThemeException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new MapzenTest(), null, 400);
    }
}
