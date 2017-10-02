/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2016 Andrey Novikov
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
package org.oscim.layers.tile.buildings;

import org.oscim.layers.tile.TileLayer;
import org.oscim.layers.tile.TileManager;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.map.Map;
import org.oscim.tiling.TileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3DBLayer extends TileLayer {
    static final Logger log = LoggerFactory.getLogger(S3DBLayer.class);

    private final static int MAX_CACHE = 32;

    private final static int MIN_ZOOM = 16;
    private final static int MAX_ZOOM = 16;

    /* TODO get from theme */

    private final TileSource mTileSource;

    public S3DBLayer(Map map, TileSource tileSource) {
        this(map, tileSource, true, false);
    }

    /**
     * Simple-3D-Buildings Layer
     *
     * @param map        Stored map workaround
     * @param tileSource Source of loaded tiles in {@link VectorTileLayer}
     * @param fxaa       Switch on Fast Approximate Anti-Aliasing
     * @param ssao       Switch on Screen Space Ambient Occlusion
     */
    public S3DBLayer(Map map, TileSource tileSource, boolean fxaa, boolean ssao) {
        super(map, new TileManager(map, MAX_CACHE));
        setRenderer(new S3DBRenderer(MIN_ZOOM, MAX_ZOOM, fxaa, ssao));

        mTileManager.setZoomLevel(MIN_ZOOM, MAX_ZOOM);
        mTileSource = tileSource;
        initLoader(2);
    }

    @Override
    protected S3DBTileLoader createLoader() {
        return new S3DBTileLoader(getManager(), mTileSource);
    }
}
