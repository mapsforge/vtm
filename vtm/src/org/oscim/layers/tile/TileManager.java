/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2018 devemux86
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
package org.oscim.layers.tile;

import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.event.Event;
import org.oscim.event.EventDispatcher;
import org.oscim.event.EventListener;
import org.oscim.layers.tile.MapTile.TileNode;
import org.oscim.map.Map;
import org.oscim.map.Viewport;
import org.oscim.renderer.BufferObject;
import org.oscim.tiling.QueryResult;
import org.oscim.utils.ScanBox;
import org.oscim.utils.quadtree.TileIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

import static org.oscim.layers.tile.MapTile.State.CANCEL;
import static org.oscim.layers.tile.MapTile.State.DEADBEEF;
import static org.oscim.layers.tile.MapTile.State.LOADING;
import static org.oscim.layers.tile.MapTile.State.NEW_DATA;
import static org.oscim.layers.tile.MapTile.State.NONE;
import static org.oscim.layers.tile.MapTile.State.READY;
import static org.oscim.utils.FastMath.clamp;

public class TileManager {
    static final Logger log = LoggerFactory.getLogger(TileManager.class);
    static final boolean dbg = false;

    public final static Event TILE_LOADED = new Event();
    public final static Event TILE_REMOVED = new Event();

    private final int mCacheLimit;
    private int mCacheReduce;

    private int mMinZoom;
    private int mMaxZoom;

    private int[] mZoomTable;

    /**
     * limit number tiles with new data not uploaded to GL
     * TODO this should depend on the number of tiles displayed
     */
    private static final int MAX_TILES_IN_QUEUE = 20;

    /**
     * cache limit threshold
     */
    private static final int CACHE_THRESHOLD = 25;
    private static final int CACHE_CLEAR_THRESHOLD = 10;

    private final Map mMap;
    private final Viewport mViewport;

    /**
     * cache for all tiles
     */
    private MapTile[] mTiles;

    /**
     * actual number of tiles in mTiles
     */
    private int mTilesCount;

    /**
     * current end position in mTiles
     */
    private int mTilesEnd;

    /**
     * counter for tiles with new data not yet loaded to GL
     */
    private int mTilesToUpload;

    /**
     * new tile jobs for MapWorkers
     */
    private final ArrayList<MapTile> mJobs;

    /**
     * counter to check whether current TileSet has changed
     */
    private int mUpdateSerial;

    /**
     * lock for TileSets while updating MapTile locks - still needed?
     */
    private final Object mTilelock = new Object();

    private TileSet mCurrentTiles;
    /* package */ TileSet mNewTiles;

    /**
     * job queue filled in TileManager and polled by TileLoaders
     */
    private final JobQueue jobQueue;

    private final float[] mMapPlane = new float[8];

    private boolean mLoadParent;
    private int mPrevZoomlevel;

    private double mLevelUpThreshold = 1;
    private double mLevelDownThreshold = 2;

    private final TileIndex<TileNode, MapTile> mIndex =
            new TileIndex<TileNode, MapTile>() {
                @Override
                public void removeItem(MapTile t) {
                    if (t.node == null)
                        throw new IllegalStateException("Already removed: " + t);

                    super.remove(t.node);
                    t.node.item = null;
                }

                @Override
                public TileNode create() {
                    return new TileNode();
                }
            };

    public final EventDispatcher<Listener, MapTile> events =
            new EventDispatcher<Listener, MapTile>() {
                @Override
                public void tell(Listener l, Event event, MapTile tile) {
                    l.onTileManagerEvent(event, tile);
                }
            };

    public interface Listener extends EventListener {
        void onTileManagerEvent(Event event, MapTile tile);
    }

    public TileManager(Map map, int cacheLimit) {
        mMap = map;
        mMaxZoom = map.viewport().getMaxZoomLevel();
        mMinZoom = map.viewport().getMinZoomLevel();
        mCacheLimit = cacheLimit;
        mCacheReduce = 0;

        mViewport = map.viewport();

        jobQueue = new JobQueue();
        mJobs = new ArrayList<MapTile>();
        mTiles = new MapTile[mCacheLimit];

        mTilesEnd = 0;
        mTilesToUpload = 0;
        mUpdateSerial = 0;
    }

    public void setZoomTable(int[] zoomTable) {
        mZoomTable = zoomTable;
    }

    /**
     * TESTING: avoid flickering when switching zoom-levels:
     * 1.85, 1.15 seems to work well
     */
    public void setZoomThresholds(float down, float up) {
        mLevelDownThreshold = clamp(down, 1, 2);
        mLevelUpThreshold = clamp(up, 1, 2);
    }

    public MapTile getTile(int x, int y, int z) {
        synchronized (mTilelock) {
            return mIndex.getTile(x, y, z);
        }
    }

    public void init() {
        if (mCurrentTiles != null)
            mCurrentTiles.releaseTiles();

        mIndex.drop();

        /* Pass VBOs and VertexItems back to pools */
        for (int i = 0; i < mTilesEnd; i++) {
            MapTile t = mTiles[i];
            if (t == null)
                continue;

            /* Check if tile is used by another thread */
            if (!t.isLocked())
                t.clear();

            /* In case the tile is still loading or used by
             * another thread: clear when returned from loader
             * or becomes unlocked */
            t.setState(DEADBEEF);
        }

        /* clear references to cached MapTiles */
        Arrays.fill(mTiles, null);
        mTilesEnd = 0;
        mTilesCount = 0;

        /* set up TileSet large enough to hold current tiles, even if window is resized */
        int num = Math.max(mMap.getScreenWidth(), mMap.getScreenHeight());
        int size = Tile.SIZE >> 1;
        int numTiles = (num * num) / (size * size) * 4;

        mNewTiles = new TileSet(numTiles);
        mCurrentTiles = new TileSet(numTiles);
    }

    /**
     * 1. Update mCurrentTiles TileSet of currently visible tiles.
     * 2. Add not yet loaded (or loading) tiles to JobQueue.
     * 3. Manage cache
     *
     * @param pos current MapPosition
     */
    public boolean update(MapPosition pos) {

        // FIXME cant expect init to be called otherwise
        // Should use some onLayerAttached callback instead.
        if (mNewTiles == null || mNewTiles.tiles.length == 0) {
            mPrevZoomlevel = pos.zoomLevel;
            init();
        }
        /* clear JobQueue and set tiles to state == NONE.
         * one could also append new tiles and sort in JobQueue
         * but this has the nice side-effect that MapWorkers dont
         * start with old jobs while new jobs are calculated, which
         * should increase the chance that they are free when new
         * jobs come in. */
        jobQueue.clear();

        if (pos.zoomLevel < mMinZoom) {
            if (mCurrentTiles.cnt > 0 && pos.zoomLevel < mMinZoom - 4) {
                synchronized (mTilelock) {
                    mCurrentTiles.releaseTiles();
                }
            }
            return false;
        }

        int tileZoom = clamp(pos.zoomLevel, mMinZoom, mMaxZoom);

        if (mZoomTable == null) {
            /* greater 1 when zoomed in further than
             * tile zoomlevel, so [1..2] while whithin
             * min/maxZoom */
            double scaleDiv = pos.scale / (1 << tileZoom);
            mLoadParent = scaleDiv < 1.5;

            int zoomDiff = tileZoom - mPrevZoomlevel;
            if (zoomDiff == 1) {
                /* dont switch zoomlevel up yet */
                if (scaleDiv < mLevelUpThreshold) {
                    tileZoom = mPrevZoomlevel;
                    mLoadParent = false;
                }
            } else if (zoomDiff == -1) {
                /* dont switch zoomlevel down yet */
                if (scaleDiv > mLevelDownThreshold) {
                    tileZoom = mPrevZoomlevel;
                    mLoadParent = true;
                }
            }
            //    log.debug("p:{} {}:{}=>{} | {} <> {}", mLoadParent,
            //              mPrevZoomlevel, pos.zoomLevel, tileZoom,
            //              scaleDiv, (pos.scale / (1 << tileZoom)));
        } else {
            mLoadParent = false;
            int match = 0;
            for (int z : mZoomTable) {
                if (z <= tileZoom && z > match)
                    match = z;
            }
            if (match == 0)
                return false;

            tileZoom = match;
        }
        mPrevZoomlevel = tileZoom;

        mViewport.getMapExtents(mMapPlane, Tile.SIZE / 2);

        /* scan visible tiles. callback function calls 'addTile'
         * which updates mNewTiles */
        mNewTiles.cnt = 0;
        mScanBox.scan(pos.x, pos.y, pos.scale, tileZoom, mMapPlane);

        MapTile[] newTiles = mNewTiles.tiles;
        int newCnt = mNewTiles.cnt;

        MapTile[] curTiles = mCurrentTiles.tiles;
        int curCnt = mCurrentTiles.cnt;

        boolean changed = (newCnt != curCnt);

        Arrays.sort(newTiles, 0, newCnt, TileSet.coordComparator);

        if (!changed) {
            /* compare if any tile has changed */
            for (int i = 0; i < newCnt; i++) {
                if (newTiles[i] != curTiles[i]) {
                    changed = true;
                    break;
                }
            }
        }

        if (changed) {
            synchronized (mTilelock) {
                /* lock new tiles */
                mNewTiles.lockTiles();

                /* unlock previous tiles */
                mCurrentTiles.releaseTiles();

                /* swap newTiles with currentTiles */
                TileSet tmp = mCurrentTiles;
                mCurrentTiles = mNewTiles;
                mNewTiles = tmp;

                mUpdateSerial++;
            }

            /* request rendering as tiles changed */
            mMap.render();
        }

        /* Add tile jobs to queue */
        if (mJobs.isEmpty())
            return false;

        MapTile[] jobs = new MapTile[mJobs.size()];
        jobs = mJobs.toArray(jobs);
        updateDistances(jobs, jobs.length, pos);

        /* sets tiles to state == LOADING */
        jobQueue.setJobs(jobs);
        mJobs.clear();

        if (mCacheReduce < mCacheLimit / 2) {
            if (BufferObject.isMaxFill()) {
                mCacheReduce += 10;
                if (dbg)
                    log.debug("reduce cache {}", (mCacheLimit - mCacheReduce));
            } else {
                mCacheReduce = 0;
            }
        }

        /* limit cache items */
        int remove = mTilesCount - (mCacheLimit - mCacheReduce);

        if (remove > CACHE_THRESHOLD || mTilesToUpload > MAX_TILES_IN_QUEUE) {
            synchronized (mTilelock) {
                limitCache(pos, remove);
            }
        }
        return true;
    }

    public void clearJobs() {
        jobQueue.clear();
    }

    public boolean hasTileJobs() {
        return !jobQueue.isEmpty();
    }

    public MapTile getTileJob() {
        return jobQueue.poll();
    }

    /**
     * Retrive a TileSet of current tiles. Tiles remain locked in cache until
     * the set is unlocked by either passing it again to this function or to
     * releaseTiles.
     *
     * @param tileSet to be updated
     * @return true if TileSet has changed
     * @threadsafe
     */
    public boolean getActiveTiles(TileSet tileSet) {
        if (mCurrentTiles == null)
            return false;

        if (tileSet == null)
            return false;

        if (tileSet.serial == mUpdateSerial)
            return false;

        /* do not flip mNew/mCurrentTiles while copying */
        synchronized (mTilelock) {
            tileSet.setTiles(mCurrentTiles);
            tileSet.serial = mUpdateSerial;
        }
        return true;
    }

    MapTile addTile(int x, int y, int zoomLevel) {
        MapTile tile = mIndex.getTile(x, y, zoomLevel);

        if (tile == null) {
            TileNode n = mIndex.add(x, y, zoomLevel);
            tile = n.item = new MapTile(n, x, y, zoomLevel);
            tile.setState(LOADING);
            mJobs.add(tile);
            addToCache(tile);
        } else if (!tile.isActive()) {
            tile.setState(LOADING);
            mJobs.add(tile);
        }

        if (mLoadParent && (zoomLevel > mMinZoom) && (mZoomTable == null)) {
            /* prefetch parent */
            MapTile p = tile.node.parent();
            if (p == null) {
                TileNode n = mIndex.add(x >> 1, y >> 1, zoomLevel - 1);
                p = n.item = new MapTile(n, x >> 1, y >> 1, zoomLevel - 1);
                addToCache(p);
                /* this prevents to add tile twice to queue */
                p.setState(LOADING);
                mJobs.add(p);
            } else if (!p.isActive()) {
                p.setState(LOADING);
                mJobs.add(p);
            }
        }
        return tile;
    }

    private void addToCache(MapTile tile) {

        if (mTilesEnd == mTiles.length) {
            if (mTilesEnd > mTilesCount) {
                TileDistanceSort.sort(mTiles, 0, mTilesEnd);
                /* sorting also repacks the 'sparse' filled array
                 * so end of mTiles is at mTilesCount now */
                mTilesEnd = mTilesCount;
            }

            if (mTilesEnd == mTiles.length) {
                log.debug("realloc tiles {}", mTilesEnd);
                MapTile[] tmp = new MapTile[mTiles.length + 20];
                System.arraycopy(mTiles, 0, tmp, 0, mTilesCount);
                mTiles = tmp;
            }
        }

        mTiles[mTilesEnd++] = tile;
        mTilesCount++;
    }

    private boolean removeFromCache(MapTile t) {
        /* TODO check valid states here:When in CANCEL state tile belongs to
         * TileLoader thread, defer clearing to jobCompleted() */

        if (dbg)
            log.debug("remove from cache {} {} {}",
                    t, t.state(), t.isLocked());

        if (t.isLocked())
            return false;

        if (t.state(NEW_DATA | READY))
            events.fire(TILE_REMOVED, t);

        t.clear();

        mIndex.removeItem(t);
        mTilesCount--;
        return true;
    }

    private void limitCache(MapPosition pos, int remove) {
        MapTile[] tiles = mTiles;

        /* count tiles that have new data */
        int newTileCnt = 0;

        /* remove tiles that were never loaded */
        for (int i = 0; i < mTilesEnd; i++) {
            MapTile t = tiles[i];
            if (t == null)
                continue;

            if (t.state(NEW_DATA))
                newTileCnt++;

            if (t.state(DEADBEEF)) {
                log.debug("found DEADBEEF {}", t);
                t.clear();
                tiles[i] = null;
                continue;
            }

            /* make sure tile cannot be used by GL or MapWorker Thread */
            if (t.state(NONE) && removeFromCache(t)) {
                tiles[i] = null;
                remove--;
            }
        }

        if ((remove < CACHE_CLEAR_THRESHOLD) && (newTileCnt < MAX_TILES_IN_QUEUE))
            return;

        updateDistances(tiles, mTilesEnd, pos);
        TileDistanceSort.sort(tiles, 0, mTilesEnd);

        /* sorting also repacks the 'sparse' filled array
         * so end of mTiles is at mTilesCount now */
        mTilesEnd = mTilesCount;

        /* start with farest away tile */
        for (int i = mTilesCount - 1; i >= 0 && remove > 0; i--) {
            MapTile t = tiles[i];

            /* dont remove tile used by TileRenderer, or somewhere else
             * try again in next run. */
            if (t.isLocked()) {
                if (dbg)
                    log.debug("{} locked (state={}, d={})",
                            t, t.state(), t.distance);
                continue;
            }

            if (t.state(CANCEL)) {
                continue;
            }

            /* cancel loading of tiles that should not even be cached */
            if (t.state(LOADING)) {
                t.setState(CANCEL);
                if (dbg)
                    log.debug("{} canceled (d={})", t, t.distance);
                continue;
            }

            /* clear new and unused tile */
            if (t.state(NEW_DATA)) {
                newTileCnt--;
                if (dbg)
                    log.debug("{} unused (d=({})", t, t.distance);
            }

            if (!t.state(READY | NEW_DATA)) {
                log.error("stuff that should be here! {} {}", t, t.state());
            }

            if (removeFromCache(t)) {
                tiles[i] = null;
                remove--;
            }
        }

        for (int i = mTilesCount - 1; i >= 0 && newTileCnt > MAX_TILES_IN_QUEUE; i--) {
            MapTile t = tiles[i];
            if ((t != null) && (t.state(NEW_DATA))) {
                if (removeFromCache(t)) {
                    tiles[i] = null;
                    newTileCnt--;
                }
            }
        }

        mTilesToUpload = newTileCnt;
    }

    /**
     * Called by TileLoader thread when tile is loaded.
     *
     * @param tile Tile ready for upload in TileRenderLayer
     * @threadsafe
     */
    public void jobCompleted(MapTile tile, QueryResult result) {

        /* send TILE_LOADED event on main-loop */
        mMap.post(new JobCompletedEvent(tile, result));

        /* locked means the tile is visible or referenced by
         * a tile that might be visible. */
        if (tile.isLocked()) {
            if (result == QueryResult.DELAYED && tile.isLocked())
                mMap.updateMap(false);
            else
                mMap.render();
        }
    }

    class JobCompletedEvent implements Runnable {
        final MapTile tile;
        final QueryResult result;

        public JobCompletedEvent(MapTile tile, QueryResult result) {
            this.tile = tile;
            this.result = result;
        }

        @Override
        public void run() {
            if (result == QueryResult.SUCCESS && tile.state(LOADING)) {
                tile.setState(NEW_DATA);
                events.fire(TILE_LOADED, tile);
                mTilesToUpload++;
                return;
            }
            // TODO use mMap.update(true) to retry tile loading?
            log.debug("Load: {} {} state:{}",
                    tile, result,
                    tile.state());

            /* got orphaned tile */
            if (tile.state(DEADBEEF)) {
                tile.clear();
                return;
            }

            tile.clear();
        }
    }

    private static void updateDistances(MapTile[] tiles, int size, MapPosition pos) {
        /* TODO there is probably a better quad-tree distance function */
        int zoom = 20;
        long x = (long) (pos.x * (1 << zoom));
        long y = (long) (pos.y * (1 << zoom));

        for (int i = 0; i < size; i++) {
            MapTile t = tiles[i];
            if (t == null)
                continue;

            int diff = (zoom - t.zoomLevel);
            long dx, dy;

            if (diff == 0) {
                dx = t.tileX - x;
                dy = t.tileY - y;
            } else { // diff > 0
                long mx = x >> diff;
                long my = y >> diff;

                dx = t.tileX - mx;
                dy = t.tileY - my;
            }

            int dz = (pos.zoomLevel - t.zoomLevel);
            if (dz == 0)
                dz = 1;
            else if (dz < -1)
                dz *= 0.75;

            t.distance = (dx * dx + dy * dy) * (dz * dz);
        }
    }

    private final ScanBox mScanBox = new ScanBox() {

        @Override
        protected void setVisible(int y, int x1, int x2) {
            MapTile[] tiles = mNewTiles.tiles;
            int cnt = mNewTiles.cnt;
            int maxTiles = tiles.length;

            int xmax = 1 << mZoom;

            for (int x = x1; x < x2; x++) {
                MapTile tile = null;

                if (cnt == maxTiles) {
                    log.debug("too many tiles {}", maxTiles);
                    break;
                }
                int xx = x;

                if (x < 0 || x >= xmax) {
                    /* flip-around date line */
                    if (x < 0)
                        xx = xmax + x;
                    else
                        xx = x - xmax;

                    if (xx < 0 || xx >= xmax)
                        continue;
                }

                /* check if tile is already added */
                for (int i = 0; i < cnt; i++)
                    if (tiles[i].tileX == xx && tiles[i].tileY == y) {
                        tile = tiles[i];
                        break;
                    }

                if (tile == null) {
                    tile = addTile(xx, y, mZoom);
                    tiles[cnt++] = tile;
                }
            }
            mNewTiles.cnt = cnt;
        }
    };

    public MapTile getTile(int tileX, int tileY, byte zoomLevel) {
        return mIndex.getTile(tileX, tileY, zoomLevel);
    }

    public void setZoomLevel(int zoomLevelMin, int zoomLevelMax) {
        mMinZoom = zoomLevelMin;
        mMaxZoom = zoomLevelMax;
    }
}
