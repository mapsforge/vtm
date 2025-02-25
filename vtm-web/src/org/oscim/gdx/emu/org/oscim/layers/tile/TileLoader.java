/*
 * Copyright 2013 Hannes Janetzek
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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.MapElement;
import org.oscim.renderer.MapRenderer;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.QueryResult;

import static org.oscim.tiling.QueryResult.FAILED;

public abstract class TileLoader implements ITileDataSink {

    private final TileManager mTileManager;
    private Timer mTimer;

    public TileLoader(TileManager tileManager) {
        if (mTimer == null)
            mTimer = new Timer();

        mTileManager = tileManager;
    }

    public abstract void dispose();

    protected abstract boolean loadTile(MapTile tile);

    boolean isInterrupted;

    public void finish() {
        isInterrupted = true;
        // cancel loading
    }

    public void cancel() {
        isInterrupted = true;
        // cancel loading... ?
    }

    boolean mPausing;

    public boolean isCanceled() {
        return mPausing;
    }

    public boolean isPausing() {
        return mPausing;
    }

    public void pause() {
        mPausing = true;
    }

    public void proceed() {
        mPausing = false;
        // FIXME
        mWorking = false;
        if (mTileManager.hasTileJobs())
            go();
    }

    public void awaitPausing() {

    }

    public void start() {
        mPausing = false;
    }

    protected boolean mWorking;
    protected MapTile mTile;

    public void go() {
        if (mWorking)
            return;

        mTile = mTileManager.getTileJob();

        if (mTile == null)
            return;

        try {
            loadTile(mTile);
            mWorking = true;
        } catch (Exception e) {
            e.printStackTrace();
            completed(FAILED);
        }
    }

    public static long lastLoadTime;

    /**
     * Callback to be called by TileDataSource when finished
     * loading or on failure. MUST BE CALLED IN ANY CASE!
     */
    @Override
    public void completed(QueryResult result) {
        long now = MapRenderer.frametime;

        //log.debug("completed {}  diff time:{}", mTile, (now - lastLoadTime));
        lastLoadTime = now;

        mTileManager.jobCompleted(mTile, result);
        mTile = null;

        mWorking = false;

        if (mPausing || !mTileManager.hasTileJobs())
            return;

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                go();
            }
        });
    }

    /**
     * Called by TileDataSource
     */
    @Override
    public void process(MapElement element) {

    }

    /**
     * Called by TileDataSource
     */
    @Override
    public void setTileImage(Bitmap bitmap) {

    }

    public static void postLoadDelay(LoadDelayTask<?> task) {
        Gdx.app.postRunnable(task);
    }

}
