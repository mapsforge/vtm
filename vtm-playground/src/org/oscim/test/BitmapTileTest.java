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
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.renderer.MapRenderer;
import org.oscim.tiling.source.bitmap.DefaultSources;

public class BitmapTileTest extends GdxMapApp {

    private BitmapTileLayer mLayer = null;
    private BitmapTileLayer mShaded = null;

    @Override
    protected boolean onKeyDown(int keycode) {
        if (keycode == Input.Keys.NUM_1) {
            mMap.layers().remove(mShaded);
            mShaded = null;
            mMap.layers().remove(mLayer);
            mLayer = new BitmapTileLayer(mMap, DefaultSources.OPENSTREETMAP.build());
            mMap.layers().add(mLayer);
            mMap.clearMap();
            return true;
        } else if (keycode == Input.Keys.NUM_2) {
            mMap.layers().remove(mShaded);
            mShaded = null;
            mMap.layers().remove(mLayer);
            mLayer = new BitmapTileLayer(mMap, DefaultSources.STAMEN_TONER.build());
            mMap.layers().add(mLayer);
            mMap.clearMap();
            return true;
        } else if (keycode == Input.Keys.NUM_3) {
            if (mShaded != null) {
                mMap.layers().remove(mShaded);
                mShaded = null;
            } else {
                mShaded = new BitmapTileLayer(mMap, DefaultSources.HIKEBIKE_HILLSHADE.build());
                mMap.layers().add(mShaded);
            }
            mMap.clearMap();
            return true;
        }

        return false;
    }

    @Override
    public void createLayers() {
        MapRenderer.setBackgroundColor(0xff888888);

        mLayer = new BitmapTileLayer(mMap, DefaultSources.OPENSTREETMAP.build());
        mMap.layers().add(mLayer);

    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new BitmapTileTest(), null, 256);
    }
}
