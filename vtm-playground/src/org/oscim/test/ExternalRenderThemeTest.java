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
package org.oscim.test;

import com.badlogic.gdx.Input;

import org.oscim.gdx.GdxMap;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.renderer.MapRenderer;
import org.oscim.theme.IRenderTheme;
import org.oscim.theme.IRenderTheme.ThemeException;
import org.oscim.theme.ThemeLoader;
import org.oscim.tiling.source.mapfile.MapFileTileSource;

public class ExternalRenderThemeTest extends GdxMap {

    VectorTileLayer mapLayer;

    @Override
    protected boolean onKeyDown(int keycode) {
        String name = null;
        if (keycode == Input.Keys.NUM_1)
            name = "themes/freizeitkarte/theme.xml";
        if (keycode == Input.Keys.NUM_2)
            name = "themes/elevate/theme.xml";
        if (keycode == Input.Keys.NUM_3)
            name = "themes/vmap/theme.xml";

        if (name == null)
            return false;

        try {
            IRenderTheme theme = ThemeLoader.load(name);
            mapLayer.setRenderTheme(theme);
            MapRenderer.setBackgroundColor(theme.getMapBackground());
        } catch (ThemeException e) {
            e.printStackTrace();
        }

        mMap.clearMap();
        mMap.updateMap(true);
        return true;
    }

    @Override
    public void createLayers() {
        mMap.setMapPosition(53.08, 8.83, 1 << 14);

        // TileSource tileSource = new OSciMap4TileSource();

        MapFileTileSource tileSource = new MapFileTileSource();
        // tileSource.setMapFile("/home/jeff/src/vtm/Freizeitkarte_DEU_NW.map");
        tileSource.setMapFile("/home/jeff/germany.map");

        VectorTileLayer l = mMap.setBaseMap(tileSource);
        mapLayer = l;

        // mMap.getLayers().add(new BuildingLayer(mMap, l.getTileLayer()));
        mMap.layers().add(new LabelLayer(mMap, l));

        try {
            IRenderTheme theme = ThemeLoader
                    .load("themes/freizeitkarte/theme.xml");
            // IRenderTheme theme =
            // ThemeLoader.load("themes/elevate/theme.xml");
            // IRenderTheme theme = ThemeLoader.load("themes/vmap/theme.xml");
            l.setRenderTheme(theme);
            MapRenderer.setBackgroundColor(theme.getMapBackground());
        } catch (ThemeException e) {
            e.printStackTrace();
        }

        // mMap.getLayers().add(new GenericLayer(mMap, new MeshRenderer()));
        // mMap.getLayers().add(new GenericLayer(mMap, new GridRenderer()));
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new ExternalRenderThemeTest(), null, 256);
    }
}
