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
package org.oscim.test;

import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint.Cap;
import org.oscim.core.MapPosition;
import org.oscim.gdx.GdxMapApp;
import org.oscim.gdx.GdxMapImpl;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.persistence.PersistenceUtils;
import org.oscim.theme.RenderTheme;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

public class ThemeBuilderTest extends GdxMapImpl {

    static class MyTheme extends ThemeBuilder {
        public MyTheme() {
            rules(
                    matchKeyValue("natural", "water")
                            .style(area(Color.BLUE)),

                    matchKeyValue("landuse", "forest")
                            .style(area(Color.GREEN)),

                    matchKeyValue("landuse", "residential")
                            .style(area(Color.LTGRAY)),

                    matchKey("highway")
                            .rules(matchValue("residential")
                                    .style(line(Color.DKGRAY, 1.2f),
                                            line(Color.WHITE, 1.1f)
                                                    .cap(Cap.ROUND)))

                            .style(line(Color.BLACK, 1)
                                    .blur(0.5f)));
        }
    }

    @Override
    public void createLayers() {

        TileSource tileSource = OSciMap4TileSource.builder()
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();
        VectorTileLayer l = mMap.setBaseMap(tileSource);

        RenderTheme t = new MyTheme().build();

        mMap.setTheme(t);
        //mMap.setTheme(VtmThemes.DEFAULT);

        mMap.layers().add(new LabelLayer(mMap, l));

        MapPosition position = mMap.getMapPosition();
        PersistenceUtils.loadMapPosPrefs(position);
        mMap.setMapPosition(position);

    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new ThemeBuilderTest());
    }
}
