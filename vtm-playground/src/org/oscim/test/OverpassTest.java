/*
 * Copyright 2016-2020 devemux86
 * Copyright 2019 Gustl22
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
import org.oscim.layers.GroupLayer;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.buildings.S3DBLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Map;
import org.oscim.map.Viewport;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;
import org.oscim.tiling.source.overpass.OverpassTileSource;

import java.util.Collections;

/**
 * Use Overpass API data for vector layer.
 * Only for developing as can be error-prone.
 * Take care of overpass provider licenses.
 */
public class OverpassTest extends GdxMapApp {

    @Override
    public void createLayers() {
        Map map = getMap();

        TileSource tileSource = OverpassTileSource.builder()
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .zoomMin(15)
                .zoomMax(17)
                .build();
        VectorTileLayer l = map.setBaseMap(tileSource);

        UrlTileSource bitmapTileSource = DefaultSources.OPENSTREETMAP
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .zoomMax(15)
                .fadeSteps(new BitmapTileLayer.FadeStep[]{
                        new BitmapTileLayer.FadeStep(15, 16, 1f, 0f),
                        new BitmapTileLayer.FadeStep(16, Viewport.MAX_ZOOM_LEVEL, 0f, 0f)
                })
                .build();
        bitmapTileSource.setHttpRequestHeaders(Collections.singletonMap("User-Agent", "vtm-playground"));
        mMap.layers().add(new BitmapTileLayer(mMap, bitmapTileSource));

        BuildingLayer.RAW_DATA = true;
        GroupLayer groupLayer = new GroupLayer(mMap);
        groupLayer.layers.add(new S3DBLayer(map, l));
        groupLayer.layers.add(new LabelLayer(map, l));
        map.layers().add(groupLayer);

        map.setTheme(VtmThemes.DEFAULT);
        MapPosition pos = MapPreferences.getMapPosition();
        if (pos != null)
            map.setMapPosition(pos);
        else
            map.setMapPosition(53.075, 8.808, 1 << 17);
    }

    @Override
    public void dispose() {
        MapPreferences.saveMapPosition(mMap.getMapPosition());
        super.dispose();
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new OverpassTest());
    }
}
