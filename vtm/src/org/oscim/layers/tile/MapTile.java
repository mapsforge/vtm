/*
 * Copyright 2012, 2013 Hannes Janetzek
 * Copyright 2017-2018 devemux86
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General License for more details.
 *
 * You should have received a copy of the GNU Lesser General License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.layers.tile;

import org.oscim.core.MercatorProjection;
import org.oscim.core.Tile;
import org.oscim.layers.tile.vector.VectorTileLoader;
import org.oscim.layers.tile.vector.labeling.LabelTileLoaderHook;
import org.oscim.renderer.bucket.RenderBuckets;
import org.oscim.utils.pool.Inlist;
import org.oscim.utils.quadtree.TileIndex;
import org.oscim.utils.quadtree.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.oscim.layers.tile.MapTile.State.CANCEL;
import static org.oscim.layers.tile.MapTile.State.DEADBEEF;
import static org.oscim.layers.tile.MapTile.State.LOADING;
import static org.oscim.layers.tile.MapTile.State.NEW_DATA;
import static org.oscim.layers.tile.MapTile.State.NONE;
import static org.oscim.layers.tile.MapTile.State.READY;

/**
 * Extends Tile class to hold state and data.
 * <p/>
 * Used concurrently in: TileManager (Main Thread), TileLoader (Worker Thread)
 * and TileRenderer (GL Thread).
 */
public class MapTile extends Tile {

    static final Logger log = LoggerFactory.getLogger(MapTile.class);

    public static class TileNode extends TreeNode<TileNode, MapTile> {
    }

    public static final class State {
        public static final byte NONE = (1 << 0);

        /**
         * STATE_LOADING means the tile is about to be loaded / loading.
         * Tile belongs to TileLoader thread.
         */
        public static final byte LOADING = (1 << 1);

        /**
         * STATE_NEW_DATA: tile data is prepared for rendering.
         * While 'locked' it belongs to GL Thread.
         */
        public static final byte NEW_DATA = (1 << 2);

        /**
         * STATE_READY: tile data is uploaded to GL.
         * While 'locked' it belongs to GL Thread.
         */
        public static final byte READY = (1 << 3);

        /**
         * STATE_CANCEL: tile is removed from TileManager,
         * but may still be processed by TileLoader.
         */
        public static final byte CANCEL = (1 << 4);

        /**
         * Dont touch if you find some.
         */
        public static final byte DEADBEEF = (1 << 6);
    }

    public static final int PROXY_CHILD00 = (1 << 0);
    public static final int PROXY_CHILD01 = (1 << 1);
    public static final int PROXY_CHILD10 = (1 << 2);
    public static final int PROXY_CHILD11 = (1 << 3);
    public static final int PROXY_PARENT = (1 << 4);
    public static final int PROXY_GRAMPA = (1 << 5);
    public static final int PROXY_HOLDER = (1 << 6);

    /**
     * Tile state
     */
    byte state = State.NONE;

    /**
     * absolute tile coordinates: tileX,Y / Math.pow(2, zoomLevel)
     */
    public final double x;
    public final double y;

    /**
     * List of TileData for rendering. ElementLayers is always at first
     * position (for VectorTileLayer). TileLoaderHooks may add additional
     * data. See e.g. {@link LabelTileLoaderHook}.
     */
    public TileData data;

    /**
     * Pointer to access relatives in {@link TileIndex}
     */
    public final TileNode node;

    /**
     * current distance from map center
     */
    public float distance;

    /**
     * Tile is in view region. Set by TileRenderer.
     */
    public boolean isVisible;

    /**
     * Used for fade-effects
     */
    public long fadeTime;

    /**
     * Used to avoid drawing a tile twice per frame
     */
    int lastDraw = 0;

    /**
     * Keep track which tiles are locked as proxy for this tile
     */
    private int proxy = 0;

    /**
     * Tile lock counter, synced in TileManager
     */
    private int locked = 0;

    private int refs = 0;

    /**
     * Only used GLRenderer when this tile sits in for another tile.
     * e.g. x:-1,y:0,z:1 for x:1,y:0
     */
    MapTile holder;

    public abstract static class TileData extends Inlist<TileData> {
        Object id;

        protected abstract void dispose();

        @Override
        public TileData next() {
            return (TileData) next;
        }
    }

    public MapTile(int tileX, int tileY, int zoomLevel) {
        this(null, tileX, tileY, zoomLevel);
    }

    public MapTile(TileNode node, int tileX, int tileY, int zoomLevel) {
        super(tileX, tileY, (byte) zoomLevel);
        this.x = (double) tileX / (1 << zoomLevel);
        this.y = (double) tileY / (1 << zoomLevel);
        this.node = node;
    }

    public boolean state(int testState) {
        return (state & testState) != 0;
    }

    public int getState() {
        return state;
    }

    /**
     * @return true when tile could be used by another thread.
     */
    boolean isLocked() {
        return locked > 0 || refs > 0;
    }

    /**
     * Set this tile to be locked, i.e. to no be modified or cleared
     * while rendering. Renderable parent, grand-parent and children
     * will also be locked. Dont forget to unlock when tile is not longer
     * used. This function should only be called through {@link TileManager}
     */
    void lock() {
        if (state == DEADBEEF) {
            log.debug("Locking dead tile {}", this);
            return;
        }

        if (locked++ > 0)
            return;

        MapTile p;
        /* lock all tiles that could serve as proxy */
        for (int i = 0; i < 4; i++) {
            p = node.child(i);
            if (p == null)
                continue;

            if (p.state(READY | NEW_DATA)) {
                proxy |= (1 << i);
                p.refs++;
            }
        }

        if (node.isRoot())
            return;

        p = node.parent();
        if (p != null && p.state(READY | NEW_DATA)) {
            proxy |= PROXY_PARENT;
            p.refs++;
        }

        if (node.parent.isRoot())
            return;

        p = node.parent.parent();
        if (p != null && p.state(READY | NEW_DATA)) {
            proxy |= PROXY_GRAMPA;
            p.refs++;
        }
    }

    /**
     * Unlocks this tile when it cannot be used by render-thread.
     */
    void unlock() {
        if (--locked > 0)
            return;

        if ((proxy & PROXY_PARENT) != 0) {
            MapTile p = node.parent();
            if (p != null)
                p.refs--;
        }

        if ((proxy & PROXY_GRAMPA) != 0) {
            MapTile p = node.parent.parent();
            if (p != null)
                p.refs--;
        }

        for (int i = 0; i < 4; i++) {
            if ((proxy & (1 << i)) != 0) {
                MapTile c = node.child(i);
                if (c != null)
                    c.refs--;
            }
        }

        /* removed all proxy references for this tile */
        proxy = 0;

        if (state == DEADBEEF) {
            log.debug("Unlock dead tile {}", this);
            clear();
        }
    }

    /**
     * @return true if tile is state is not NONE.
     */
    public boolean isActive() {
        return state > State.NONE;
    }

    /**
     * Test whether it is save to access a proxy item
     * through this.node.*
     */
    public boolean hasProxy(int proxy) {
        return (this.proxy & proxy) != 0;
    }

    /**
     * CAUTION: This function may only be called
     * by {@link TileManager}
     */
    protected void clear() {
        while (data != null) {
            data.dispose();
            data = data.next;
        }
        setState(NONE);
    }

    /**
     * Get the default ElementLayers which are added
     * by {@link VectorTileLoader}
     */
    public RenderBuckets getBuckets() {
        if (!(data instanceof RenderBuckets))
            return null;

        return (RenderBuckets) data;
    }

    public TileData getData(Object id) {
        for (TileData d = data; d != null; d = d.next)
            if (d.id == id)
                return d;
        return null;
    }

    public void addData(Object id, TileData td) {
        /* keeping ElementLayers at position 0! */
        td.id = id;
        if (data != null) {
            td.next = data.next;
            data.next = td;
        } else {
            data = td;
        }
    }

    public TileData removeData(Object id) {
        if (data == null)
            return null;

        TileData prev = data;
        if (data.id == id) {
            data = data.next;
            return prev;
        }
        for (TileData d = data.next; d != null; d = d.next) {
            if (d.id == id) {
                prev.next = d.next;
                return d;
            }
            prev = d;
        }
        return null;
    }

    public static int depthOffset(MapTile t) {
        return ((t.tileX % 4) + (t.tileY % 4 * 4) + 1);
    }

    /**
     * @return the corresponding ground scale
     */
    public float getGroundScale() {
        double lat = MercatorProjection.toLatitude(this.y);
        return (float) MercatorProjection
                .groundResolutionWithScale(lat, 1 << this.zoomLevel);
    }

    public MapTile getProxyChild(int id, byte state) {
        if ((proxy & (1 << id)) == 0)
            return null;

        MapTile child = node.child(id);
        if (child == null || (child.state & state) == 0)
            return null;

        return child;
    }

    public MapTile getParent() {
        if ((proxy & PROXY_PARENT) == 0)
            return null;

        return node.parent.item;
    }

    public MapTile getProxy(int proxy, byte state) {

        if ((this.proxy & proxy) == 0)
            return null;

        MapTile p = null;
        switch (proxy) {
            case PROXY_CHILD00:
                p = node.child(0);
                break;
            case PROXY_CHILD01:
                p = node.child(1);
                break;
            case PROXY_CHILD10:
                p = node.child(2);
                break;
            case PROXY_CHILD11:
                p = node.child(3);
                break;
            case PROXY_PARENT:
                p = node.parent();
                break;
            case PROXY_GRAMPA:
                p = node.parent.parent();
                break;
            case PROXY_HOLDER:
                p = holder;
                break;
        }

        if (p == null || (p.state & state) == 0)
            return null;

        return p;
    }

    public String state() {
        switch (state) {
            case State.NONE:
                return "None";
            case State.LOADING:
                return "Loading";
            case State.NEW_DATA:
                return "Data";
            case State.READY:
                return "Ready";
            case State.CANCEL:
                return "Cancel";
            case State.DEADBEEF:
                return "Dead";
        }
        return "";
    }

    public synchronized void setState(byte newState) {
        if (state == newState)
            return;

        /* Renderer could have uploaded the tile while the layer
         * was cleared. This prevents to set tile to READY state. */
        /* All other state changes are on the main-thread. */
        if (state == DEADBEEF)
            return;

        switch (newState) {
            case NONE:
                state = newState;
                return;

            case LOADING:
                if (state == NONE) {
                    state = newState;
                    return;
                }
                throw new IllegalStateException("Loading"
                        + " <= " + state() + " " + this);
            case NEW_DATA:
                if (state == LOADING) {
                    state = newState;
                    return;
                }
                throw new IllegalStateException("NewData"
                        + " <= " + state() + " " + this);

            case READY:
                if (state == NEW_DATA) {
                    state = newState;
                    return;
                }
                throw new IllegalStateException("Ready"
                        + " <= " + state() + " " + this);

            case CANCEL:
                if (state == LOADING) {
                    state = newState;
                    return;
                }
                throw new IllegalStateException("Cancel" +
                        " <= " + state() + " " + this);
            case DEADBEEF:
                state = newState;
                return;
        }
    }
}
