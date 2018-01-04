/*
 * Copyright 2013 Hannes Janetzek
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

import org.oscim.backend.canvas.Color;
import org.oscim.core.GeometryBuffer.GeometryType;
import org.oscim.core.MapElement;
import org.oscim.core.Tag;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.TileLoader;
import org.oscim.layers.tile.TileManager;
import org.oscim.renderer.bucket.ExtrusionBuckets;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.QueryResult;
import org.oscim.tiling.TileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class S3DBTileLoader extends TileLoader {
    static final Logger log = LoggerFactory.getLogger(S3DBTileLoader.class);

    private static final String OSCIM4_KEY_COLOR = "c";
    private static final String OSCIM4_KEY_MATERIAL = "m";
    private static final int DEFAULT_COLOR = Color.get(255, 254, 252);

    /**
     * current TileDataSource used by this MapTileLoader
     */
    private final ITileDataSource mTileDataSource;

    private float mGroundScale;

    public S3DBTileLoader(TileManager tileManager, TileSource tileSource) {
        super(tileManager);
        mTileDataSource = tileSource.getDataSource();

    }

    @Override
    public void dispose() {
        mTileDataSource.dispose();
    }

    @Override
    public void cancel() {
        mTileDataSource.cancel();
    }

    @Override
    protected boolean loadTile(MapTile tile) {
        mTile = tile;

        try {
            /* query database, which calls process() callback */
            mTileDataSource.query(mTile, this);
        } catch (Exception e) {
            log.debug("{}", e);
            return false;
        }

        return true;
    }

    @Override
    public void process(MapElement element) {
        if (BuildingLayer.get(mTile).buckets == null) {
            mGroundScale = mTile.getGroundScale();
        }

        if (element.type != GeometryType.TRIS) {
            log.debug("wrong type " + element.type);
            return;
        }

        boolean isRoof = element.tags.containsKey(Tag.KEY_ROOF);

        int c = DEFAULT_COLOR;
        if (element.tags.containsKey(OSCIM4_KEY_COLOR)) {
            c = S3DBUtils.getColor(element.tags.getValue(OSCIM4_KEY_COLOR), isRoof);
        } else if (element.tags.containsKey(OSCIM4_KEY_MATERIAL)) {
            c = S3DBUtils.getMaterialColor(element.tags.getValue(OSCIM4_KEY_MATERIAL), isRoof);
        }

        ExtrusionBuckets ebs = BuildingLayer.get(mTile);
        ebs.addMeshElement(element, mGroundScale, c);
    }

    @Override
    public void completed(QueryResult result) {
        super.completed(result);
    }
}
