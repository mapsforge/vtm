/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2017-2019 devemux86
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

import org.oscim.core.BoundingBox;
import org.oscim.gdx.lwjgl.GdxMapApp;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.renderer.MapRenderer;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.bitmap.DefaultSources;

public class AnimatorTest extends GdxMapApp {

    @Override
    public void createLayers() {
        MapRenderer.setBackgroundColor(0xff000000);

        TileSource tileSource = DefaultSources.OPENSTREETMAP
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();
        mMap.layers().add(new BitmapTileLayer(mMap, tileSource));

        mMap.setMapPosition(0, 0, 1 << 4);

    }

    @Override
    protected boolean onKeyDown(int keycode) {
        if (keycode == Input.Keys.NUM_1) {
            mMap.animator().animateTo(new BoundingBox(53.1, 8.8, 53.2, 8.9));
            mMap.updateMap(true);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new AnimatorTest(), null, 256);
    }
}
