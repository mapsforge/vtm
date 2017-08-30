/*
 * Copyright 2016-2017 devemux86
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
package org.oscim.test.jeo;

import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.renderer.MapRenderer;
import org.oscim.theme.carto.RenderTheme;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

public class ThemeTest extends GdxMapApp {

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new ThemeTest(), null, 256);
    }

    @Override
    public void createLayers() {
        UrlTileSource ts = new OSciMap4TileSource();

        VectorTileLayer l = mMap.setBaseMap(ts);

        l.setRenderTheme(new RenderTheme());

        MapRenderer.setBackgroundColor(0xffcccccc);

        // mMap.getLayers().add(new LabelLayer(mMap,
        // mMapLayer.getTileLayer()));
        // mMap.getLayers().add(new JeoMapLayer(mMap));
    }
}
