/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 Izumi Kawashima
 * Copyright 2017 Longri
 * Copyright 2017 devemux86
 * Copyright 2017 nebular
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
package org.oscim.layers.marker;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.MercatorProjection;
import org.oscim.core.PointF;
import org.oscim.core.Tile;
import org.oscim.layers.marker.utils.ScreenUtils;
import org.oscim.layers.marker.utils.SparseIntArray;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.bucket.SymbolItem;
import org.oscim.utils.FastMath;
import org.oscim.utils.geom.GeometryUtils;

/**
 * An extension to the MarkerRenderer with item clustering support.
 */
public class ClusterMarkerRenderer extends MarkerRenderer {

    /**
     * Max number to display inside a cluster icon
     */
    private static final int CLUSTER_MAXSIZE = 10;

    /**
     * default color of number inside the icon. Would be super-cool to cook this into the map theme
     */
    private static int CLUSTER_COLORTEXT = 0xff8000c0;

    /**
     * default color of circle background
     */
    private static final int CLUSTER_COLORBACK = 0xffffffff;

    /**
     * Map Cluster Icon Size. This is the biggest size for clusters of CLUSTER_MAXSIZE elements. Smaller clusters will be slightly smaller
     */
    private static final int MAP_MARKER_CLUSTER_SIZE_DP = 64;

    /**
     * Clustering grid square size, decrease to cluster more aggresively. Ideally this value is the typical marker size
     */
    private static final int MAP_GRID_SIZE_DP = 64;

    /**
     * cached bitmaps database, we will cache cluster bitmaps from 1 to MAX_SIZE
     * and always use same bitmap for efficiency
     */
    private static Bitmap[] mClusterBitmaps = new Bitmap[CLUSTER_MAXSIZE + 1];

    private int mStyleBackground = CLUSTER_COLORBACK, mStyleForeground = CLUSTER_COLORTEXT;

    /**
     * Discrete scale step, used to trigger reclustering on significant scale change
     */
    private int mScaleSaved = 0;

    /**
     * We use a flat Sparse array to calculate the clusters. The sparse array models a 2D map where every (x,y) denotes
     * a grid slot, ie. 64x64dp. For efficiency I use a linear sparsearray with ARRindex = SLOTypos * max_x + SLOTxpos"
     */
    private SparseIntArray mGridMap = new SparseIntArray(200); // initial space for 200 markers, that's not a lot of memory, and in most cases will avoid resizing the array

    /**
     * Whether to enable clustering or disable the functionality
     */
    private boolean mClusteringEnabled = false;

    /**
     * Constructs a clustered marker renderer
     *
     * @param markerLayer   The owner layer
     * @param defaultSymbol The default symbol
     * @param style         The desired style, or NULL to disable clustering
     */
    public ClusterMarkerRenderer(MarkerLayer<MarkerInterface> markerLayer, MarkerSymbol defaultSymbol, ClusterMarkerRenderer.ClusterStyle style) {
        super(markerLayer, defaultSymbol);

        mClusteringEnabled = style != null;

        if (mClusteringEnabled) {
            setClusterStyle(style.foreground, style.background);
            for (int k = 0; k <= CLUSTER_MAXSIZE; k++) {
                // cache bitmaps so render thread never creates them
                // we create CLUSTER_MAXSIZE bitmaps. Bigger clusters will show like "+"
                getClusterBitmap(k);
            }
        }
    }

    /**
     * Configures the cluster icon style. This is called by the constructor and cannot be made public because
     * we pre-cache the icons at construction time so the renderer does not have to create them while rendering
     *
     * @param backgroundColor Background color
     * @param foregroundColor text & border color
     */
    private void setClusterStyle(int foregroundColor, int backgroundColor) {
        mStyleBackground = backgroundColor;
        mStyleForeground = foregroundColor;
    }

    @Override
    protected void populate(int size) {
        repopulateCluster(size, mScaleSaved);
    }

    /**
     * Repopulates item list clustering close markers. This is triggered from update() when
     * a significant change in scale has happened.
     *
     * @param size  Item list size
     * @param scale current map scale
     */
    private void repopulateCluster(int size, double scale) {
        /* the grid slot size in px. increase to group more aggressively. currently set to marker size */
        final int GRIDSIZE = ScreenUtils.getPixels(MAP_GRID_SIZE_DP);

		/* the factor to map into Grid Coordinates (discrete squares of GRIDSIZE x GRIDSIZE) */
        final double factor = (scale / GRIDSIZE);

        InternalItem.Clustered[] tmp = new InternalItem.Clustered[size];

        // clear grid map to count items that share the same "grid slot"
        mGridMap.clear();

        for (int i = 0; i < size; i++) {
            InternalItem.Clustered it = tmp[i] = new InternalItem.Clustered();

            it.item = mMarkerLayer.createItem(i);

			/* pre-project points */
            MercatorProjection.project(it.item.getPoint(), mMapPoint);
            it.px = mMapPoint.x;
            it.py = mMapPoint.y;

            // items can be declared non-clusterable
            if (!(it.item instanceof MarkerItem.NonClusterable)) {

                final int
                        absposx = (int) (it.px * factor),             // absolute item X position in the grid
                        absposy = (int) (it.py * factor),             // absolute item Y position
                        maxcols = (int) factor,                       // Grid number of columns
                        itemGridIndex = absposx + absposy * maxcols;  // Index in the sparsearray map

                // we store in the linear sparsearray the index of the marker,
                // ie, index = y * maxcols + x; array[index} = markerIndex

                // Lets check if there's already an item in the grid slot
                final int storedIndexInGridSlot = mGridMap.get(itemGridIndex, -1);

                if (storedIndexInGridSlot == -1) {
                    // no item at that grid position. The grid slot is free so let's
                    // store this item "i" (we identify every item by its InternalItem index)

                    mGridMap.put(itemGridIndex, i);
                    //Log.v(TAG, "UNclustered item at " + itemGridIndex);
                } else {
                    // at that grid position there's already a marker index
                    // mark this item as clustered out, so it will be skipped in the update() call

                    it.clusteredOut = true;

                    // and increment the count on its "parent" that will from now on act as a cluster
                    tmp[storedIndexInGridSlot].clusterSize++;

                    //Log.v(TAG, "Clustered item at " + itemGridIndex + ", \'parent\' size " + (tmp[storedIndexInGridSlot].clusterSize));
                }
            }
        }

        /* All ready for update. */
        synchronized (this) {
            mUpdate = true;
            mItems = tmp;
        }
    }

    @Override
    public synchronized void update(GLViewport v) {
        final double scale = Tile.SIZE * v.pos.scale;

        if (mClusteringEnabled) {
            /*
              Clustering check: If clustering is enabled and there's been a significant scale change
              trigger repopulation and return. After repopulation, this will be called again
             */

            // (int) log of scale gives us adequate steps to trigger clustering
            int scalepow = FastMath.log2((int) scale);

            if (scalepow != mScaleSaved) {
                mScaleSaved = scalepow;

                // post repopulation to the main thread
                mMarkerLayer.map().post(new Runnable() {
                    @Override
                    public void run() {
                        repopulateCluster(mItems.length, scale);
                    }
                });

                // and get out of here
                return;
            }
        }

        if (!v.changed() && !mUpdate)
            return;

        mUpdate = false;

        double mx = v.pos.x;
        double my = v.pos.y;

        //int changesInvisible = 0;
        //int changedVisible = 0;
        int numVisible = 0;

        mMarkerLayer.map().viewport().getMapExtents(mBox, mExtents);

        long flip = (long) (Tile.SIZE * v.pos.scale) >> 1;

        if (mItems == null) {
            if (buckets.get() != null) {
                buckets.clear();
                compile();
            }
            return;
        }

        double angle = Math.toRadians(v.pos.bearing);
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

		/* check visibility */
        for (InternalItem itm : mItems) {
            InternalItem.Clustered it = (InternalItem.Clustered) itm;

            it.changes = false;
            it.x = (float) ((it.px - mx) * scale);
            it.y = (float) ((it.py - my) * scale);

            if (it.x > flip)
                it.x -= (flip << 1);
            else if (it.x < -flip)
                it.x += (flip << 1);

            if ((it.clusteredOut) || (!GeometryUtils.pointInPoly(it.x, it.y, mBox, 8, 0))) {
                // either properly invisible, or clustered out. Items marked as clustered out mean there's another item
                // on the same-ish position that will be promoted to cluster marker, so this particular item is considered
                // invisible

                if (it.visible && (!it.clusteredOut)) {
                    // it was previously visible, but now it won't
                    it.changes = true;
                    // changes to invible
                    //changesInvisible++;
                }
                continue;
            }

            // item IS definitely visible
            it.dy = sin * it.x + cos * it.y;

            if (!it.visible) {
                it.visible = true;
                //changedVisible++;
            }
            numVisible++;
        }

        //log.debug(numVisible + " " + changedVisible + " " + changesInvisible);

		/* only update when zoomlevel changed, new items are visible
         * or more than 10 of the current items became invisible */
        //if ((numVisible == 0) && (changedVisible == 0 && changesInvisible < 10))
        //	return;
        buckets.clear();

        if (numVisible == 0) {
            compile();
            return;
        }
        /* keep position for current state */
        mMapPosition.copy(v.pos);
        mMapPosition.bearing = -mMapPosition.bearing;

        // why do we sort ? z-index?
        sort(mItems, 0, mItems.length);
        //log.debug(Arrays.toString(mItems));

        for (InternalItem itm : mItems) {
            InternalItem.Clustered it = (InternalItem.Clustered) itm;

            // skip invisible AND clustered-out
            if ((!it.visible) || (it.clusteredOut))
                continue;

            if (it.changes) {
                it.visible = false;
                continue;
            }

            SymbolItem s = SymbolItem.pool.get();

            if (it.clusterSize > 0) {
                // this item will act as a cluster, just use a proper bitmap
                // depending on cluster size, instead of its marker

                Bitmap bitmap = getClusterBitmap(it.clusterSize + 1);
                s.set(it.x, it.y, bitmap, true);
                s.offset = new PointF(0.5f, 0.5f);
                s.billboard = true; // could be a parameter

            } else {
                // normal item, use its marker

                MarkerSymbol symbol = it.item.getMarker();

                if (symbol == null)
                    symbol = mDefaultMarker;

                s.set(it.x, it.y, symbol.getBitmap(), true);
                s.offset = symbol.getHotspot();
                s.billboard = symbol.isBillboard();
            }

            mSymbolLayer.pushSymbol(s);
        }

        buckets.set(mSymbolLayer);
        buckets.prepare();

        compile();
    }


    /**
     * Gets a bitmap for a given cluster size
     *
     * @param size The cluster size. Can be greater than CLUSTER_MAXSIZE.
     * @return A somewhat cool bitmap to be used as the cluster marker
     */
    private Bitmap getClusterBitmap(int size) {
        final String strValue;

        if (size >= CLUSTER_MAXSIZE) {
            // restrict cluster indicator size. Bigger clusters will show as "+" instead of ie. "45"
            size = CLUSTER_MAXSIZE;
            strValue = "+";
        } else {
            strValue = String.valueOf(size);
        }

        // return cached bitmap if exists. cache hit !
        if (mClusterBitmaps[size] != null)
            return mClusterBitmaps[size];

        // create and cache bitmap. This is unacceptable inside the GL thread,
        // so we'll call this routine at the beginning to pre-cache all bitmaps

        ScreenUtils.ClusterDrawable drawable = new ScreenUtils.ClusterDrawable(
                MAP_MARKER_CLUSTER_SIZE_DP - CLUSTER_MAXSIZE + size, // make size dependent on cluster size
                mStyleForeground,
                mStyleBackground,
                strValue
        );

        mClusterBitmaps[size] = drawable.getBitmap();
        return mClusterBitmaps[size];
    }

    /**
     * Class to wrap the cluster icon style properties
     */
    public static class ClusterStyle {
        final int background;
        final int foreground;

        /**
         * Creates the Cluster style
         *
         * @param fore Foreground (border and text) color
         * @param back Background (circle) color
         */

        public ClusterStyle(int fore, int back) {
            foreground = fore;
            background = back;
        }
    }

    /**
     * Convenience method for instantiating this renderer via a factory, so the layer construction semantic is more pleasing to the eye
     *
     * @param defaultSymbol Default symbol to use if the Marker is not assigned a symbol
     * @param style         Cluster icon style, or NULL to disable clustering functionality
     * @return A factory to be passed to the ItemizedLayer constructor in order to enable the cluster functionality
     */
    public static MarkerRendererFactory factory(final MarkerSymbol defaultSymbol, final ClusterStyle style) {
        return new MarkerRendererFactory() {
            @Override
            public MarkerRenderer create(MarkerLayer markerLayer) {
                return new ClusterMarkerRenderer(markerLayer, defaultSymbol, style);
            }
        };
    }
}
