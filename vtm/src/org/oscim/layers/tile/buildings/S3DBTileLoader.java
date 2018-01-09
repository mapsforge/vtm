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
import org.oscim.core.GeometryBuffer;
import org.oscim.core.GeometryBuffer.GeometryType;
import org.oscim.core.MapElement;
import org.oscim.core.Tag;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.TileLoader;
import org.oscim.layers.tile.TileManager;
import org.oscim.renderer.bucket.ExtrusionBucket;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.QueryResult;
import org.oscim.tiling.TileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class S3DBTileLoader extends TileLoader {
    static final Logger log = LoggerFactory.getLogger(S3DBTileLoader.class);

    private static final String OSCIM4_KEY_COLOR = "c";
    private static final String OSCIM4_KEY_MATERIAL = "m";

    /**
     * current TileDataSource used by this MapTileLoader
     */
    private final ITileDataSource mTileDataSource;

    private ExtrusionBucket mParts;
    private ExtrusionBucket mRoofs;

    private float mGroundScale;

    static MapElement mTilePlane = new MapElement();

    static {
        mTilePlane = new MapElement();
        GeometryBuffer g = mTilePlane;
        g.type = GeometryType.TRIS;
        g.points = new float[]{
                0, 0, 0,
                4096, 0, 0,
                0, 4096, 0,
                4096, 4096, 0};
        g.index = new int[]{0, 1, 2, 2, 1, 3};
        mTilePlane.tags.add(new Tag(OSCIM4_KEY_COLOR, "transparent"));
    }

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

    private void initTile(MapTile tile) {
        mGroundScale = tile.getGroundScale();

        mRoofs = new ExtrusionBucket(0, mGroundScale, Color.get(247, 249, 250));

        mParts = new ExtrusionBucket(0, mGroundScale, Color.get(255, 254, 252));
        //mRoofs = new ExtrusionLayer(0, mGroundScale, Color.get(207, 209, 210));
        mRoofs.next = mParts;

        BuildingLayer.get(tile).resetBuckets(mRoofs);

        process(mTilePlane);
    }

    @Override
    public void process(MapElement element) {

        if (element.type != GeometryType.TRIS) {
            log.debug("wrong type " + element.type);
            return;
        }

        if (mParts == null)
            initTile(mTile);

        boolean isRoof = element.tags.containsKey(Tag.KEY_ROOF);
        //if (isRoof)
        //    log.debug(element.tags.toString());

        int c = 0;
        if (element.tags.containsKey(OSCIM4_KEY_COLOR)) {
            c = S3DBUtils.getColor(element.tags.getValue(OSCIM4_KEY_COLOR), isRoof, true);
        }

        if (c == 0 && element.tags.containsKey(OSCIM4_KEY_MATERIAL)) {
            c = S3DBUtils.getMaterialColor(element.tags.getValue(OSCIM4_KEY_MATERIAL), isRoof);
        }

        if (c == 0) {
            String roofShape = element.tags.getValue(Tag.KEY_ROOF_SHAPE);

            if (isRoof && (roofShape == null || Tag.VALUE_FLAT.equals(roofShape)))
                mRoofs.addMesh(element);
            else
                mParts.addMesh(element);
            return;
        }

        // May replace with ExtrusionBucket.addMeshElement()
        for (ExtrusionBucket l = mParts; l != null; l = l.next()) {
            if (l.getColor() == c) {
                l.addMesh(element);
                return;
            }
        }
        ExtrusionBucket l = new ExtrusionBucket(0, mGroundScale, c);

        l.next = mParts.next;
        mParts.next = l;

        l.addMesh(element);
    }

    @Override
    public void completed(QueryResult result) {

        mParts = null;
        mRoofs = null;

        super.completed(result);
    }
}
