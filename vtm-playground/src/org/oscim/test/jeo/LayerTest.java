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

import org.jeo.map.Style;
import org.jeo.vector.VectorDataset;
import org.oscim.gdx.GdxMap;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.JeoVectorLayer;
import org.oscim.layers.OSMIndoorLayer;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.test.JeoTest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static org.oscim.tiling.source.bitmap.DefaultSources.STAMEN_TONER;

public class LayerTest extends GdxMap {

    private String PATH = "https://gist.github.com/anonymous/8960337/raw/overpass.geojson";

    private OSMIndoorLayer mIndoorLayer;

    @Override
    public void createLayers() {
        mMap.setBaseMap(new BitmapTileLayer(mMap, STAMEN_TONER.build()));

        mMap.addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(PATH);
                    URLConnection conn = url.openConnection();
                    InputStream is = conn.getInputStream();

                    VectorDataset data = JeoTest.readGeoJson(is);
                    Style style = JeoTest.getStyle();
                    mIndoorLayer = new OSMIndoorLayer(mMap, data, style);
                    mIndoorLayer.activeLevels[0] = true;
                    mIndoorLayer.activeLevels[1] = true;
                    mIndoorLayer.activeLevels[2] = true;
                    mIndoorLayer.activeLevels[3] = true;

                    mMap.layers().add(new JeoVectorLayer(mMap, data, style));
                    mMap.layers().add(mIndoorLayer);

                    mMap.updateMap(true);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mMap.setMapPosition(53.5620092, 9.9866457, 1 << 16);

        //VectorDataset data = (VectorDataset) JeoTest.getJsonData("states.json", true);
        //Style style = JeoTest.getStyle();
        //mMap.layers().add(new JeoVectorLayer(mMap, data, style));

    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new LayerTest(), null, 256);
    }
}
