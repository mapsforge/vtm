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

import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.tile.buildings.S3DBTileLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

public class S3DBTileLayerTest extends GdxMapApp {

    @Override
    public void createLayers() {
        mMap.setBaseMap(new OSciMap4TileSource());
        mMap.setTheme(VtmThemes.DEFAULT);

        TileSource ts = OSciMap4TileSource
                .builder()
                .url("http://opensciencemap.org/tiles/s3db")
                .build();

        S3DBTileLayer tl = new S3DBTileLayer(mMap, ts);
        mMap.layers().add(tl);

        mMap.setMapPosition(53.08, 8.82, 1 << 17);

    }

    public static void main(String[] args) {
        init();
        run(new S3DBTileLayerTest());
    }
}
