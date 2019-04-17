/*
 * Copyright 2016-2018 devemux86
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

import org.oscim.backend.canvas.Color;
import org.oscim.gdx.lwjgl.GdxMapApp;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.layers.vector.VectorLayer;
import org.oscim.layers.vector.geometries.PointDrawable;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.bitmap.DefaultSources;
import org.oscim.utils.ColorUtil;

public class VectorLayerTest extends GdxMapApp {

    @Override
    public void createLayers() {
        TileSource tileSource = DefaultSources.OPENSTREETMAP
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();
        mMap.layers().add(new BitmapTileLayer(mMap, tileSource));

        mMap.setMapPosition(0, 0, 1 << 2);

        VectorLayer vectorLayer = new VectorLayer(mMap);

        //        vectorLayer.add(new PointDrawable(0, 180, Style.builder()
        //            .setBuffer(10)
        //            .setFillColor(Color.RED)
        //            .setFillAlpha(0.5)
        //            .build()));
        //
        //        Geometry g = new GeomBuilder()
        //            .point(180, 0)
        //            .point()
        //            .buffer(6)
        //            .get();
        //
        //        vectorLayer.add(new PolygonDrawable(g, defaultStyle()));
        //

        Style.Builder sb = Style.builder()
                .buffer(0.4)
                .fillColor(Color.RED)
                .fillAlpha(0.2f);

        //        int tileSize = 5;
        //        for (int x = -180; x < 200; x += tileSize) {
        //            for (int y = -90; y < 90; y += tileSize) {
        //                //    Style style = sb.setFillAlpha(FastMath.clamp(FastMath.length(x, y) / 180, 0.2, 1))
        //                //            .build();
        //
        //                vectorLayer.add(new RectangleDrawable(FastMath.clamp(y, -85, 85), x,
        //                                                      FastMath.clamp(y + tileSize - 0.1, -85, 85),
        //                                                      x + tileSize - 0.1, style));
        //
        //            }
        //        }

        for (int i = 0; i < 1000; i++) {
            Style style = sb.buffer(Math.random() * 1)
                    .fillColor(ColorUtil.setHue(Color.RED,
                            Math.random()))
                    .fillAlpha(0.5f)
                    .build();

            vectorLayer.add(new PointDrawable(Math.random() * 180 - 90,
                    Math.random() * 360 - 180,
                    style));

        }
        vectorLayer.update();

        mMap.layers().add(vectorLayer);
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new VectorLayerTest());
    }
}
