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
package org.oscim.android.test;

import android.os.Bundle;

import org.oscim.android.cache.TileCache;
import org.oscim.layers.tile.buildings.S3DBLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

public class S3DBMapActivity extends BaseMapActivity {

    TileCache mS3dbCache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMap.setTheme(VtmThemes.DEFAULT);

        TileSource ts = OSciMap4TileSource.builder()
                .url("http://opensciencemap.org/tiles/s3db")
                .zoomMin(16)
                .zoomMax(16)
                .build();

        if (USE_CACHE) {
            mS3dbCache = new TileCache(this, null, "s3db.db");
            mS3dbCache.setCacheSize(512 * (1 << 10));
            ts.setCache(mS3dbCache);
        }

        mMap.layers().add(new S3DBLayer(mMap, ts));
        mMap.layers().add(new LabelLayer(mMap, mBaseLayer));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mS3dbCache != null)
            mS3dbCache.dispose();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* ignore saved position */
        mMap.setMapPosition(53.5620092, 9.9866457, 1 << 16);
    }
}
