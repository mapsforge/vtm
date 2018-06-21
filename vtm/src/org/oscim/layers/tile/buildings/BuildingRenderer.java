/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2018 devemux86
 * Copyright 2018 Gustl22
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

import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.TileDistanceSort;
import org.oscim.layers.tile.TileRenderer;
import org.oscim.layers.tile.TileSet;
import org.oscim.layers.tile.ZoomLimiter;
import org.oscim.renderer.ExtrusionRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.MapRenderer;
import org.oscim.renderer.bucket.ExtrusionBuckets;
import org.oscim.renderer.bucket.RenderBuckets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static java.lang.System.currentTimeMillis;
import static org.oscim.layers.tile.MapTile.State.NEW_DATA;
import static org.oscim.layers.tile.MapTile.State.READY;
import static org.oscim.utils.FastMath.clamp;

public class BuildingRenderer extends ExtrusionRenderer {
    static final Logger log = LoggerFactory.getLogger(BuildingRenderer.class);

    private final TileRenderer mTileRenderer;
    private final TileSet mTileSet;

    private final ZoomLimiter mZoomLimiter;

    private final float mFadeInTime = 250;
    private final float mFadeOutTime = 400;

    private long mAnimTime;
    private boolean mShow;

    public BuildingRenderer(TileRenderer tileRenderer, ZoomLimiter zoomLimiter,
                            boolean mesh, boolean drawCovered) {
        super(mesh, drawCovered);

        mZoomLimiter = zoomLimiter;
        mTileRenderer = tileRenderer;
        mTileSet = new TileSet();
    }

    @Override
    public boolean setup() {
        mAlpha = 0;
        return super.setup();

    }

    @Override
    public void update(GLViewport v) {

        int diff = (v.pos.zoomLevel - mZoomLimiter.getMinZoom());

        /* if below min zoom or already faded out */
        if (diff < -1) {
            mAlpha = 0;
            mShow = false;
            setReady(false);
            return;
        }

        if (diff >= 0) {
            if (mAlpha < 1) {
                long now = currentTimeMillis();
                if (!mShow)
                    mAnimTime = now - (long) (mAlpha * mFadeInTime);

                mShow = true;
                mAlpha = clamp((now - mAnimTime) / mFadeInTime, 0, 1);
                MapRenderer.animate();
            }
        } else {
            if (mAlpha > 0) {
                long now = currentTimeMillis();
                if (mShow)
                    mAnimTime = now - (long) ((1 - mAlpha) * mFadeOutTime);

                mShow = false;
                mAlpha = clamp(1 - (now - mAnimTime) / mFadeOutTime, 0, 1);
                MapRenderer.animate();
            }
        }

        if (mAlpha == 0) {
            setReady(false);
            return;
        }

        Integer zoom = mTileRenderer.getVisibleTiles(mTileSet, true);

        if (mTileSet.cnt == 0 || zoom == null) {
            mTileRenderer.releaseTiles(mTileSet);
            setReady(false);
            return;
        }

        MapTile[] tiles = mTileSet.tiles;
        TileDistanceSort.sort(tiles, 0, mTileSet.cnt);

        /* keep a list of tiles available for rendering */
        int maxTiles = mTileSet.cnt * 4;
        if (mExtrusionBucketSet.length < maxTiles)
            mExtrusionBucketSet = new ExtrusionBuckets[maxTiles];

        /* compile one tile max per frame */
        boolean compiled = false;

        int activeTiles = 0;

        if (zoom >= mZoomLimiter.getMinZoom() && zoom <= mZoomLimiter.getZoomLimit()) {
            /* TODO - if tile is not available try parent or children */

            for (int i = 0; i < mTileSet.cnt; i++) {
                ExtrusionBuckets ebs = getBuckets(tiles[i]);
                if (ebs == null)
                    continue;

                if (ebs.compiled)
                    mExtrusionBucketSet[activeTiles++] = ebs;
                else if (!compiled && ebs.compile()) {
                    mExtrusionBucketSet[activeTiles++] = ebs;
                    compiled = true;
                }
            }
        } else if (zoom > mZoomLimiter.getZoomLimit() && zoom <= mZoomLimiter.getMaxZoom()) {
            // render from zoom limit tiles (avoid duplicates and null)
            Set<MapTile> hashTiles = new HashSet<>();
            for (int i = 0; i < mTileSet.cnt; i++) {
                MapTile t = mZoomLimiter.getTile(tiles[i]);
                if (t == null)
                    continue;
                if (!hashTiles.add(t))
                    continue;

                ExtrusionBuckets ebs = getBuckets(t);
                if (ebs == null)
                    continue;

                if (ebs.compiled)
                    mExtrusionBucketSet[activeTiles++] = ebs;

                else if (!compiled && ebs.compile()) {
                    mExtrusionBucketSet[activeTiles++] = ebs;
                    compiled = true;
                }
            }
        } else if (zoom == mZoomLimiter.getMinZoom() - 1) {
            /* check if proxy children are ready */
            for (int i = 0; i < mTileSet.cnt; i++) {
                MapTile t = tiles[i];
                for (byte j = 0; j < 4; j++) {
                    if (!t.hasProxy(1 << j))
                        continue;

                    MapTile c = t.node.child(j);
                    ExtrusionBuckets eb = getBuckets(c);

                    if (eb == null || !eb.compiled)
                        continue;

                    mExtrusionBucketSet[activeTiles++] = eb;
                }
            }
        }

        /* load more tiles on next frame */
        if (compiled)
            MapRenderer.animate();

        mBucketsCnt = activeTiles;

        //log.debug("active tiles: {}", mExtrusionLayerCnt);

        if (activeTiles == 0) {
            mTileRenderer.releaseTiles(mTileSet);
            setReady(false);
            return;
        }
        setReady(true);
    }

    @Override
    public void render(GLViewport v) {
        super.render(v);

        /* release lock on tile data */
        mTileRenderer.releaseTiles(mTileSet);
    }

    private static ExtrusionBuckets getBuckets(MapTile t) {
        RenderBuckets buckets = t.getBuckets();
        if (buckets != null && !t.state(READY | NEW_DATA))
            return null;

        return BuildingLayer.get(t);
    }
}
