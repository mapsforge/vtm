/*
 * Copyright 2014-2015 Hannes Janetzek
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
package org.oscim.layers.tile.buildings;

import org.oscim.backend.canvas.Color;
import org.oscim.core.GeometryBuffer;
import org.oscim.core.GeometryBuffer.GeometryType;
import org.oscim.core.MapElement;
import org.oscim.core.MercatorProjection;
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
        mTilePlane.tags.add(new Tag("c", "transparent"));
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
        double lat = MercatorProjection.toLatitude(tile.y);
        mGroundScale = (float) MercatorProjection
                .groundResolutionWithScale(lat, 1 << mTile.zoomLevel);

        mRoofs = new ExtrusionBucket(0, mGroundScale, Color.get(247, 249, 250));

        mParts = new ExtrusionBucket(0, mGroundScale, Color.get(255, 254, 252));
        //mRoofs = new ExtrusionLayer(0, mGroundScale, Color.get(207, 209, 210));
        mRoofs.next = mParts;

        BuildingLayer.get(tile).setBuckets(mRoofs);

        process(mTilePlane);
    }

    String COLOR_KEY = "c";
    String MATERIAL_KEY = "m";
    String ROOF_KEY = "roof";
    String ROOF_SHAPE_KEY = "roof:shape";

    @Override
    public void process(MapElement element) {

        if (element.type != GeometryType.TRIS) {
            log.debug("wrong type " + element.type);
            return;
        }

        if (mParts == null)
            initTile(mTile);

        boolean isRoof = element.tags.containsKey(ROOF_KEY);
        //if (isRoof)
        //    log.debug(element.tags.toString());

        int c = 0;
        if (element.tags.containsKey(COLOR_KEY)) {
            c = S3DBRenderer.getColor(element.tags.getValue(COLOR_KEY), isRoof);
        }

        if (c == 0 && element.tags.containsKey(MATERIAL_KEY)) {
            c = S3DBRenderer.getMaterialColor(element.tags.getValue(MATERIAL_KEY), isRoof);
        }

        if (c == 0) {
            String roofShape = element.tags.getValue(ROOF_SHAPE_KEY);

            if (isRoof && (roofShape == null || "flat".equals(roofShape)))
                mRoofs.add(element);
            else
                mParts.add(element);
            return;
        }

        for (ExtrusionBucket l = mParts; l != null; l = l.next()) {
            if (l.color == c) {
                l.add(element);
                return;
            }
        }
        ExtrusionBucket l = new ExtrusionBucket(0, mGroundScale, c);

        l.next = mParts.next;
        mParts.next = l;

        l.add(element);
    }

    @Override
    public void completed(QueryResult result) {

        mParts = null;
        mRoofs = null;

        super.completed(result);
    }
}
