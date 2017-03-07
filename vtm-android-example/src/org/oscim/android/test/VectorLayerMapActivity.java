/*
 * Copyright 2013 Hannes Janetzek
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
package org.oscim.android.test;

import android.os.Bundle;

import org.oscim.backend.canvas.Color;
import org.oscim.layers.vector.VectorLayer;
import org.oscim.layers.vector.geometries.PointDrawable;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.utils.ColorUtil;

public class VectorLayerMapActivity extends SimpleMapActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VectorLayer vectorLayer = new VectorLayer(mMap);

        //    Geometry g = new GeomBuilder()
        //        .point(8.8, 53.1)
        //        .point()
        //        .buffer(1)
        //        .get();
        //
        //    vectorLayer.add(new PolygonDrawable(g, defaultStyle()));
        //
        //    vectorLayer.add(new PointDrawable(53.1, 8.8, Style.builder()
        //        .setBuffer(0.5)
        //        .setFillColor(Color.RED)
        //        .setFillAlpha(0.2)
        //        .build()));
        //
        //    Style.Builder sb = Style.builder()
        //        .setBuffer(0.5)
        //        .setFillColor(Color.RED)
        //        .setFillAlpha(0.2);
        //
        //    Style style = sb.setFillAlpha(0.2).build();
        //
        //    int tileSize = 5;
        //    for (int x = -180; x < 180; x += tileSize) {
        //        for (int y = -90; y < 90; y += tileSize) {
        //            //    Style style = sb.setFillAlpha(FastMath.clamp(FastMath.length(x, y) / 180, 0.2, 1))
        //            //            .build();
        //
        //            vectorLayer.add(new RectangleDrawable(FastMath.clamp(y, -85, 85), x,
        //                                                  FastMath.clamp(y + tileSize - 0.1, -85, 85),
        //                                                  x + tileSize - 0.1, style));
        //
        //        }
        //    }

        Style.Builder sb = Style.builder()
                .buffer(0.5)
                .fillColor(Color.RED)
                .fillAlpha(0.2f);

        for (int i = 0; i < 2000; i++) {
            Style style = sb.buffer(Math.random() + 0.2)
                    .fillColor(ColorUtil.setHue(Color.RED,
                            (int) (Math.random() * 50) / 50.0))
                    .fillAlpha(0.5f)
                    .build();

            vectorLayer.add(new PointDrawable(Math.random() * 180 - 90,
                    Math.random() * 360 - 180,
                    style));

        }
        vectorLayer.update();

        mMap.layers().add(vectorLayer);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* ignore saved position */
        mMap.setMapPosition(0, 0, 1 << 2);
    }
}
