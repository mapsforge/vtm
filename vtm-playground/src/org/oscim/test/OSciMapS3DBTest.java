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

import org.oscim.core.MapPosition;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.tile.buildings.S3DBTileLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.persistence.PersistenceUtils;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

public class OSciMapS3DBTest extends GdxMapApp {

    @Override
    public void createLayers() {
        TileSource tileSource = OSciMap4TileSource.builder()
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();
        VectorTileLayer l = mMap.setBaseMap(tileSource);
        mMap.setTheme(VtmThemes.DEFAULT);

        TileSource ts = OSciMap4TileSource.builder()
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .url("http://opensciencemap.org/tiles/s3db")
                .zoomMin(16)
                .zoomMax(16)
                .build();

        S3DBTileLayer tl = new S3DBTileLayer(mMap, ts);
        mMap.layers().add(tl);
        mMap.layers().add(new LabelLayer(mMap, l));

        MapPosition position = mMap.getMapPosition();
        PersistenceUtils.loadMapPosPrefs(position);
        mMap.setMapPosition(position);
    }

    public static void main(String[] args) {
        init();
        run(new OSciMapS3DBTest());
    }
}
